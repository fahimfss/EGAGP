class Population {
    private Individual[] individuals;

    Population(int populationSize, boolean initialise) {
        individuals = new Individual[populationSize];
        if (initialise) {
            for (int i = 0; i < size(); i++) {
                Individual newIndividual = new Individual();
                newIndividual.generateValidIndividual();
                saveIndividual(i, newIndividual);
            }
        }
    }

    public Individual[] getIndividuals(){
        return  individuals;
    }

    Individual getIndividual(int index) {
        return individuals[index];
    }

    Individual getFittest() {
        Individual fittest = null;
        for (int i = 0; i < size(); i++) {
            if (getIndividual(i).isValid() == Individual.VALID) {
                fittest = getIndividual(i);
                break;
            }
        }
        if (fittest != null) {
            for (int i = 0; i < size(); i++) {
                if (getIndividual(i).isValid() == Individual.VALID) {
                    if (fittest.getFitness(true) > getIndividual(i).getFitness(true)) {
                        fittest = getIndividual(i);
                    }
                }
            }
            return fittest;
        } else{
            System.out.println("No valid individual in population!");
            System.exit(-1);
            return null;
        }
    }

    int size() {
        return individuals.length;
    }

    void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }
}