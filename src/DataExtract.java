import java.io.BufferedReader;
import java.io.FileReader;
 
class DataExtract {
   static double[][] C;       // Component communication cost
   static double[] W;         // Weight of component
   static double[][] B;       // Machine communication cost
   static double[] M;         // Machine capacity

   static int numberOfMachines, numberOfVertex;
   static double maxComponentCommunicationCost, maxMachineCommunicationCost;

    private static final double HIGHVALUE = 10000000.0;

    static void extractData(String path) {
        try {
            maxComponentCommunicationCost = -1;
            maxMachineCommunicationCost = -1;
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

                double c = Double.parseDouble(array[2]);
                maxComponentCommunicationCost = Math.max(c, maxComponentCommunicationCost);
                C[x][y] = c;
                C[y][x] = c;
            }

            for(int i = 0; i < n; i++) for(int j = 0; j < n; j++) C[i][j] /= maxComponentCommunicationCost;

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
                    B[i][j] = HIGHVALUE*HIGHVALUE*HIGHVALUE;
                }
            }
            for (int i = 0; i < m; i++) {
                line = br.readLine();
                array = line.split(" ");
                x = Integer.parseInt(array[0]);
                y = Integer.parseInt(array[1]);
                double c = Double.parseDouble(array[2]);
                B[x][y] = c;
                B[y][x] = c;
                maxMachineCommunicationCost = Math.max(maxMachineCommunicationCost, c);
            }
            for(int i = 0; i < n; i++) for(int j = 0; j < n; j++) B[i][j] /= maxMachineCommunicationCost;

            line = br.readLine();
            array = line.split(" ");
            for (int i = 0; i < array.length; i++) {
                M[i] = Double.parseDouble(array[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}