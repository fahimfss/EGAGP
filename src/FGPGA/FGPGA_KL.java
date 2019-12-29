package FGPGA;

import java.util.HashSet;

public class FGPGA_KL {
	public static double[][] Gain;
	public static double[] Free;
	public static int moveVertex;
	public static int moveMachine;
	public static double maxGain;

	static long time;

	static void setTime() {
		time = System.currentTimeMillis();
	}

	static void printTime(String no) {
		long cTime = System.currentTimeMillis();
		System.out.println(no + ": " + ((cTime - time) / 1000) + "s");
		time = cTime;
	}

	public static void calculateFree(FGPGA_Individual me) {
		for (int m = 0; m < FGPGA_FitnessCalc.numberOfMachines; m++)
			Free[m] = FGPGA_FitnessCalc.M[m];
		for (int i = 0; i < FGPGA_FitnessCalc.numberOfVertex; i++) {
			Free[me.getGene(i)] = Free[me.getGene(i)] - FGPGA_FitnessCalc.W[i];
		}
	}

	public static void calculateGain(FGPGA_Individual me, HashSet<Integer> movedList) {
		FGPGA_Individual you = new FGPGA_Individual(me);
		maxGain = Double.NEGATIVE_INFINITY;
		moveVertex = -1;
		moveMachine = -1;
		double myFitness = me.getGraphCutCost();
		for (int i = 0; i < FGPGA_FitnessCalc.numberOfVertex; i++) {
			if (movedList.contains(i)) continue;
			for (int m = 0; m < FGPGA_FitnessCalc.numberOfMachines; m++) {
				you.setGene(i, m);
				Gain[i][m] = myFitness - you.getGraphCutCost();
				if (Gain[i][m] > maxGain && !movedList.contains(i)) {
					maxGain = Gain[i][m];
					moveVertex = i;
					moveMachine = m;
				}
				you.setGene(i, me.getGene(i));
			}
		}
	}

	public static void main(String[] args) {
		setTime();
		double t_avg = 0, avg = 0, minimum, t_min = 0;

		int iterations = 10;

		for (int index = 0; index < 1; index++) {
			String fileName = "G100_" + index + ".txt";
			System.out.println(fileName);
			System.out.print("Iterations: ");
			avg = 0;
			minimum = Double.MAX_VALUE;
			FGPGA_FitnessCalc.extractData("D:\\Thesis\\Data\\DataSet\\" + fileName);

			for (int r = 0; r < iterations; r++) {
				System.out.print(r + " ");

				Gain = new double[FGPGA_FitnessCalc.numberOfVertex][FGPGA_FitnessCalc.numberOfMachines];
				Free = new double[FGPGA_FitnessCalc.numberOfMachines];
				FGPGA_Individual initPartition = new FGPGA_Individual();
				initPartition.generateValidIndividual();

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

				FGPGA_Individual best = initPartition;
				FGPGA_Individual current = initPartition;
				double bestFitness = initPartition.getGraphCutCost();
				double currentFitness = initPartition.getGraphCutCost();

				HashSet<Integer> movedList = new HashSet<Integer>();

				while (true) {
					boolean moveFound = false;
					HashSet<Integer> tempMovedList = new HashSet<Integer>();
					for(int v: movedList){
						tempMovedList.add(v);
					}
					while(true){
						calculateGain(current, tempMovedList);
						calculateFree(current);

						if(moveMachine==-1)break;

						if (Free[moveMachine] < 0) {
							double freeM = Free[current.getGene(moveVertex)];
							if (freeM < 0 && (Free[moveMachine] - FGPGA_FitnessCalc.W[moveVertex] > freeM))
								moveFound = true;
						}
						else
							moveFound = true;

						if(moveFound)break;
						else tempMovedList.add(moveVertex);
					}
					if (moveFound) {
						current.setGene(moveVertex, moveMachine);
						movedList.add(moveVertex);
						if(current.getGraphCutCost() < bestFitness){
							bestFitness = current.getGraphCutCost();
							best = current;
						}
					}
					else
						break;
				}

				double mnCost = bestFitness;
				if(mnCost<minimum){
					minimum = mnCost;
				}
				avg += mnCost;
			}
			System.out.println();
			System.out.println("Avg Graph Cut Cost: " + avg / iterations);
			System.out.println("Min Graph Cut Cost: " + minimum);
			System.out.println();
			t_avg += (avg / iterations);
			t_min += minimum;
		}
		System.out.println("Summation of Avg Graph Cut Cost: " + t_avg);
		System.out.println("Summation of Min Graph Cut Cost: " + t_min);
		printTime("Time");
	}
}
