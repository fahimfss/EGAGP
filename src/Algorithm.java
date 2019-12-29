import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class Algorithm implements Runnable{

    private static final int tournamentSize = 5;
    private static final int noOfThreads = 8;
    private static final boolean elitism = true;

    private Population population;
    private int generationLimit;
    private int restartAfter;
    private int index;
    private ArrayList<Individual> individuals;

    Algorithm(Population population, int generationLimit, int restartAfter,
              ArrayList<Individual> individuals, int index){
        this.population = population;
        this.generationLimit = generationLimit;
        this.restartAfter = restartAfter;
        this.individuals = individuals;
        this.index = index;
    }

    private void geneticAlgorithm(){

        int generationCount = 0, lastUpdated = 0;
        double bestFitness = Double.MAX_VALUE, tempBest;

        while (generationCount < generationLimit) {
            if ((generationCount + 1) % 50 == 0) removeTwins();

            evolvePopulation();

            tempBest = population.getFittest().getFitness(false);
            if (tempBest < bestFitness) {
                bestFitness = tempBest;
                lastUpdated = generationCount;
            }

            if (generationCount - lastUpdated > restartAfter) {
                randomRestart();
            }

            generationCount++;
            lastUpdated++;
        }
    }

    private void evolvePopulation() {
        Population newPopulation = new Population(population.size(), false);
        if (elitism) {
            newPopulation.saveIndividual(0, population.getFittest());
        }
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        for (int i = elitismOffset; i < population.size(); i++) {
            int j = 0;
            while (j < DataExtract.numberOfVertex) {
                Individual indiv1 = tournamentSelection(population);
                Individual indiv2 = tournamentSelection(population);

                if (indiv1 == null || indiv2 == null) {
                    System.out.println("Tournament selection returned null individuals!");
                    System.exit(-1);
                }
                Individual newIndiv = onePointCrossover(indiv1, indiv2);
                if (newIndiv.isValid() == Individual.VALID) {
                    newPopulation.saveIndividual(i, newIndiv);
                    break;
                }
                j++;
            }
            if (j == DataExtract.numberOfVertex) {
                newPopulation.saveIndividual(i, population.getIndividual(i));
            }
        }
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            newPopulation.getIndividual(i).greedyMutate();
        }
        population = newPopulation;
    }

    private void randomRestart() {
        Population newPopulation;

        newPopulation = new Population(population.size(), false);
        newPopulation.saveIndividual(0, population.getFittest());

        int i;
        for (i = 1; i < (population.size() / 5); i++) {
            Individual indiv1 = tournamentSelection(population);
            newPopulation.saveIndividual(i, indiv1);
        }
        for (; i < newPopulation.size(); i++) {
            Individual indiv = new Individual();                 //new individual generates valid individual
            newPopulation.saveIndividual(i, indiv);
        }
        population = newPopulation;
    }

    private Individual onePointCrossover(Individual indiv1, Individual indiv2) {
        Individual newIndiv = new Individual();
        newIndiv.onePointCrossover(indiv1, indiv2);
        return newIndiv;
    }

    private Individual tournamentSelection(Population pop) {
        Population tournament = new Population(tournamentSize, false);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = ThreadLocalRandom.current().nextInt(pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest();
    }

    private void removeTwins() {
        for (int i = 0; i < population.size(); i++) {
            for (int j = i + 1; j < population.size(); j++) {
                if (isTwins(population.getIndividual(i), population.getIndividual(j))) {
                    Individual indiv = new Individual();
                    indiv.generateValidIndividual();
                    population.saveIndividual(j, indiv);
                }
            }
        }
    }

    private boolean isTwins(Individual ind1, Individual ind2) {
        int hammingDistance = 0;
        for (int i = 0; i < ind1.size(); i++) {
            if (ind1.getGene(i) != ind2.getGene(i))
                hammingDistance++;
        }
        return (hammingDistance * 100 / (double) ind1.size()) <= 2.50;
    }

    Population getPopulation(){
        return population;
    }

    @Override
    public void run() {
        geneticAlgorithm();
        if(individuals != null) individuals.set(index, population.getFittest());
    }
}

//class ThreadedCrossOver implements Runnable{
//    private int index;
//    private Population newPopulation;
//    private Population oldPopulation;
//
//    ThreadedCrossOver(int index, Population newPopulation, Population oldPopulation) {
//        this.index = index;
//        this.newPopulation = newPopulation;
//        this.oldPopulation = oldPopulation;
//    }
//    private Individual tournamentSelection(Population pop) {
//        Population tournament = new Population(Algorithm.tournamentSize, false);
//        for (int i = 0; i < Algorithm.tournamentSize; i++) {
//            int randomId = ThreadLocalRandom.current().nextInt(pop.size());
//            tournament.saveIndividual(i, pop.getIndividual(randomId));
//        }
//        return tournament.getFittest();
//    }
//
//    @Override
//    public void run() {
//        int j = 0;
//        while (j < DataExtract.numberOfVertex) {
//            Individual indiv1 = tournamentSelection(oldPopulation);
//            Individual indiv2 = tournamentSelection(oldPopulation);
//
//            if(indiv1==null || indiv2==null){
//                System.out.println("Tournament selection returned null individuals!");
//                System.exit(-1);
//            }
//
//            Individual newIndiv = new Individual();
//            newIndiv.onePointCrossover(indiv1, indiv2);
//
//            if (newIndiv.isValid() == Individual.VALID) {
//                newPopulation.saveIndividual(index, newIndiv);
//                break;
//            }
//            j++;
//        }
//        if (j == DataExtract.numberOfVertex) {
//            newPopulation.saveIndividual(index, oldPopulation.getIndividual(index));
//        }
//    }
//}
//
//class ThreadedMutate implements Runnable{
//    private Individual individual;
//
//    ThreadedMutate(Individual individual) {
//        this.individual = individual;
//    }
//
//    @Override
//    public void run() {
//        individual.greedyMutate();
//    }
//}

//    private void evolvePopulation() {
//        Population newPopulation = new Population(population.size(), false);
//        if (elitism) {
//            newPopulation.saveIndividual(0, population.getFittest());
//        }
//        int elitismOffset;
//        if (elitism) {
//            elitismOffset = 1;
//        } else {
//            elitismOffset = 0;
//        }
//
//        if(!threadedFunctions) {
//            for (int i = elitismOffset; i < population.size(); i++) {
//                int j = 0;
//                while (j < DataExtract.numberOfVertex) {
//                    Individual indiv1 = tournamentSelection(population);
//                    Individual indiv2 = tournamentSelection(population);
//
//                    if (indiv1 == null || indiv2 == null) {
//                        System.out.println("Tournament selection returned null individuals!");
//                        System.exit(-1);
//                    }
//                    Individual newIndiv = onePointCrossover(indiv1, indiv2);
//                    if (newIndiv.isValid() == Individual.VALID) {
//                        newPopulation.saveIndividual(i, newIndiv);
//                        break;
//                    }
//                    j++;
//                }
//                if (j == DataExtract.numberOfVertex) {
//                    newPopulation.saveIndividual(i, population.getIndividual(i));
//                }
//            }
//        }
//        else{
//            ArrayList<Thread> threads = new ArrayList<>();
//            for (int i = elitismOffset; i < population.size(); i++) {
//                ThreadedCrossOver c = new ThreadedCrossOver(i, newPopulation, population);
//                Thread t = new Thread(c);
//                threads.add(t);
//                t.start();
//            }
//            for(Thread t: threads){
//                try {
//                    t.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        if(!threadedFunctions || true) {
//            for (int i = elitismOffset; i < newPopulation.size(); i++) {
//                newPopulation.getIndividual(i).greedyMutate();
//            }
//        }
//        else{
//            ArrayList<Thread> threads = new ArrayList<>();
//            for (int i = elitismOffset; i < newPopulation.size(); i++) {
//                ThreadedMutate m = new ThreadedMutate(newPopulation.getIndividual(i));
//                Thread t = new Thread(m);
//                threads.add(t);
//                t.start();
//            }
//            for(Thread t: threads){
//                try {
//                    t.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        population = newPopulation;
//    }