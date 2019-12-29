import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GAWithTiers {
    static boolean useBestGene = true;
    private static final int NO_OF_THREADS = 8;               // No of threads in the cpu where the code is run
    private static final int INDIVIDUALS_PER_POPULATION = NO_OF_THREADS;
    private static final int TIER_3_POPULATION_COUNT = NO_OF_THREADS;

    private static final int  TIER_1_GENERATION_LIMIT = 2000;
    private static final int  TIER_2_GENERATION_LIMIT = TIER_1_GENERATION_LIMIT * 8;
    private static final int  TIER_3_GENERATION_LIMIT = TIER_2_GENERATION_LIMIT * 8;

    private static final int  TIER_1_RESTART_AFTER = 100;
    private static final int  TIER_2_RESTART_AFTER = 1000;
    private static final int  TIER_3_RESTART_AFTER = 10000;

    private static final String FILE = "Result_EGAGP_TIER.txt";

    private static void runAlgorithmsInThread(ArrayList <Thread> threads){
        for(int i = 0; i < threads.size(); i += NO_OF_THREADS) {
            for(int j = i; j < i + NO_OF_THREADS && j < threads.size(); j++){
                threads.get(j).start();
            }
            for(int j = i; j < i + NO_OF_THREADS && j < threads.size(); j++){
                try {
                    threads.get(j).join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Population getUpperTierPopulationFromLowerTier(ArrayList<Population> lowerTierPopulations,
                                                                 int generationLimit, int restartAfter){
        Population upperTierPopulation = new Population(lowerTierPopulations.size(), false);

        ArrayList<Individual> bestIndividuals = new ArrayList<>(Collections.nCopies(lowerTierPopulations.size(), null));
        ArrayList<Thread> threads = new ArrayList<>();


        for(int i = 0; i < lowerTierPopulations.size(); i++){
            Algorithm a = new Algorithm(lowerTierPopulations.get(i), generationLimit, restartAfter, bestIndividuals, i);
            Thread t = new Thread(a);
            threads.add(t);
        }

        runAlgorithmsInThread(threads);

        for(int i = 0; i < lowerTierPopulations.size(); i++){
            upperTierPopulation.saveIndividual(i, bestIndividuals.get(i));
        }

        return  upperTierPopulation;
    }

    public static void main(String[] args) {
        try {
            FileWriter fw1 = new FileWriter(FILE, false);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            PrintWriter out1 = new PrintWriter(bw1);

            out1.println("Individuals per population = " + INDIVIDUALS_PER_POPULATION);
            out1.println("Tier 1 Population Count = " + TIER_3_POPULATION_COUNT * INDIVIDUALS_PER_POPULATION * INDIVIDUALS_PER_POPULATION);
            out1.println("Tier 2 Population Count = " + TIER_3_POPULATION_COUNT * INDIVIDUALS_PER_POPULATION);
            out1.println("Tier 3 Population Count = " + TIER_3_POPULATION_COUNT);

            out1.println();

            out1.printf(String.format("%30s%30s%30s%30s%30s",
                    "fileName", "Number of Vertex", "Number of Machines", "Total Time", "Fitness"));

            out1.println();

            out1.close();
            bw1.close();
            fw1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int file = 2; file <= 2; file++) {
            for (int index = 0; index < 1; index++) {
                long startTime = System.currentTimeMillis(), totalTime;
                String fileName = "G" + file + "00_" + index + ".txt";
                System.out.println(fileName);
                DataExtract.extractData("./Data/MyDataSet/" + fileName);

                ArrayList<Population> tier3Populations = new ArrayList<>();
                for (int itTier3 = 0; itTier3 < TIER_3_POPULATION_COUNT; itTier3++) {
                    System.out.println("Tier 3 iteration " + itTier3);

                    ArrayList<Population> tier2Populations = new ArrayList<>();
                    for (int itTier2 = 0; itTier2 < INDIVIDUALS_PER_POPULATION; itTier2++) {

                        ArrayList<Population> tier1Populations = new ArrayList<>();
                        for (int itTier1 = 0; itTier1 < INDIVIDUALS_PER_POPULATION; itTier1++) {
                            tier1Populations.add(itTier1, new Population(INDIVIDUALS_PER_POPULATION, true));
                        }
                        Population population = getUpperTierPopulationFromLowerTier(tier1Populations, TIER_1_GENERATION_LIMIT, TIER_1_RESTART_AFTER);
                        tier2Populations.add(population);

                    }

                    Population population = getUpperTierPopulationFromLowerTier(tier2Populations, TIER_2_GENERATION_LIMIT, TIER_2_RESTART_AFTER);
                    tier3Populations.add(population);
                }

                Population population = getUpperTierPopulationFromLowerTier(tier3Populations, TIER_3_GENERATION_LIMIT, TIER_3_RESTART_AFTER);
                Individual bestIndividual = population.getFittest();

                double bestFitness = bestIndividual.getFitness(false);
                System.out.println();
                totalTime = (System.currentTimeMillis() - startTime)/1000;

                String toPrint = String.format("%30s%30s%30s%30s%30s",
                        "" + fileName, "" + DataExtract.numberOfVertex, "" + DataExtract.numberOfMachines,
                        "" + totalTime, "" + bestFitness);
                System.out.println(toPrint);

                try {
                    FileWriter fw = new FileWriter(FILE, true);
                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.write(toPrint);
                    bw.newLine();
                    bw.flush();

                    bw.close();
                    fw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}