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
	
	private final int id;
	private final int metadata;
	/**
	 * Constructor for defining a block from its ID.
	 * Specifies a default metadata of 0.
	 */
	public StaticBlock(int id) { this(id, 0); }
	/**
	 * Constructor for defining a block with ID and metadata.
	 */
	public StaticBlock(int id, int metadata)
	{
		if(id < 0 || metadata < 0)
			throw new IllegalArgumentException(String.format("Invalid block data. Id: %d\tMeta: %d", id, metadata));
		
		this.id = id & 0x0FFF;
		this.metadata = metadata & 0x000F;
	}

	@Override
	public void drawAt(Position startPosition, Rotation facing, Random generator, World canvas)
	{
		if(startPosition != null)
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
	public StaticBlock copy()
	{
		return new StaticBlock(this.id, this.metadata);
	}
	@Override
	public StaticBlock rotate(Rotation rotation) { return this; }
}
