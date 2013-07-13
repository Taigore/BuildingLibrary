package taigore.buildapi.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import taigore.buildapi.Rotation;
import taigore.buildapi.Vec3Int;

public interface IBlock
{
	/**
	 * This function performs the actual block placement, or none if the block
	 * decides to do so.
	 */
	int getBlockID(World world, Vec3Int position, Rotation facing);
	
	int getBlockMeta(World world, Vec3Int position, Rotation facing);
	
	NBTTagCompound getBlockTileEntityNBT(World world, Vec3Int position, Rotation facing);
}
