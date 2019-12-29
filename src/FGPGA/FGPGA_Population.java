package FGPGA;

public class FGPGA_Population
{
    FGPGA_Individual[] individuals;
    /*
     * Constructors
     */
    // Create a population
    public FGPGA_Population(int populationSize, boolean initialise)
    {
        individuals = new FGPGA_Individual[populationSize];
        // Initialise population
        if (initialise) 
        {
            //Loop and create individuals
            for (int i = 0; i < size(); i++) 
            {
                FGPGA_Individual newIndividual = new FGPGA_Individual();
                newIndividual.generateValidIndividual();
                saveIndividual(i, newIndividual);
            }
        }
    }

    /* Getters */
    public FGPGA_Individual getIndividual(int index)
    {
        return individuals[index];
    }

    public FGPGA_Individual getFittest()
    {
    	FGPGA_Individual fittest = null;
    	for(int i=0;i<size();i++)
    	{
    		if(getIndividual(i).isValid()== FGPGA_Individual.VALID)
    		{
    			fittest = getIndividual(i);
    			break;
    		}
    	}
        
        //Loop through individuals to find fittest
    	if(fittest!=null)
    	{
	        for (int i = 0; i < size(); i++) 
	        {
	        	if(getIndividual(i).isValid() == FGPGA_Individual.VALID)
	        	{
		            if (fittest.getFitness() > getIndividual(i).getFitness()) 
		            {
		                fittest = getIndividual(i);
		            }
	        	}
	        }
	        return fittest;
    	}
    	else
    		return null;
    }

    /* Public methods */
    // Get population size
    public int size() 
    {
        return individuals.length;
    }
    
    // Save individual
    public void saveIndividual(int index, FGPGA_Individual indiv)
    {
        individuals[index] = indiv;
    }
}