package taigore.buildapi.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import taigore.buildapi.Rotation;
import taigore.buildapi.Vec3Int;
import taigore.buildapi.utils.RandomSelector;

public class RandomBlock extends RandomSelector implements IBlock
{
    private Map<ParamSet, BlockInfo> savedPeeks = new HashMap();
    
    public RandomBlock() {};
    public RandomBlock(RandomBlock toCopy)
    {
        this.probabilityMap.putAll(toCopy.probabilityMap);
        this.totalProbability = toCopy.totalProbability;
        this.savedPeeks.putAll(toCopy.savedPeeks);
    }
    
	public RandomBlock addBlocks(IBlock...toAdd)
	{
	    for(IBlock block : toAdd)
	        this.addBlock(block, 1);
	    
	    return this;
	};
	/**
	 * Adds a block to the selection this block has.
	 * If spawn chance is 0 nothing happens.
	 * If spawn chance is positive and the block is already included, the
	 * chance of it getting selected increases.
	 * If spawn chance is negative and the block is already included, its
	 * spawn chance get reduced up to 0, then it is removed.
	 * If the block is not included, nothing happens
	 */
	public RandomBlock addBlock(IBlock toAdd, int spawnChance)
	{
		this.addObject(toAdd.copy(), spawnChance);
		
		return this;
	}
	
	///////////
	// IBlock
	///////////
    @Override
    public void placeBlock(World world, Vec3Int position, Rotation facing, Random generator)
    {
        BlockInfo toPlace = this.getNextBlock(world, position, facing, generator);
        
        if(toPlace != null && toPlace.isValid())
        {
            world.setBlock(position.x, position.y, position.z, toPlace.id, toPlace.meta, 2);
            
            TileEntity blockTileEntity = world.getBlockTileEntity(position.x, position.y, position.z);
            NBTTagCompound tileEntityData = toPlace.getTileEntityData();
            
            if(tileEntityData != null && blockTileEntity != null)
                blockTileEntity.readFromNBT(tileEntityData);
        }
    }
    @Override
    public BlockInfo peekNextBlock(World world, Vec3Int position, Rotation facing, Random generator)
    {
        BlockInfo returnValue = null;
        
        if(world != null && position != null)
        {
            if(facing == null)
                facing = Rotation.NO_ROTATION;
            
            ParamSet paramSet = new ParamSet();
            paramSet.world = world;
            paramSet.position = position;
            paramSet.facing = facing;
            paramSet.generator = generator;
            
            if(this.savedPeeks.containsKey(paramSet))
                returnValue = this.savedPeeks.get(paramSet);
            else
            {
                IBlock selected = (IBlock) this.select(generator);
                
                if(selected != null)
                    returnValue = selected.peekNextBlock(world, position, facing, generator);
                
                this.savedPeeks.put(paramSet, returnValue);
            }
        }
        
        return returnValue;
    }
    @Override
    public BlockInfo getNextBlock(World world, Vec3Int position, Rotation facing, Random generator)
    {
        BlockInfo returnValue = null;
        
        if(world != null && position != null)
        {
            if(facing == null)
                facing = Rotation.NO_ROTATION;
            
            ParamSet paramSet = new ParamSet();
            paramSet.world = world;
            paramSet.position = position;
            paramSet.facing = facing;
            paramSet.generator = generator;
            
            if(this.savedPeeks.containsKey(paramSet))
                returnValue = this.savedPeeks.remove(paramSet);
            else
            {
                IBlock selected = (IBlock) this.select(generator);
                
                if(selected != null)
                    returnValue = selected.getNextBlock(world, position, facing, generator);
            }
        }
        
        return returnValue;
    }
    @Override
    public IBlock copy() { return new RandomBlock(this); }
	
	//TODO Equals and hashCode
    
    static private class ParamSet
    {
        public World world;
        public Vec3Int position;
        public Rotation facing;
        public Random generator;
        
        @Override
        public boolean equals(Object toCompare)
        {
            if(this == toCompare) return true;
            
            if(this.getClass().isInstance(toCompare))
            {
                ParamSet paramsToCompare = (ParamSet)toCompare;
                
                return this.world.equals(paramsToCompare.world)
                    && this.position.equals(paramsToCompare)
                    && this.facing.equals(facing)
                    && this.generator.equals(generator);
            }
            
            return false;
        }
    }
}
