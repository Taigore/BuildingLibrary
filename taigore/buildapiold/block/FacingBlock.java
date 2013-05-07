package taigore.buildapi.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;

public class FacingBlock implements IAbstractBlock
{
	public static final FacingBlock stoneSmoothStairsPlusX = new FacingBlock(Block.stairsStoneBrick.blockID, 1, 3, 0, 2);
	public static final FacingBlock stoneSmoothStairsPlusXReversed = new FacingBlock(Block.stairsStoneBrick.blockID, 5, 7, 4, 6);
	public static final FacingBlock simpleChestPlusX = new FacingBlock(Block.chest.blockID, 5, 3, 4, 2);
	
	//Sub-blocks random choice
	protected int id = 0;
	protected int[] metadatas = new int[Rotation.values().length];
	protected Rotation rotation = Rotation.NO_ROTATION;
	
	/**
	 * This kind of block provides a different metadata depending
	 * on the rotation it is drawn with.
	 */
	public FacingBlock(int id, int defaultMeta, int clockwiseMeta, int reverseMeta, int countercwMeta)
	{
		this.resetId(id);
		this.resetMetaForFacing(defaultMeta, Rotation.NO_ROTATION);
		this.resetMetaForFacing(clockwiseMeta, Rotation.CLOCKWISE);
		this.resetMetaForFacing(reverseMeta, Rotation.REVERSE);
		this.resetMetaForFacing(countercwMeta, Rotation.COUNTERCLOCKWISE);
	}
	/**
	 * Copy constructor. Also copies the rotation of the block.
	 */
	public FacingBlock(FacingBlock toCopy)
	{
	    this.id = toCopy.id;
	    this.metadatas = toCopy.metadatas.clone();
	    this.rotation = toCopy.rotation;
	}
	
	/**
	 * Sets the block id to the one given.
	 * Default to 0 if less than 0, and takes only the lowest 12 bits.
	 */
	public FacingBlock resetId(int id)
	{
	    if(id < 0) id = 0;
	    this.id = id & 0x00000FFF;
	    return this;
	}
	/**
	 * Sets the block meta to the one given for the given facing.
	 * Default to 0 if less than 0, and takes only the lowest 4 bits.
	 * Nothing changes if the facing is invalid.
	 */
	public FacingBlock resetMetaForFacing(int meta, Rotation facing)
	{
	    if(facing != null)
	    {
	        if(meta < 0) meta = 0;
	        this.metadatas[facing.getID()] = meta & 0x0000000F;
	    }
	    return this;
	}
	
	/**
     * Sets the new base rotation for this block to the one given.
     * This means that the rotation given will be the new default,
     * and all subsequent rotation will be modified accordingly.
     */
    public FacingBlock rotate(Rotation rotation) { this.rotation = this.rotation.add(rotation); return this; }
    /**
     * Sets the block to the original facing.
     */
    public FacingBlock resetRotation() { this.rotation = Rotation.NO_ROTATION; return this; }

	@Override
	public void drawAt(Position startPosition, Rotation facing, Random generator, World canvas)
	{
		if(startPosition != null)
		{
			Rotation drawRotation = this.rotation.add(facing);
			int blockMeta = this.metadatas[drawRotation.getID()];
			
			canvas.setBlock(startPosition.x, startPosition.y, startPosition.z,
			                this.id, blockMeta,
			                3);
		}
	}
	
	@Override
	public boolean equals(Object toCompare)
	{
	    if(toCompare == this) return true;
	    
	    if(FacingBlock.class.isInstance(toCompare))
	    {
	        FacingBlock blockToCompare = (FacingBlock)toCompare;
	        
	        if(this.id == blockToCompare.id)
	        {
	            boolean isEqual = this.rotation == blockToCompare.rotation;
	            
	            for(int i = 0; isEqual && i < this.metadatas.length; ++i)
	                isEqual = this.metadatas[i] == blockToCompare.metadatas[i];
	            
	            return isEqual;
	        }
	    }
	    
	    return false;
	}
	@Override
	public int hashCode() { return ((this.metadatas[3] & 0xF) << 24) & ((this.metadatas[2] & 0xF) << 20) & ((this.metadatas[1] & 0xF) << 16) & ((this.metadatas[0] & 0xF) << 12) & this.id & 0xFFF; }
}