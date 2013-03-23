package taigore.buildapi.building;

import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;

public interface IAbstractBuilding
{
	/**
	 * Draws the building in the world, at the specified position as the origin.
	 * The building will be rotated according to facing, but the origin point
	 * will always be (rotationX, rotationY, rotationZ).
	 * Uses generator for random generation.
	 */
	public abstract void drawOnTheWorld(Position drawOrigin, Rotation facing, Random generator, World canvas);
	/**
	 * Rotates the building. All drawOnTheWorld calls with NO_ROTATION will
	 * actually use this rotation, and all other facings will be shifted accordingly.
	 */
	public abstract IAbstractBuilding rotate(Rotation rotation);
	/**
	 * Creates a copy of this building.
	 */
	public abstract IAbstractBuilding copy();
}
