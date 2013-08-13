package taigore.buildapi.block;

import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

/**
 * Extension of StaticBlock, capable of reacting to rotation changes.
 * The id is still static, while the meta (indicating rotation) depends on the
 * rotation of both the block and the structure, but it's the unchanging for each
 * of the Rotation values.
 * @author Taigore
 */
public abstract class FacingBlock extends StaticBlock
{
    //The rotation of the block
    protected Rotation facing = Rotation.NO_ROTATION;
    
	protected FacingBlock(int id, Rotation facing) { super(id, 0); this.setRotation(facing); }
	
	public FacingBlock setRotation(Rotation facing) { this.facing = facing != null ? facing : Rotation.NO_ROTATION; return this; }
	public FacingBlock addRotation(Rotation facing) { this.facing.add(facing); return this; }
	
	/**
	 * Returns the metadata value of the block when facing this way.
	 * The reference (NO_ROTATION) is North (+Z)
     * @param facing - A Rotation value
     * @return A number between 0 and 15
     */
	abstract public int getMetaForFacing(Rotation facing);
	
	///////////
	// IBlock
	///////////
	@Override
    public BlockInfo peekNextBlock(World world, Vec3Int position, Rotation facing, Random generator) { return new BlockInfo(this.id, this.getMetaForFacing(facing), this.tileEntityData); }
	
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
            FacingBlock blockToCompare = (FacingBlock)toCompare;
            
            for(Rotation facing : Rotation.values())
                if(this.getMetaForFacing(facing) != blockToCompare.getMetaForFacing(facing))
                    return false;
            
            return super.equals(toCompare);
        }
        
        return false;
    }
}