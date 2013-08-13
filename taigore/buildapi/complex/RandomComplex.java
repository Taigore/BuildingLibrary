package taigore.buildapi.complex;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.utils.ParamSet;
import taigore.buildapi.utils.RandomSelector;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

public class RandomComplex extends RandomSelector<IComplexGenerator> implements IComplexGenerator
{
    private Map<ParamSet, IComplexGenerator> savedPeeks = new HashMap();
    private Rotation facing = Rotation.NO_ROTATION;
    
    /**
     * Adds several complexes to this random complex, all with probability 1.
     * A list with duplicates will add the duplicate with probability 2, 3 and so on,
     * based on how many times the IComplexGenerator appears in the list.
     */
    public RandomComplex addComplexes(IComplexGenerator...toAdd)
    {
        Map<IComplexGenerator, Integer> added = new HashMap();
        
        for(IComplexGenerator complex : toAdd)
            added.put(complex, added.containsKey(complex) ? added.get(complex) : 1);
        
        for(IComplexGenerator complex : added.keySet())
            this.addComplex(complex, added.get(complex));
        
        return this;
    }
    /**
     * Adds a complex for random generation.
     * If probability is 0, nothing happens.
     * If probability is positive:
     *     -If toAdd was already added, probabilities will add and increase the likeliness of its selection
     *     -If toAdd wasn't added, it will be added with that probability of being chosen
     * If probability is negative:
     *     -If toAdd was already added, probability will be subtracted, and if total probability is 0 or less, the building will be removed
     *     -If toAdd wasn't added, nothing will happen
     */
    public RandomComplex addComplex(IComplexGenerator toAdd, int probability)
    {
        if(probability > 0 || (probability < 0 && this.getProbability(toAdd) > 0))
            this.savedPeeks.clear();
        
        this.addObject(toAdd.clone(), probability);
        
        return this;
    }
    
    public RandomComplex setRotation(Rotation facing) { this.facing = facing != null ? facing : Rotation.NO_ROTATION; return this; }
    public RandomComplex addRotation(Rotation facing) { this.facing = this.facing.add(facing); return this; }
    
    //////////////////////
    // IComplexGenerator
    //////////////////////
    @Override
    public IComplexGenerator peekNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        IComplexGenerator returnValue = null;
        facing = this.facing.add(facing);
        
        ParamSet paramSet = new ParamSet(world, startPosition, facing, generator);
        
        if(this.savedPeeks.containsKey(paramSet))
            returnValue = this.savedPeeks.get(paramSet);
        else
        {
            IComplexGenerator selected = this.select(generator);
            
            if(selected != null)
                returnValue = selected.peekNextComplex(world, startPosition, facing, generator);
            
            this.savedPeeks.put(paramSet, returnValue);
        }
        
        if(returnValue != null)
            returnValue = returnValue.clone();
        
        return returnValue;
    }
    
    @Override
    public IComplexGenerator getNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        IComplexGenerator returnValue = null;
        facing = this.facing.add(facing);
        
        ParamSet paramSet = new ParamSet(world, startPosition, facing, generator);
        
        if(this.savedPeeks.containsKey(paramSet))
            returnValue = this.savedPeeks.remove(paramSet);
        else
        {
            IComplexGenerator selected = this.select(generator);
            
            if(selected != null)
                returnValue = selected.getNextComplex(world, startPosition, facing, generator);
        }
        
        if(returnValue != null)
            returnValue = returnValue.clone();
        
        this.savedPeeks.clear();
        
        return returnValue;
    }
    
    @Override
    public void placeNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        IComplexGenerator toPlace = this.getNextComplex(world, startPosition, facing, generator);
        
        if(toPlace != null)
            toPlace.placeNextComplex(world, startPosition, Rotation.NO_ROTATION, generator);
    }
    
    ///////////
    // Object
    ///////////
    @Override
    public RandomComplex clone()
	{
        RandomComplex returnValue = (RandomComplex)super.clone();
        
        returnValue.savedPeeks = new HashMap(this.savedPeeks);
        
        return returnValue;
	}
    @Override
    public boolean equals(Object toCompare)
    {
        if(toCompare == null) return false;
        if(this == toCompare) return true;
        
        if(this.getClass() == toCompare.getClass())
        {
            RandomComplex oToCompare = (RandomComplex) toCompare;
            
            return this.facing == oToCompare.facing
                    && super.equals(oToCompare)
                    && this.savedPeeks.equals(oToCompare.savedPeeks);
        }
        
        return false;
    }
}
