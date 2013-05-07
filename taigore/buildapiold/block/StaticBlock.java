package taigore.buildapi.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;

public class StaticBlock implements IAbstractBlock
{
	//Common use blocks
	public static final StaticBlock air = new StaticBlock(0, 0);
	public static final StaticBlock ironBars = new StaticBlock(Block.fenceIron.blockID, 0);
	public static final StaticBlock noEdit = null;
	
	private int id = 0;
	private int metadata = 0;
	/**
	 * Constructor for defining a block from its ID.
	 * Assumes a default metadata of 0.
	 */
	public StaticBlock(int id) { this(id, 0); }
	/**
	 * Constructor for defining a block with ID and metadata.
	 */
	public StaticBlock(int id, int metadata) { this.resetId(id).resetMeta(metadata); }
	/**
	 * Copy constructor.
	 */
	public StaticBlock(StaticBlock toCopy) { this.id = toCopy.id; this.metadata = toCopy.metadata; }
	/**
	 * Sets the ID of the block to the given number.
	 * Will set to 0 if id is less than 0, otherwise takes the lowest 12 bits.
	 */
	public StaticBlock resetId(int id)
	{
	    if(id < 0) id = 0;
	    this.id = id & 0x00000FFF;
	    return this;
	}
	/**
	 * Sets the metadata of the block to the given number.
	 * Will set to 0 if metadata is less than 0, otherwise takes the lowest 4 bits.
	 */
	public StaticBlock resetMeta(int metadata)
	{
	    if(metadata < 0) metadata = 0;
	    this.metadata = metadata & 0x0000000F;
	    return this;
	}

	@Override
	public void drawAt(Position startPosition, Rotation facing, Random generator, World canvas)
	{
		if(canvas != null && startPosition != null)
			canvas.setBlock(startPosition.x, startPosition.y, startPosition.z, this.id, this.metadata, 0);
	}
	
	@Override
    public boolean equals(Object toCompare)
    {
        if(toCompare != null && toCompare.getClass() == this.getClass())
        {
            StaticBlock blockToCompare = (StaticBlock)toCompare;
            
            return this.id == blockToCompare.id && this.metadata == blockToCompare.metadata;
        }
        else return false;
    }
    @Override
    public int hashCode() { return ((this.metadata & 0x0000000F) << 12) | (this.id & 0x00000FFF); }
}
