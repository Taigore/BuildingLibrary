package taigore.buildapi.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomSelector
{
    private final Random fallbackGenerator = new Random();
    
    protected Map<Object, Integer> probabilityMap = new HashMap();
    protected int totalProbability = 0;
    
    /**
     * Adds a new object to the random selection list.
     * If the object was already added, it increases its probability
     * of being chosen by the specified amount, or decreases it if negative.
     * If the final probability is negative, it will be removed.
     * @param toAdd
     * @param probability
     */
    protected void addObject(Object toAdd, int probability)
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
    protected Object select(Random generator)
    {
        if(this.totalProbability > 0 && !this.probabilityMap.isEmpty())
        {
            if(generator == null)
                generator = this.fallbackGenerator;
            
            int draw = generator.nextInt(this.totalProbability);
            
            for(Map.Entry<Object, Integer> toCheck : this.probabilityMap.entrySet())
            {
                draw -= toCheck.getValue();
                
                if(draw < 0)
                    return toCheck.getValue();
            }
        }
        
        return null;
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
    protected double getProbability(Object toCheck) { return this.probabilityMap.containsKey(toCheck) ? (double)this.probabilityMap.get(toCheck) / this.totalProbability : 0; }
}
