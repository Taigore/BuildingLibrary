package taigore.buildapi.block;

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
	
	private int id = 0;
	private int metadata = 0;
	private NBTTagCompound tileEntityData = null;
	
	public StaticBlock(Block block, int blockMeta) { this(block.blockID, blockMeta); }
	public StaticBlock(int blockID, int blockMeta) { this.setID(blockID).setMeta(blockMeta); }
	public StaticBlock(StaticBlock toCopy) { this.id = toCopy.id; this.metadata = toCopy.metadata; }
	
	/**
	 * Sets the ID of the block to the given number.
	 * Will set to 0 if id is less than 0, otherwise takes the lowest 12 bits.
	 */
	public StaticBlock setID(int id)
	{
	    if(id < 0) id = 0;
	    this.id = id & 0x00000FFF;
	    return this;
	}
	/**
	 * Sets the metadata of the block to the given number.
	 * Will set to 0 if metadata is less than 0, otherwise takes the lowest 4 bits.
	 */
	public StaticBlock setMeta(int metadata)
	{
	    if(metadata < 0) metadata = 0;
	    this.metadata = metadata & 0x0000000F;
	    return this;
	}
	/**
	 * Saves the properties of the TileEntity provided, to replicate
	 * them in every block placed.
	 */
	public StaticBlock setTileEntityData(TileEntity toSave)
	{
	    if(toSave == null)
	        this.tileEntityData = null;
	    else
	    {
	        if(this.tileEntityData == null)
	            this.tileEntityData = new NBTTagCompound();
	        
	        toSave.writeToNBT(this.tileEntityData);
	    }
	    
	    return this;
	}
	
	///////////
	// IBlock
	///////////
	@Override
    public int getBlockID(World world, Vec3Int position, Rotation facing) { return this.id; }
    @Override
    public int getBlockMeta(World world, Vec3Int position, Rotation facing) { return this.metadata; }
    @Override
    public NBTTagCompound getBlockTileEntityNBT(World world, Vec3Int position, Rotation facing)
    {
        if(Block.blocksList[this.id].hasTileEntity(this.metadata))
            return this.tileEntityData;
        else
            return null;
    }
	
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
            
            return this.id == blockToCompare.id && this.metadata == blockToCompare.metadata;
        }
        else return false;
    }
    @Override
    public int hashCode() { return ((this.metadata & 0x0000000F) << 12) | (this.id & 0x00000FFF); }
}
