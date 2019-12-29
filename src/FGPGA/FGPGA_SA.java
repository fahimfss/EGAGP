package FGPGA;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * @author Itukitu
 */

public class FGPGA_SA {
    public static double[][] Gain;
    public static double[] Free;
    //public static int moveVertex;
    //public static int moveMachine;
    public static ArrayList<Integer> bestMoves;
    public static ArrayList<Integer> bestMachines;
    public static double maxGain;
    public static double delta1;
    public static int deltacount;

    public static void calculateFree(FGPGA_Individual me) {
        for (int m = 0; m < FGPGA_FitnessCalc.numberOfMachines; m++)
            Free[m] = FGPGA_FitnessCalc.M[m];
        for (int i = 0; i < FGPGA_FitnessCalc.numberOfVertex; i++) {
            Free[me.getGene(i)] = Free[me.getGene(i)] - FGPGA_FitnessCalc.W[i];
        }
    }

    public static void calculateGain(FGPGA_Individual me) {
        maxGain = Double.NEGATIVE_INFINITY;
        //moveVertex=-1;
        //moveMachine=-1;
        double myFitness = me.getGraphCutCost();
        delta1 = 0;
        deltacount = 0;
        for (int i = 0; i < FGPGA_FitnessCalc.numberOfVertex; i++)
            for (int m = 0; m < FGPGA_FitnessCalc.numberOfMachines; m++) {
                FGPGA_Individual you = new FGPGA_Individual(me);
                you.setGene(i, m);
                Gain[i][m] = myFitness - you.getGraphCutCost();
                if (Gain[i][m] < 0) {
                    delta1 += Gain[i][m];
                    deltacount++;
                }
                if (Gain[i][m] >= maxGain) {
                    if (Gain[i][m] > maxGain) {
                        bestMoves.clear();
                        bestMachines.clear();
                        maxGain = Gain[i][m];
                    }
                    bestMoves.add(i);
                    bestMachines.add(m);

                    //moveVertex=i;
                    //moveMachine=m;
                }
            }
        delta1 = delta1 / deltacount;
    }

    public static void main(String[] args) {
        long st;
        double min, avg;
        for (int fileNo = 1; fileNo <= 9; fileNo++) {
            st = System.currentTimeMillis();
            min = Double.MAX_VALUE;
            avg = 0;

            String file = "G" + fileNo + "00.txt";
            FGPGA_FitnessCalc.extractData("D:\\Thesis\\Data\\MyDataSet\\" + file);
            BufferedWriter bw = null;

            for (int ite = 0; ite < 10; ite++) {
                bestMoves = new ArrayList<Integer>();
                bestMachines = new ArrayList<Integer>();

                Gain = new double[FGPGA_FitnessCalc.numberOfVertex][FGPGA_FitnessCalc.numberOfMachines];
                Free = new double[FGPGA_FitnessCalc.numberOfMachines];
                FGPGA_Individual initPartition = new FGPGA_Individual();
                // initial candidate according to gp paper
                double Mm[] = new double[FGPGA_FitnessCalc.numberOfMachines];
                for (int m = 0; m < FGPGA_FitnessCalc.numberOfMachines; m++)
                    Mm[m] = FGPGA_FitnessCalc.M[m];
                for (int i = 0; i < FGPGA_FitnessCalc.numberOfVertex; i++) {
                    for (int m = 0; m < FGPGA_FitnessCalc.numberOfMachines; m++) {
                        if (FGPGA_FitnessCalc.W[i] <= Mm[m]) {
                            initPartition.setGene(i, m);
                            Mm[m] = Mm[m] - FGPGA_FitnessCalc.W[i];
                            break;

                        }
                    }
                }
                // print initial fitness
                System.out.println(initPartition.getGraphCutCost());
                System.out.println(initPartition);

                int I = 5;
                double Fmin = 0.02;
                //int tCount=0;
                // now the Simulated annealing algorithm
                FGPGA_Individual best = initPartition;
                FGPGA_Individual current = initPartition;
                double bestFitness = initPartition.getGraphCutCost();
                double currentFitness = initPartition.getGraphCutCost();

                calculateGain(current);
                calculateFree(current);

                double P1 = 0.627;
                double initT = -delta1 / P1;
                //System.out.println(delta1);
                int epochLength = FGPGA_FitnessCalc.numberOfVertex;//*(FGPGA_FitnessCalc.numberOfMachines-1);
                System.out.println(epochLength);
                double alpha = 0.908;
                double temperature = initT; // initial temperature

                calculateFree(best);

                long startTime = System.currentTimeMillis();
                long endTime = System.currentTimeMillis();
                long totalTime = 1000 * 600;
                while (/*tCount<I*/(endTime - startTime) <= totalTime && bestFitness != 0) {
                    //System.out.println(temperature+" "+bestFitness);
                    int counter = 0;
                    //int acceptedMove=0;
                    while (counter <= epochLength && bestFitness != 0 && (endTime - startTime) <= totalTime) {
                        endTime = System.currentTimeMillis();
                        if (counter % 10 == 0) {
//                            try {
//                                bw.write(String.valueOf(bestFitness));
//                                bw.newLine();
//                                bw.flush();
//                            } catch (IOException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
                            System.out.println(counter + ":" + bestFitness);


                        }
                        //if(counter%100==0)

                        counter++;
                        calculateGain(current);
                        calculateFree(current);
                        int moveVertex = -1;
                        int moveMachine = -1;
                        if (!bestMoves.isEmpty()) {
                            int random = (int) Math.floor((Math.random() * (bestMoves.size() - 1)));
                            moveVertex = bestMoves.get(random);
                            moveMachine = bestMachines.get(random);
                        }
                        //System.out.println("max:"+maxGain+" move:"+moveVertex+" machine:"+moveMachine+" prev:"+current.getGene(moveVertex));
                        if (maxGain >= 0) {
                            if (Free[moveMachine] >= FGPGA_FitnessCalc.W[moveVertex]) {
                                current.setGene(moveVertex, moveMachine);
                                //acceptedMove++;
                            } else {
                                double p = Math.exp(Free[moveMachine] - FGPGA_FitnessCalc.W[moveVertex] / temperature);
                                moveVertex = (int) Math.floor(Math.random() * (FGPGA_FitnessCalc.numberOfVertex - 1));
                                moveMachine = (int) Math.floor(Math.random() * (FGPGA_FitnessCalc.numberOfMachines - 1));
                                if (Free[moveMachine] >= FGPGA_FitnessCalc.W[moveVertex]) {
                                    /// probabilistic move
                                    double random = Math.random();
                                    if (random < p) {
                                        current.setGene(moveVertex, moveMachine);
                                        //acceptedMove++;
                                    }
                                }


                            }
					/*else
                    {

                        /// probabilistic move
                            double random=Math.random();
                            double p=Math.exp((Free[moveMachine]-FGPGA_FitnessCalc.W[moveVertex])/temperature);
                            if(random<p)
                            {
                                current.setGene(moveVertex, moveMachine);
								acceptedMove++;
                            }
                    }*/

                        } else {
                            if (Free[moveMachine] >= FGPGA_FitnessCalc.W[moveVertex]) {
                                /// probabilistic move
                                double random = Math.random();
                                double p = Math.exp(maxGain / temperature);
                                if (random < p) {
                                    current.setGene(moveVertex, moveMachine);
                                    //acceptedMove++;
                                }
                            }
                        }
                        currentFitness = current.getGraphCutCost();
                        if (currentFitness < bestFitness) {
                            best = new FGPGA_Individual(current);
                            bestFitness = currentFitness;
                            //tCount=0;
                        }
				/*if((acceptedMove*1.0/counter)<Fmin)
				{
					//tCount++; // or tCount=I??
							break;
				}*/


                    }
                    if (counter >= epochLength)
                        break;
                    //tCount++;
                    temperature = alpha * temperature;
                    endTime = System.currentTimeMillis();
                }
                System.out.println(best);

                if (best.isValid() == FGPGA_Individual.VALID)
                    System.out.println("Red Signal");
                else {
                    System.out.println("Yeeeei");
                    ite--;
                    continue;
                }
                System.out.println(bestFitness);
                calculateFree(best);
                for (int i = 0; i < FGPGA_FitnessCalc.numberOfMachines; i++) {
                    System.out.println(Free[i] + " " + FGPGA_FitnessCalc.M[i]);

                }
                System.out.println(endTime - startTime);

                avg += bestFitness;
                if(bestFitness<min) min = bestFitness;
            }
            try{
                bw = new BufferedWriter(new FileWriter("SA_RESULTS\\resultSA_" + file));
                bw.write("Time: " + ((System.currentTimeMillis() - st)/1000) + "s");
                bw.newLine();
                bw.write("Minimum Fitness: " + min);
                bw.newLine();
                bw.write("Average Fitness: " + avg);
                bw.newLine();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
