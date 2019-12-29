//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//
//public class GA {
//    static boolean useBestGene = true;
//    private static final int NO_OF_THREADS = 8;
//    private static final String FILE = "Result_EGAGP.txt";
//
//    public static void main(String[] args) {
//        int noOfCycles = 2;
//
//        int secondaryPopulationCount = NO_OF_THREADS * noOfCycles;
//
//        int secondaryPopulationSize = 20;
//        int primaryPopulationSize = 50;
//
//        int secondaryPopulationGenerations = 50000;
//        int primaryPopulationGenerations = 10000;
//
//        try {
//            FileWriter fw1 = new FileWriter(FILE, false);
//            BufferedWriter bw1 = new BufferedWriter(fw1);
//            PrintWriter out1 = new PrintWriter(bw1);
//
//            out1.println("noOfCycles = " + noOfCycles);
//            out1.println("secondaryPopulationCount = " + secondaryPopulationCount);
//            out1.println("secondaryPopulationSize = " + secondaryPopulationSize);
//            out1.println("primaryPopulationSize = " + primaryPopulationSize);
//            out1.println("secondaryPopulationGenerations = " + secondaryPopulationGenerations);
//            out1.println("primaryPopulationGenerations = " + primaryPopulationGenerations);
//
//            out1.println();
//
//            out1.printf(String.format("%30s%30s%30s%30s%30s",
//                    "fileName", "Number of Vertex", "Number of Machines", "Total Time", "Fitness"));
//
//            out1.println();
//
//            out1.close();
//            bw1.close();
//            fw1.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        int restartAfter2 = 1000;
//
//        for (int file = 2; file <= 2; file++) {
//            for (int index = 0; index < 1; index++) {
//                long startTime = System.currentTimeMillis(), totalTime;
//                String fileName = "G" + file + "00_" + index + ".txt";
//                System.out.println(fileName);
//                DataExtract.extractData("./Data/MyDataSet/" + fileName);
//
//                Population primaryPopulation = new Population(primaryPopulationSize, true);
//
//                int restartAfter1 = 100;
//
//                ArrayList<Individual> individuals = new ArrayList<>(Collections.nCopies(secondaryPopulationCount, null));
//                ArrayList<Thread> threads = new ArrayList<>(Collections.nCopies(secondaryPopulationCount, null));
//
//                for(int ite = 0; ite < secondaryPopulationCount; ite++){
//                    Population secondaryPopulation = new Population(secondaryPopulationSize, true);
//                    Algorithm a = new Algorithm(secondaryPopulation, secondaryPopulationGenerations, restartAfter1,
//                            individuals, ite);
//                    Thread t = new Thread(a);
//                    threads.set(ite, t);
//                }
//                for(int i = 0; i < threads.size(); i+=NO_OF_THREADS) {
//                    for(int j = i; j < i + NO_OF_THREADS && j < threads.size(); j++){
//                        threads.get(j).start();
//                    }
//                    for(int j = i; j < i + NO_OF_THREADS && j < threads.size(); j++){
//                        try {
//                            threads.get(j).join();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                individuals.sort(Comparator.comparingDouble(o -> o.getFitness(true)));
//
//                for(int i = 0; i < primaryPopulation.size() && i < individuals.size(); i++){
//                    Individual individual = individuals.get(i);
//                    if (individual.isValidResetUsedCapacities() == Individual.INVALID) {
//                        System.out.println("Produced result was invalid!");
//                        System.exit(-1);
//                    }
//
//                    System.out.println("Fitness: " + individual.getFitness(false));
//                    primaryPopulation.saveIndividual(i, individual);
//                }
//
//                Algorithm algorithm = new Algorithm(primaryPopulation, primaryPopulationGenerations,
//                        restartAfter2, null, -1);
//
//                algorithm.run();
//                primaryPopulation = algorithm.getPopulation();
//
//                double bestFitness = primaryPopulation.getFittest().getFitness(false);
//                System.out.println();
//                totalTime = (System.currentTimeMillis() - startTime)/1000;
//
//                String toPrint = String.format("%30s%30s%30s%30s%30s",
//                        "" + fileName, "" + DataExtract.numberOfVertex, "" + DataExtract.numberOfMachines,
//                        "" + totalTime, "" + bestFitness);
//                System.out.println(toPrint);
//
//                try {
//                    FileWriter fw = new FileWriter(FILE, true);
//                    BufferedWriter bw = new BufferedWriter(fw);
//
//                    bw.write(toPrint);
//                    bw.newLine();
//                    bw.flush();
//
//                    bw.close();
//                    fw.close();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }
//}