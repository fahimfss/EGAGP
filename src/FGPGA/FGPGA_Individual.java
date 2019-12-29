package FGPGA;
import java.util.Random;

public class FGPGA_Individual
{
    private int[] genes;
    // Cache
    private double fitness = 0;
    private int isValid;
   
    public static final int VALID = 1;
    public static final int INVALID = 0;
    public static final int DONTKNOW = -1;
    public FGPGA_Individual()
    {
    	genes = new int[FGPGA_FitnessCalc.numberOfVertex];
    	isValid = DONTKNOW;
	}
    
    public FGPGA_Individual(FGPGA_Individual you)
    {
    	genes = new int[FGPGA_FitnessCalc.numberOfVertex];
    	for(int i=0;i<genes.length;i++)
    	{
    		genes[i] = you.getGene(i);
    	}
    	this.fitness = you.getFitness();
    	this.isValid = you.isValid();
	}
    
    public int isValid()
    {
    	if(this.isValid==DONTKNOW)
    	{
    		this.isValid = FGPGA_FitnessCalc.getValidity(this);
    	}
    	return this.isValid;
    }
    
    public void generateValidIndividual() 
    {
    	double[] free = new double[FGPGA_FitnessCalc.numberOfMachines];
    	for(int i=0;i<free.length;i++)
    	{
    		free[i] = FGPGA_FitnessCalc.M[i];
    	}
    	int value;
    	Random random = new Random();
        for (int i = 0; i < size(); i++) 
        {
        	while(true)
        	{
        		//value = (int)Math.floor(Math.random()*FGPGA_FitnessCalc.numberOfMachines);
	        	value = random.nextInt(FGPGA_FitnessCalc.numberOfMachines);
        		//value = (int)Math.round(Math.random()*(FGPGA_FitnessCalc.numberOfMachines-1));
	        	if(free[value]>= FGPGA_FitnessCalc.W[i])
	        	{
	        		genes[i] = value;
	        		free[value] -= FGPGA_FitnessCalc.W[i];
	        		break;
	        	}
        	}
        }
        this.fitness = 0.0;
        this.isValid = DONTKNOW;
    }
    
    public int getGene(int index) 
    {
        return genes[index];
    }

    public void setGene(int index, int value) 
    {
        genes[index] = value;
        fitness = 0;
        isValid = DONTKNOW;
    }

    /* Public methods */
    public int size() 
    {
        return genes.length;
    }

    public double getFitness() 
    {
        if (fitness == 0)
        {
            fitness = FGPGA_FitnessCalc.getFitness(this);
        }
        return fitness;
    }
    
    public double getGraphCutCost() 
    {
        return FGPGA_FitnessCalc.getGraphCutCost(this);
    }

    @Override
    public String toString() 
    {
    	StringBuffer strBuff = new StringBuffer();
    	for(int i=0;i<size();i++)
    	{
    		strBuff.append("(Vertex "+ i+" ==> Machine "+getGene(i)+")\n");
    	}
    	return strBuff.toString();
    }
}