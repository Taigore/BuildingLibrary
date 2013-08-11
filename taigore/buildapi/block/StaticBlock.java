package taigore.buildapi.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import taigore.buildapi.Rotation;
import taigore.buildapi.Vec3Int;

public class StaticBlock implements IBlock
{
	//Common use blocks
    public static final StaticBlock noEdit = null;
	public static final StaticBlock air = new StaticBlock(0, 0);
	public static final StaticBlock ironBars = new StaticBlock(Block.fenceIron, 0);
	
	protected int id = -1;
	protected int metadata = -1;
	protected NBTTagCompound tileEntityData = null;
	
	public StaticBlock(Block block) { this(block, 0); }
	public StaticBlock(Block block, int blockMeta) { this(block.blockID, blockMeta); }
	public StaticBlock(int blockID, int blockMeta) { this(blockID, blockMeta, null); }
	public StaticBlock(int blockID, int blockMeta, NBTTagCompound tileEntityData) { this.setID(blockID).setMeta(blockMeta).setTileEntityData(tileEntityData); }
	public StaticBlock(StaticBlock toCopy)
	{
	    this(toCopy != null ? toCopy.id : -1,
	         toCopy != null ? toCopy.metadata : -1,
	         toCopy != null ? toCopy.tileEntityData : null);
	}
	
	/**
	 * Sets the ID of the block to the given number.
	 * Will set to 0 if id is less than 0, otherwise takes the lowest 12 bits.
	 */
	public StaticBlock setID(int id)
	{
	    this.id = id;
	    return this;
	}
	/**
	 * Sets the metadata of the block to the given number.
	 * Will set to 0 if metadata is less than 0, otherwise takes the lowest 4 bits.
	 */
	public StaticBlock setMeta(int metadata)
	{
	    this.metadata = metadata;
	    return this;
	}
	/**
	 * Copies the NBTTagCompound provided, to use it in the placement of the block, if
	 * the Block type has a tile entity.
	 */
	public StaticBlock setTileEntityData(NBTTagCompound tileEntityData) { this.tileEntityData = (NBTTagCompound)(tileEntityData != null ? tileEntityData.copy() : null); return this; }
	
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
	public StaticBlock copy() { return new StaticBlock(this); }
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
	    if(toCompare == this) return true;
	    
        if(StaticBlock.class.isInstance(toCompare))
        {
            StaticBlock blockToCompare = (StaticBlock)toCompare;
            
            return this.id == blockToCompare.id && this.metadata == blockToCompare.metadata && this.tileEntityData.equals(blockToCompare.tileEntityData);
        }
        else return false;
    }
    @Override
    public int hashCode() { return ((this.metadata & 0x0000000F) << 12) | (this.id & 0x00000FFF); }
}
