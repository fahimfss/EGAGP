package FGPGA;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FGPGA_GA {

    static long time, tTime=0;
    public static final String FILE = "Result_FGPGA.txt";
    static void setTime(){
        time = System.currentTimeMillis();
    }
    static void printTime(String no){
        long cTime = System.currentTimeMillis();
        System.out.println(no + ": " + ((cTime - time)/1000.0) + "s");
        tTime += ((cTime - time)/1000.0);
        time = cTime;
    }

    public static void main(String[] args) {
        try {
            FileWriter fw = new FileWriter(FILE, false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            out.println("fileName\t" +
                    "Number of Vertex\t" +
                    "Number of Machines\t" +
                    "Total Time\t" +
                    "Average Fitness\t" +
                    "Best Fitness\t"
            );

            out.close(); bw.close(); fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        double totalAvg = 0, avg, minimum, totalMinimum = 0;
        long startTime;

        int iterations=10, generationLimit;

        for(int file=1; file<=9; file++) {
            for (int index = 0; index < 1; index++) {
                startTime = System.currentTimeMillis();
                String fileName = "G" + file + "00_" + index + ".txt";
                System.out.println(fileName);
                avg = 0;
                minimum = Double.MAX_VALUE;
                setTime();
                FGPGA_FitnessCalc.extractData("./Data/MyDataSet/" + fileName);

                if(file<=5)generationLimit = 6000;
                else generationLimit = 3000;
                System.out.println("Iterations: ");

                for (int r = 0; r < iterations; r++) {
                    System.out.print(r + ". ");

                    FGPGA_Population myPop = new FGPGA_Population(20, true);

                    int generationCount = 0;
                    double currentBest = -1.0, bestFitness, prevBest;


                    do {
                        generationCount++;
                        if (generationCount % 50 == 0) {
                            myPop = FGPGA_Algorithm.removeTwins(myPop);
                        }
                        myPop = FGPGA_Algorithm.evolvePopulation(myPop);

                        bestFitness = myPop.getFittest().getFitness();

                        if (generationCount % 100 == 0) {
                            if (currentBest < 0) {
                                currentBest = bestFitness;
                            } else {
                                prevBest = currentBest;
                                currentBest = bestFitness;
                                if (currentBest == prevBest) {
                                    myPop = FGPGA_Algorithm.randomRestart(myPop);
                                }
                            }
                        }
                    }
                    while (generationCount < generationLimit && bestFitness > 0.0);

                    if (myPop.getFittest().isValid() == FGPGA_Individual.INVALID) {
                        System.out.println("Produced result was invalid!");
                        System.exit(-1);
                    }

                    bestFitness = myPop.getFittest().getFitness();
                    System.out.println("Fitness: "+bestFitness);

                    double mnCost = bestFitness;
                    if (mnCost < minimum) {
                        minimum = mnCost;
                    }
                    avg += mnCost;
                }

                System.out.println();
                printTime("\nTime");
                System.out.println("Number of Vertex: " + FGPGA_FitnessCalc.numberOfVertex);
                System.out.println("Number of Machines: " + FGPGA_FitnessCalc.numberOfMachines);
                System.out.println("Avg Fitness: " + avg / iterations);
                System.out.println("Min Fitness: " + minimum);
                System.out.println();
                totalAvg += (avg / iterations);
                totalMinimum += minimum;

                try {
                    FileWriter fw = new FileWriter(FILE, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw);

                    out.println(fileName+"\t" +
                            FGPGA_FitnessCalc.numberOfVertex+"\t" +
                            FGPGA_FitnessCalc.numberOfMachines+"\t" +
                            ((System.currentTimeMillis() - startTime) / 1000)+"\t" +
                            (avg / iterations)+"\t" +
                            minimum +"\t"
                    );
//                    out.println("Time: " + (System.currentTimeMillis() - startTime) / 1000 + "s");
//                    out.println("Avg Fitness: " + avg / iterations);
//                    out.println("Min Fitness: " + minimum);
//                    out.println("Number of Vertex: " + FGPGA_FitnessCalc.numberOfVertex);
//                    out.println("Number of Machines: " + FGPGA_FitnessCalc.numberOfMachines);
//                    out.println();

                    out.close();
                    bw.close();
                    fw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}