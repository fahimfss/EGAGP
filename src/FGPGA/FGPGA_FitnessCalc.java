package FGPGA;
import java.io.BufferedReader;
import java.io.FileReader;

public class FGPGA_FitnessCalc {
    public static double[][] C;       // Component communication cost
    public static double[] W;         // Weight of component
    public static double[][] B;       // Machine communication cost
    public static double[] M;         // Machine capacity

    public static int numberOfMachines, numberOfVertex;

    private static final double HIGHVALUE = 10000000.0;

    public static void extractData(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            int n, m;
            int x, y;
            String[] array;

            //1st Graph
            n = Integer.parseInt(br.readLine());
            m = Integer.parseInt(br.readLine());
            numberOfVertex = n;
            C = new double[n][n];
            W = new double[n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    C[i][j] = 0;
                }
            }
            for (int i = 0; i < m; i++) {
                line = br.readLine();
                array = line.split(" ");
                x = Integer.parseInt(array[0]);
                y = Integer.parseInt(array[1]);
                C[x][y] = Double.parseDouble(array[2]);
                C[y][x] = Double.parseDouble(array[2]);
            }
            line = br.readLine();
            array = line.split(" ");
            for (int i = 0; i < array.length; i++) {
                W[i] = Double.parseDouble(array[i]);
            }

            n = Integer.parseInt(br.readLine());
            m = Integer.parseInt(br.readLine());
            numberOfMachines = n;
            B = new double[n][n];
            M = new double[n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    B[i][j] = HIGHVALUE;
                }
            }
            for (int i = 0; i < m; i++) {
                line = br.readLine();
                array = line.split(" ");
                x = Integer.parseInt(array[0]);
                y = Integer.parseInt(array[1]);
                B[x][y] = Double.parseDouble(array[2]);
                B[y][x] = Double.parseDouble(array[2]);
            }
            line = br.readLine();
            array = line.split(" ");
            for (int i = 0; i < array.length; i++) {
                M[i] = Double.parseDouble(array[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static double getGraphCutCost(FGPGA_Individual individual) {
        double fitness = 0;


        for (int i = 0; i < individual.size(); i++) {
            for (int j = i + 1; j < individual.size(); j++) {
                if (individual.getGene(i) != individual.getGene(j)) {
                    fitness += (C[i][j] * B[individual.getGene(i)][individual.getGene(j)]);
                }
            }
        }
        return fitness;
    }

    static int getValidity(FGPGA_Individual individual) {
        double UsedCapacity;
        for (int i = 0; i < numberOfMachines; i++) {
            UsedCapacity = 0.0;
            for (int j = 0; j < individual.size(); j++) {
                if (individual.getGene(j) == i) {
                    UsedCapacity += W[j];
                }
            }
            if (UsedCapacity > M[i])
                return FGPGA_Individual.INVALID;
        }
        return FGPGA_Individual.VALID;
    }

    static double getFitness(FGPGA_Individual individual) {
        double fitness = 0;
        double UsedCapacity;
        for (int i = 0; i < numberOfMachines; i++) {
            UsedCapacity = 0.0;
            for (int j = 0; j < individual.size(); j++) {
                if (individual.getGene(j) == i) {
                    UsedCapacity += W[j];
                }
            }
            if (UsedCapacity > M[i])
                fitness += HIGHVALUE;
        }

        for (int i = 0; i < individual.size(); i++) {
            for (int j = i + 1; j < individual.size(); j++) {
                if (individual.getGene(i) != individual.getGene(j)) {
                    fitness += (C[i][j] * B[individual.getGene(i)][individual.getGene(j)]);
                }
            }
        }
        return fitness;
    }
}