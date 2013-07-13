package taigore.buildapi.building;

import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Vec3Int;
import taigore.buildapi.Rotation;

public abstract class AbstractBuilding
{
    protected Rotation rotation = Rotation.NO_ROTATION;
    
	/**
	 * Draws the building in the world, at the specified position as the origin.
	 * The building will be rotated according to facing, but the origin point
	 * will always be (rotationX, rotationY, rotationZ).
	 * Uses generator for random generation.
	 */
	abstract void drawOnTheWorld(Vec3Int drawOrigin, Rotation facing, Random generator, World canvas);
	/**
	 * Rotates the building. All drawOnTheWorld calls with NO_ROTATION will
	 * actually use this rotation, and all other facings will be shifted accordingly.
	 */
	public AbstractBuilding rotate(Rotation rotation) { this.rotation = this.rotation.add(rotation); return this;}
	/**
	 * Brings the building back to its original facing.
	 */
	public AbstractBuilding resetRotation() { this.rotation = Rotation.NO_ROTATION; return this;}
}
