package taigore.buildapi.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;

//TODO Cloneable
public class RandomSelector<Type> implements Cloneable
{
    private Random fallbackGenerator = new Random();
    
    private Map<Type, Integer> probabilityMap = new HashMap();
    private int totalProbability = 0;
    
    public RandomSelector() {};
    public RandomSelector(RandomSelector<Type> toCopy)
    {
        this.probabilityMap.putAll(toCopy.probabilityMap);
        this.totalProbability = toCopy.totalProbability;
    }
    
    /**
     * Adds a new object to the random selection list.
     * If the object was already added, it increases its probability
     * of being chosen by the specified amount, or decreases it if negative.
     * If the final probability is negative, it will be removed.
     * @param toAdd
     * @param probability
     */
    protected void addObject(Type toAdd, int probability)
    {
        if(probabilityMap.containsKey(toAdd))
        {
            int oldProbability = probabilityMap.get(toAdd);
            
            probability += oldProbability;
            this.totalProbability -= oldProbability;
        }
        
        if(probability > 0)
        {
            this.probabilityMap.put(toAdd, probability);
            this.totalProbability += probability;
        }
        else
            this.probabilityMap.remove(toAdd);
    }
    
    /**
     * Returns a randomly selected object using the provided generator.
     * It will draw a single int from it. It will use a fallback random generator
     * if that one is invalid.
     * @param generator - A non null generator
     * @return Null if this selector has no values to choose from, a value from its table otherwise (even null if it was added)
     */
    protected Type select(Random generator)
    {
        if(this.totalProbability > 0 && !this.probabilityMap.isEmpty())
        {
            if(generator == null)
                generator = this.fallbackGenerator;
            
            int draw = generator.nextInt(this.totalProbability);
            
            for(Map.Entry<Type, Integer> toCheck : this.probabilityMap.entrySet())
            {
                draw -= toCheck.getValue();
                
                if(draw < 0)
                    return toCheck.getKey();
            }
        }
        
        return null;
    }
    
    ///////////
    // Object
    ///////////
    @Override
    public RandomSelector clone()
	{
	    try
	    {
    	    RandomSelector returnValue = (RandomSelector)super.clone();
    	    
    	    returnValue.fallbackGenerator = new Random();
    	    returnValue.probabilityMap = new HashMap(this.probabilityMap);
    	    
    	    return returnValue;
	    }
	    catch (CloneNotSupportedException e)
        {
            FMLLog.info("Clone failed");
            e.printStackTrace();
            
            return null;
        }
	}
    
    @Override
    public boolean equals(Object toCompare)
    {
        if(this == toCompare) return true;
        
        if(this.getClass().isInstance(toCompare))
        {
            RandomSelector selectorToCompare = (RandomSelector)toCompare;
            
            return this.probabilityMap.equals(selectorToCompare.probabilityMap.entrySet());
        }
        
        return false;
    }
    
    /**
     * Returns the fraction of probability that the given object will be
     * chosen at the next draw.
     * @param toCheck - An object
     * @return 0 for not included objects, a number between 1.0 (inclusive) and 0.0 (exclusive) otherwise.
     */
    public double getProbability(Type toCheck) { return this.probabilityMap.containsKey(toCheck) ? (double)this.probabilityMap.get(toCheck) / this.totalProbability : 0; }
}
