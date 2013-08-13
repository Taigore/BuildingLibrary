package taigore.buildapi.entity;

import java.util.Random;

import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;
import taigore.buildapi.utils.Vec3Int;

public interface IEntitySpawn
{
    /**
     * Adds the defined NBT tag to any entity spawned by this.
     * Returns itself.
     */
    IEntitySpawn setTag(String tagName, NBTBase tagValue);
    /**
     * Places a new entity from this spawn at the given position,
     * in the given world.
     */
    void placeInPosition(Vec3Int spawnPosition, Random generator, World canvas);
}
