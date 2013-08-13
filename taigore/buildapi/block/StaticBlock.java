package taigore.buildapi.block;


import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

public class StaticBlock implements IBlock
{
	protected int id = -1;
	protected int metadata = -1;
	protected NBTTagCompound tileEntityData = null;
	
	public StaticBlock(Block block) { this(block, 0); }
	public StaticBlock(Block block, int blockMeta) { this(block.blockID, blockMeta); }
	public StaticBlock(int blockID, int blockMeta) { this(blockID, blockMeta, null); }
	public StaticBlock(int blockID, int blockMeta, NBTTagCompound tileEntityData)
	{
	    this.id = blockID;
	    this.metadata = blockMeta;
	    this.tileEntityData = (NBTTagCompound) (tileEntityData != null ? tileEntityData.copy() : null);
	}
	public StaticBlock(StaticBlock toCopy)
	{
	    this(toCopy != null ? toCopy.id : -1,
	         toCopy != null ? toCopy.metadata : -1,
	         toCopy != null ? toCopy.tileEntityData : null);
	}
	
	///////////
	// IBlock
	///////////
	@Override
    public void placeBlock(World world, Vec3Int position, Rotation facing, Random generator)
    {
	    if(world != null && position != null)
	    {
            BlockInfo blockData = this.getNextBlock(world, position, facing, generator);
            
            if(blockData != null && blockData.isValid())
            {
                world.setBlock(position.x, position.y, position.z, blockData.id, blockData.meta, 2);
            
                TileEntity blockTileEntity = world.getBlockTileEntity(position.x, position.y, position.z);
                NBTTagCompound tileEntityData = blockData.getTileEntityData();
                
                if(tileEntityData != null)
                    blockTileEntity.readFromNBT(tileEntityData);
            }
	    }
    }
	
    @Override
    public BlockInfo peekNextBlock(World world, Vec3Int position, Rotation facing, Random generator) { return new BlockInfo(this.id, this.metadata, this.tileEntityData); }
    @Override
    public BlockInfo getNextBlock(World world, Vec3Int position, Rotation facing, Random generator) { return this.peekNextBlock(world, position, facing, generator); }

    ///////////
	// Object
    ///////////
	@Override
    public boolean equals(Object toCompare)
    {
	    if(toCompare == null) return false;
	    if(toCompare == this) return true;
	    
        if(this.getClass() == toCompare.getClass())
        {
            StaticBlock blockToCompare = (StaticBlock)toCompare;
            
            if(this.tileEntityData != null)
                return !this.tileEntityData.equals(blockToCompare.tileEntityData);
            else if(blockToCompare.tileEntityData != null)
                return false;
            
            return this.id == blockToCompare.id
                && this.metadata == blockToCompare.metadata;
        }
        
        return false;
    }
	@Override
    public StaticBlock clone()
	{
	    try
	    {
    	    StaticBlock returnValue = (StaticBlock)super.clone();
    	    
    	    if(returnValue.tileEntityData != null)
    	        returnValue.tileEntityData = (NBTTagCompound) returnValue.tileEntityData.copy();
    	    
    	    return returnValue;
	    }
	    catch (CloneNotSupportedException e)
        {
	        FMLLog.info("Clone failed");
            e.printStackTrace();
            
            return null;
        }
	}
}
