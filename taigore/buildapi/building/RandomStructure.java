package taigore.buildapi.building;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.utils.ParamSet;
import taigore.buildapi.utils.RandomSelector;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

public class RandomStructure extends RandomSelector<IStructureGenerator> implements IStructureGenerator
{
    //Peeks cache, to return the same result with the same parameters, until getNext is called
    private Map<ParamSet, IStructureGenerator> savedPeeks = new HashMap();
    //All buildings returned will have this rotation added
    private Rotation facing = Rotation.NO_ROTATION;
    
    /**
     * Adds several structures to this random structure, all with probability 1.
     * A list with duplicates will add the duplicate with probability 2, 3 and so on,
     * based on how many times the IStructureGenerator appears in the list.
     */
    public RandomStructure addBuildings(IStructureGenerator...toAdd)
    {
        Map<IStructureGenerator, Integer> added = new HashMap();
        
        for(IStructureGenerator structure : toAdd)
            added.put(structure, added.containsKey(structure) ? added.get(structure) : 1);
        
        for(IStructureGenerator structure : added.keySet())
            this.addBuilding(structure, added.get(structure));
        
        return this;
    }
	/**
	 * Adds a building for random generation.
	 * If probability is 0, nothing happens.
	 * If probability is positive:
	 *     -If toAdd was already added, probabilities will add and increase the likeliness of its selection
	 *     -If toAdd wasn't added, it will be added with that probability of being chosen
	 * If probability is negative:
	 *     -If toAdd was already added, probability will be subtracted, and if total probability is 0 or less, the building will be removed
	 *     -If toAdd wasn't added, nothing will happen
	 */
	public RandomStructure addBuilding(IStructureGenerator toAdd, int probability)
	{
	    if(probability > 0 || (probability < 0 && this.getProbability(toAdd) > 0))
	        this.savedPeeks.clear();
	    
		this.addObject(toAdd, probability);
		
		return this;
	}
	
	public RandomStructure setRotation(Rotation facing) { this.facing = facing != null ? facing : Rotation.NO_ROTATION; return this; }
    public RandomStructure addRotation(Rotation facing) { this.facing = this.facing.add(facing); return this; }
	////////////////////////
	// IStructureGenerator
	////////////////////////
    @Override
    public IStructureGenerator peekNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        IStructureGenerator returnValue = null;
        facing = this.facing.add(facing);
        
        ParamSet paramSet = new ParamSet(world, startPosition, facing, generator);
        
        if(this.savedPeeks.containsKey(paramSet))
            returnValue = this.savedPeeks.get(paramSet);
        else
        {
            IStructureGenerator selected = this.select(generator);
            
            if(selected != null)
                returnValue = selected.peekNextStructure(world, startPosition, facing, generator);
            
            this.savedPeeks.put(paramSet, returnValue);
        }
        
        if(returnValue != null)
            returnValue = returnValue.clone();
        
        return returnValue;
    }
    @Override
    public IStructureGenerator getNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        IStructureGenerator returnValue = null;
        
        ParamSet paramSet = new ParamSet(world, startPosition, facing, generator);
        
        facing = this.facing.add(facing);
        
        if(this.savedPeeks.containsKey(paramSet))
            returnValue = this.savedPeeks.remove(paramSet);
        else
        {
            IStructureGenerator selected = this.select(generator);
            
            if(selected != null)
                returnValue = selected.getNextStructure(world, startPosition, facing, generator);
        }
        
        this.savedPeeks.clear();
        return returnValue;
    }
    @Override
    public void placeNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        IStructureGenerator toPlace = this.getNextStructure(world, startPosition, facing, generator);
        
        if(toPlace != null)
            toPlace.placeNextStructure(world, startPosition, Rotation.NO_ROTATION, generator);
    }
    
    
    ///////////
    // Object
    ///////////
    @Override
    public RandomStructure clone()
	{
        RandomStructure returnValue = (RandomStructure)super.clone();
        
        returnValue.savedPeeks = new HashMap(this.savedPeeks);
        
        return returnValue;
	}
    
    @Override
    public boolean equals(Object toCompare)
    {
        if(toCompare == null) return false;
        if(this == toCompare) return true;
        
        if(this.getClass().isInstance(toCompare))
        {
            RandomStructure oToCompare = (RandomStructure) toCompare;
            
            return this.facing == oToCompare.facing
                && super.equals(oToCompare)
                && this.savedPeeks.equals(oToCompare.savedPeeks);
        }
        
        return false;
    }
}
