package taigore.buildapi.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import taigore.buildapi.utils.ParamSet;
import taigore.buildapi.utils.RandomSelector;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

public class RandomBlock extends RandomSelector<IBlock> implements IBlock
{
    //The previous results of peekNextBlock, to allow the method to return the same
    //value, until getNextBlock is called or the object is modified.
    private Map<ParamSet, BlockInfo> savedPeeks = new HashMap();
    
    //The base rotation of the block (relevant if FacingBlocks can be selected in any number of steps)
    private Rotation facing = Rotation.NO_ROTATION;
    
    public RandomBlock() {};
    
    public RandomBlock addBlocks(IBlock...toAdd)
    {
        Map<IBlock, Integer> added = new HashMap();
        
        for(IBlock block : toAdd)
            added.put(block, added.containsKey(block) ? added.get(block) : 1);
        
        for(IBlock block : added.keySet())
            this.addBlock(block, added.get(block));
        
        return this;
    }
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
	    this.savedPeeks.clear();
	    
		this.addObject(toAdd, spawnChance);
		
		return this;
	}
	
	public RandomBlock setRotation(Rotation facing) { this.facing = facing != null ? facing : Rotation.NO_ROTATION; return this; }
    public RandomBlock addRotation(Rotation facing) { this.facing.add(facing); return this; }
	
	///////////
	// IBlock
	///////////
    @Override
    public BlockInfo peekNextBlock(World world, Vec3Int position, Rotation facing, Random generator)
    {
        BlockInfo returnValue = null;
        
        facing = this.facing.add(facing);
        
        ParamSet paramSet = new ParamSet(world, position, facing, generator);
        
        if(this.savedPeeks.containsKey(paramSet))
            returnValue = this.savedPeeks.get(paramSet);
        else
        {
            IBlock selected = this.select(generator);
            
            if(selected != null)
                returnValue = selected.peekNextBlock(world, position, facing, generator);
            
            this.savedPeeks.put(paramSet, returnValue);
        }
        
        return returnValue;
    }
    @Override
    public BlockInfo getNextBlock(World world, Vec3Int position, Rotation facing, Random generator)
    {
        BlockInfo returnValue = null;
        
        facing = this.facing.add(facing);
        
        ParamSet paramSet = new ParamSet(world, position, facing, generator);
        
        if(this.savedPeeks.containsKey(paramSet))
            returnValue = this.savedPeeks.remove(paramSet);
        else
        {
            IBlock selected = this.select(generator);
            
            if(selected != null)
                returnValue = selected.getNextBlock(world, position, facing, generator);
        }
        
        this.savedPeeks.clear();
        return returnValue;
    }
    @Override
    public void placeBlock(World world, Vec3Int position, Rotation facing, Random generator)  throws IllegalArgumentException
    {
        if(world != null && position != null)
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
    }
    
    ///////////
    // Object
    ///////////
    @Override
    public RandomBlock clone()
	{
        RandomBlock returnValue = (RandomBlock)super.clone();
        
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
	        RandomBlock blockToCompare = (RandomBlock) toCompare;
	        
	        return super.equals(blockToCompare)
	            && this.facing == blockToCompare.facing
	            && this.savedPeeks.entrySet().equals(blockToCompare.savedPeeks);
	    }
	    
	    return false;
	}
}
