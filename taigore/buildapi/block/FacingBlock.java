package taigore.buildapi.block;

import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Rotation;
import taigore.buildapi.Vec3Int;

//TODO Documentation
public abstract class FacingBlock extends StaticBlock
{
	/**
	 * This kind of block provides a different metadata depending
	 * on the rotation it is drawn with.
	 */
	protected FacingBlock(int id) { super(id, 0); }
	
	/**
	 * Returns the metadata value that peekNextBlock should have when called with facing
     * @param facing - A valid Rotation value
     * @return A valid metadata value, if the facing is valid
     */
	abstract public int getMetaForFacing(Rotation facing);
	
	@Override
    public BlockInfo peekNextBlock(World world, Vec3Int position, Rotation facing, Random generator) { return new BlockInfo(this.id, this.getMetaForFacing(facing), this.tileEntityData); }
}