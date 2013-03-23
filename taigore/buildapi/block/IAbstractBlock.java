package taigore.buildapi.block;

import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;

public interface IAbstractBlock
{
	/**
	 * This function performs the actual block placement, or none if the block
	 * decides to do so.
	 */
	public void drawAt(Position startPosition, Rotation facing, Random generator, World canvas);
	/**
	 * Rotates this block and all child blocks.
	 */
	public IAbstractBlock rotate(Rotation rotation);
	/**
	 * Creates a copy of this block.
	 */
	public abstract IAbstractBlock copy();
}
