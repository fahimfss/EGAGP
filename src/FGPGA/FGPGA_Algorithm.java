package FGPGA;
import java.util.ArrayList;
import java.util.Random;

public class FGPGA_Algorithm {

    /* FGPGA_GA parameters */
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static final double greedyMutationRate = 0.5;
    private static int greedyMutationValues = FGPGA_FitnessCalc.numberOfMachines;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;

    /* Public methods */

    // Evolve a population
    public static FGPGA_Population evolvePopulation(FGPGA_Population pop) {
        FGPGA_Population newPopulation = new FGPGA_Population(pop.size(), false);

        // Keep our best individual
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        // Loop over the population size and create new individuals with
        // crossover
        for (int i = elitismOffset; i < pop.size(); i++) {
            int j = 0;
            while (j < FGPGA_FitnessCalc.numberOfVertex) {
                FGPGA_Individual indiv1 = tournamentSelection(pop);
                FGPGA_Individual indiv2 = tournamentSelection(pop);
                FGPGA_Individual newIndiv = onePointCrossover(indiv1, indiv2);
                if (newIndiv.isValid() == FGPGA_Individual.VALID) {
                    newPopulation.saveIndividual(i, newIndiv);
                    break;
                }
                j++;
            }
            if (j == FGPGA_FitnessCalc.numberOfVertex) {
                newPopulation.saveIndividual(i, pop.getIndividual(i));
            }
        }

        // ThreadedMutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            greedymutate(newPopulation.getIndividual(i));
        }
        return newPopulation;
    }

    public static FGPGA_Population randomRestart(FGPGA_Population pop) {
        FGPGA_Population newPopulation = new FGPGA_Population(pop.size(), false);

        // Keep our best individual
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        // Loop over the population size and create new individuals with
        // crossover
        int i;
        for (i = elitismOffset; i < (pop.size() / 5); i++) {
            FGPGA_Individual indiv1 = tournamentSelection(pop);
            newPopulation.saveIndividual(i, indiv1);
        }

        // ThreadedMutate population
        for (; i < newPopulation.size(); i++) {
            FGPGA_Individual indiv = new FGPGA_Individual();
            indiv.generateValidIndividual();
            newPopulation.saveIndividual(i, indiv);
        }
        return newPopulation;
    }

    // Crossover individuals
    private static FGPGA_Individual crossover(FGPGA_Individual indiv1, FGPGA_Individual indiv2) {
        FGPGA_Individual newSol = new FGPGA_Individual();
        // Loop through genes
        for (int i = 0; i < indiv1.size(); i++) {
            // Crossover
            if (Math.random() <= uniformRate) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }
        return newSol;
    }

    // Crossover individuals
    private static FGPGA_Individual onePointCrossover(FGPGA_Individual indiv1, FGPGA_Individual indiv2) {
        FGPGA_Individual newSol = new FGPGA_Individual();
        Random random = new Random();
        int index = random.nextInt(indiv1.size());
        //int index = (int)(Math.random() * indiv1.size());
        // Loop through genes
        int i;
        for (i = 0; i < index; i++) {
            newSol.setGene(i, indiv1.getGene(i));
        }
        for (; i < indiv1.size(); i++) {
            newSol.setGene(i, indiv2.getGene(i));
        }
        return newSol;
    }

    // ThreadedMutate an individual
    private static void mutate(FGPGA_Individual indiv) {
        Random random = new Random();
        // Loop through genes
        for (int i = 0; i < indiv.size(); i++) {
            if (Math.random() <= mutationRate) {
                //int value = (int)Math.floor(Math.random()*FGPGA_FitnessCalc.numberOfMachines);
                int value = random.nextInt(FGPGA_FitnessCalc.numberOfMachines);
                //int value = (int)Math.round(Math.random()*(FGPGA_FitnessCalc.numberOfMachines-1));
                indiv.setGene(i, value);
            }
        }
    }

    private static void greedymutate(FGPGA_Individual indiv) {
        FGPGA_Individual temp = new FGPGA_Individual(indiv);

        Random random = new Random();
        int minIndex = -1;
        int gene_index = -1;
        int min_gene_value = -1;
        int k = 0;
        while (k < FGPGA_FitnessCalc.numberOfVertex) {
            gene_index = random.nextInt(FGPGA_FitnessCalc.numberOfVertex);
            greedyMutationValues = FGPGA_FitnessCalc.numberOfMachines;

            ArrayList<Integer> values = new ArrayList<Integer>();
            for (int i = 0; i < greedyMutationValues; i++) {
                values.add(random.nextInt(FGPGA_FitnessCalc.numberOfMachines));
                //values.add((int)Math.floor(Math.random()*FGPGA_FitnessCalc.numberOfMachines));
            }


            int i = 0;
            double minFitness = -1.0;
            minIndex = -1;
            double fitness;
            while (i < greedyMutationValues) {
                indiv.setGene(gene_index, values.get(i));
                if (indiv.isValid() == FGPGA_Individual.VALID) {
                    minFitness = indiv.getFitness();
                    minIndex = i;
                    min_gene_value = values.get(i);
                    break;
                }
                i++;
            }

            if (minIndex != -1) {
                for (; i < greedyMutationValues; i++) {
                    indiv.setGene(gene_index, values.get(i));
                    if (indiv.isValid() == FGPGA_Individual.VALID) {
                        fitness = indiv.getFitness();
                        if (minFitness > fitness) {
                            minIndex = i;
                            minFitness = fitness;
                            min_gene_value = values.get(i);
                        }
                    }
                }
                break;
            }
            k++;
        }
        if (minIndex != -1) {
            indiv.setGene(gene_index, min_gene_value);
        } else {
            for (int i = 0; i < indiv.size(); i++) {
                indiv.setGene(i, temp.getGene(i));
            }
        }
    }

    // Select individuals for crossover
    private static FGPGA_Individual tournamentSelection(FGPGA_Population pop) {
        // Create a tournament population
        FGPGA_Population tournament = new FGPGA_Population(tournamentSize, false);
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        // Get the fittest
        FGPGA_Individual fittest = tournament.getFittest();
        return fittest;
    }

    public static FGPGA_Population removeTwins(FGPGA_Population myPop) {
        // TODO Auto-generated method stub
        int count = 0;
        for (int i = 0; i < myPop.size(); i++) {
            for (int j = i + 1; j < myPop.size(); j++) {
                if (isTwins(myPop.getIndividual(i), myPop.getIndividual(j))) {
                    FGPGA_Individual indiv = new FGPGA_Individual();
                    indiv.generateValidIndividual();
                    myPop.saveIndividual(j, indiv);
                    count++;
                }
            }
        }
//        System.out.println("Twins found: " + count);
        return myPop;
    }

    private static boolean isTwins(FGPGA_Individual ind1, FGPGA_Individual ind2) {
        int hammingDistance = 0;
        for (int i = 0; i < ind1.size(); i++) {
            if (ind1.getGene(i) != ind2.getGene(i))
                hammingDistance++;
        }
        if ((hammingDistance * 100 / (double) ind1.size()) <= 2.50)
            return true;
        else
            return false;
    }
}