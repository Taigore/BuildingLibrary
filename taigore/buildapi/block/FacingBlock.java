package taigore.buildapi.block;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;

public class FacingBlock implements IAbstractBlock
{
	public static final FacingBlock stoneSmoothStairsPlusX = makeFromIdAndMetas(Block.stairsStoneBrick.blockID, 1, 3, 0, 2);
	public static final FacingBlock stoneSmoothStairsPlusXReversed = makeFromIdAndMetas(Block.stairsStoneBrick.blockID, 5, 7, 4, 6);
	public static final FacingBlock simpleChestPlusX = makeFromIdAndMetas(Block.chest.blockID, 5, 3, 4, 2);
	
	//Sub-blocks random choice
	private Map<Rotation, IAbstractBlock> blockPairings = new EnumMap(Rotation.class);
	private Rotation rotation = Rotation.NO_ROTATION;
	
	/**
	 * Creates a facing block, pairing the four metadatas provided
	 * with the four facings.
	 */
	public static FacingBlock makeFromIdAndMetas(int id, int defaultMeta, int clockwiseMeta, int reverseMeta, int countercwMeta)
	{
		return new FacingBlock(new StaticBlock(id, defaultMeta),
							   new StaticBlock(id, clockwiseMeta),
							   new StaticBlock(id, reverseMeta),
							   new StaticBlock(id, countercwMeta));
	}
	/**
	 * This kind of block provides a different id and metadata depending on
	 * the given block facing.
	 * All provided values are associated with each facing as specified.
	 */
	private FacingBlock() {}
	public FacingBlock(IAbstractBlock defaultBlock, IAbstractBlock clockwiseBlock, IAbstractBlock reverseBlock, IAbstractBlock countercwBlock)
	{
		this.blockPairings.put(Rotation.NO_ROTATION, defaultBlock);
		this.blockPairings.put(Rotation.CLOCKWISE, clockwiseBlock);
		this.blockPairings.put(Rotation.REVERSE, reverseBlock);
		this.blockPairings.put(Rotation.COUNTERCLOCKWISE, countercwBlock);
	}

	@Override
	public void drawAt(Position startPosition, Rotation facing, Random generator, World canvas)
	{
		if(startPosition != null)
		{
			Rotation drawRotation = this.rotation.add(facing);
			IAbstractBlock drawing = this.blockPairings.get(drawRotation);
			
			if(drawing != StaticBlock.noEdit)
				drawing.drawAt(startPosition, drawRotation, generator, canvas);
		}
	}
	
	/**
	 * Sets the new base rotation for this block to the one given.
	 * This means that the rotation given will be the new default,
	 * and all subsequent rotation will be modified accordingly.
	 */
	public FacingBlock rotate(Rotation rotation)
	{
		this.rotation = this.rotation.add(rotation);
		
		return this;
	}
	@Override
	public FacingBlock copy()
	{
		FacingBlock copy = new FacingBlock();
		copy.blockPairings.putAll(this.blockPairings);
		copy.rotation = this.rotation;
		return copy;
	}
}
