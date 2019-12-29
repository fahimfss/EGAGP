import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Individual {

    private int[] genes;
    private double[] usedCapacities;       // used amount of capacities for each machine

    private double fitness;
    private int validity;
    private boolean isFitnessCalculated;

    private int bestGeneIndex;

    private static final double HIGH_VALUE = 10000000.0;
    static final int VALID = 1;
    static final int INVALID = 0;
    private static final int UNKNOWN = -1;

    Individual() {
        generateValidIndividual();
    }

    Individual(Individual you) {

        genes = new int[DataExtract.numberOfVertex];
        System.arraycopy(you.getGenes(), 0, genes, 0, DataExtract.numberOfVertex);

        usedCapacities = new double[DataExtract.numberOfMachines];
        System.arraycopy(you.getUsedCapacities(), 0, usedCapacities, 0, DataExtract.numberOfMachines);

        this.fitness = you.getFitness(true);

        this.validity = you.isValid();

        isFitnessCalculated = you.getFitnessCalculated();

        if(GAWithTiers.useBestGene)bestGeneIndex = you.getBestGeneIndex();
    }

    void generateValidIndividual() {

        double[] free = new double[DataExtract.numberOfMachines];
        int[] machine = new int[DataExtract.numberOfMachines];

        usedCapacities = new double[DataExtract.numberOfMachines];
        genes = new int[DataExtract.numberOfVertex];

        fitness = Double.MAX_VALUE;
        bestGeneIndex = -1;
        isFitnessCalculated = false;

        for (int i = 0; i < free.length; i++) {
            free[i] = DataExtract.M[i];
            machine[i] = i;
            usedCapacities[i]=0;
        }
        int value, temp, gene;
        for(int times = 0; times < 20; times++) {
            for (int i = 0; i < size(); i++) {
                for (int j = 0; j < DataExtract.numberOfMachines; j++) {

                    value = ThreadLocalRandom.current().nextInt(DataExtract.numberOfMachines - j);
                    gene = machine[value];
                    if(gene>=DataExtract.numberOfMachines || gene < 0){
                        System.out.println("Gene value invalid!");
                        System.exit(-1);
                    }
                    if (free[gene] >= DataExtract.W[i]) {
                        genes[i] = gene;
                        free[gene] -= DataExtract.W[i];
                        usedCapacities[gene] += DataExtract.W[i];
                        break;
                    }
                    temp = machine[DataExtract.numberOfMachines - j - 1];
                    machine[DataExtract.numberOfMachines - j - 1] = machine[value];
                    machine[value] = temp;
                }
            }
            validity = UNKNOWN;
            if(isValid()==VALID)return;
        }
        validity = UNKNOWN;
        if(isValid()==INVALID){
            System.out.println("Valid individual generation failed!");
            System.exit(-1);
        }
    }

    int isValid() {

        if (validity != UNKNOWN) return validity;

        double[] M = DataExtract.M;

        for (int i = 0; i < DataExtract.numberOfMachines; i++) {
            if (usedCapacities[i] > M[i]) {
                validity = INVALID;
                return validity;
            }
        }
        validity = VALID;
        return validity;
    }

    int isValidResetUsedCapacities() {

        double[] M = DataExtract.M;
        double[] W = DataExtract.W;
        for (int i = 0; i < DataExtract.numberOfMachines; i++) usedCapacities[i]=0.0;

        for (int i=0; i<DataExtract.numberOfVertex; i++){
            usedCapacities[genes[i]]+=W[i];
        }
        for (int i = 0; i < DataExtract.numberOfMachines; i++) {
            if (usedCapacities[i] > M[i]) {
                validity = INVALID;
                return validity;
            }
        }
        validity = VALID;
        return validity;
    }

    void onePointCrossover(Individual indiv1, Individual indiv2) {
        int index = ThreadLocalRandom.current().nextInt(indiv1.size());
        int bgene1 = indiv1.getBestGeneIndex(), bgene2 = indiv2.getBestGeneIndex();
        if(!GAWithTiers.useBestGene){
            bgene1 = -1;
            bgene2 = -1;
        }
        int i;

        for(int j = 0; j< DataExtract.numberOfMachines; j++) usedCapacities[j] = 0;

        for (i = 0; i < index; i++) {
            if(i==bgene2){
                genes[i] =  indiv2.getGene(bgene2);
                usedCapacities[genes[i]] += DataExtract.W[i];
                continue;
            }
            int gene = indiv1.getGene(i);
            genes[i] = gene;
            usedCapacities[gene] += DataExtract.W[i];
        }
        for (; i < indiv1.size(); i++) {
            if(i==bgene1){
                genes[i] =  indiv1.getGene(bgene1);
                usedCapacities[genes[i]] += DataExtract.W[i];
                continue;
            }
            int gene = indiv2.getGene(i);
            genes[i] = gene;
            usedCapacities[gene] += DataExtract.W[i];
        }
        validity = UNKNOWN;
    }

    int getGene(int index) {
        return genes[index];
    }

    void greedyMutate() {

        Individual temp = new Individual(this);

        int minIndex;
        int geneIndex;
        int minIndexValue = -1;
        int k = 0;
        while (k < DataExtract.numberOfVertex) {

            geneIndex = ThreadLocalRandom.current().nextInt(DataExtract.numberOfVertex);

            int greedyMutationValues = DataExtract.numberOfMachines;

            ArrayList<Integer> values = new ArrayList<>();
            for (int i = 0; i < greedyMutationValues; i++) {
                values.add(ThreadLocalRandom.current().nextInt(DataExtract.numberOfMachines));
            }
            int i = 0;
            double minFitness = Double.MAX_VALUE, fitness;
            minIndex = -1;
            while (i < greedyMutationValues) {
                setGene(geneIndex, values.get(i));
                if (isValid() == Individual.VALID) {
                    fitness = getFitness(true);
                    if (fitness < minFitness) {
                        minIndex = i;
                        minIndexValue = values.get(i);
                    }
                }
                setGene(geneIndex, temp.getGene(geneIndex));
                i++;
            }
            if (minIndex != -1) {
                setGene(geneIndex, minIndexValue);
                if(GAWithTiers.useBestGene) setBestGeneIndex(geneIndex);
                break;
            }
            k++;
        }
    }

    void setGene(int index, int value) {

        int prevValue = genes[index];
        if (value == prevValue) {
            return;
        }
        genes[index] = value;
        validity = UNKNOWN;

        double[] W = DataExtract.W;
        double[] M = DataExtract.M;

        if (!isFitnessCalculated) {
            usedCapacities[prevValue] -= W[index];
            usedCapacities[value] += W[index];
            getFitness(true);
        } else {
            if (usedCapacities[prevValue] > M[prevValue] && usedCapacities[prevValue] - W[index] <= M[prevValue])
                fitness -= HIGH_VALUE;

            if (usedCapacities[value] <= M[value] && usedCapacities[value] + W[index] > M[value])
                fitness += HIGH_VALUE;

            usedCapacities[prevValue] -= W[index];
            usedCapacities[value] += W[index];

            double[][] C = DataExtract.C;
            double[][] B = DataExtract.B;

            double fitnessPlus = 0, fitnessMinus = 0;

            for (int i = 0; i < DataExtract.numberOfVertex; i++) {
                if (genes[i] != value) {
                    fitnessPlus += C[i][index] * B[genes[i]][value];
                }
                if (genes[i] != prevValue) {
                    fitnessMinus += C[i][index] * B[genes[i]][prevValue];
                }
            }

            fitness += fitnessPlus;
            fitness -= fitnessMinus;
        }
    }

    int size() {
        return genes.length;
    }

    double getFitness(boolean normalized) {

        if (!isFitnessCalculated) {

            fitness = 0;
            for (int i = 0; i < DataExtract.numberOfMachines; i++) {
                if (usedCapacities[i] > DataExtract.M[i]) {
                    fitness += HIGH_VALUE;
                }
            }
            double[][] C = DataExtract.C;
            double[][] B = DataExtract.B;

            for (int i = 0; i < DataExtract.numberOfVertex; i++) {
                for (int j = i + 1; j < DataExtract.numberOfVertex; j++) {
                    if (genes[i] != genes[j]) {
                        double d = (C[i][j] * B[genes[i]][genes[j]]);
                        fitness += d;
                    }
                }
            }
            isFitnessCalculated = true;
        }

        if(normalized) return fitness;
        else return fitness * DataExtract.maxMachineCommunicationCost * DataExtract.maxComponentCommunicationCost;
    }

    private boolean getFitnessCalculated() {
        return isFitnessCalculated;
    }

    @Override
    public String toString() {
        StringBuilder strBuff = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            strBuff.append("(Vertex ").append(i).append(" ==> Machine ").append(getGene(i)).append(")\n");
        }
        return strBuff.toString();
    }

    private int[] getGenes(){
        return genes;
    }

    private double[] getUsedCapacities() {
        return usedCapacities;
    }

    private int getBestGeneIndex() {
        return bestGeneIndex;
    }

    void setBestGeneIndex(int bestGene) {
        this.bestGeneIndex = bestGene;
    }
}