package taigore.buildapi.block;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

public interface IBlock extends Cloneable
{
    /**
     * Returns the next BlockInfo that will be used to place a new block in the world
     * with placeBlock. Any number of subsequent calls to this method must return copies
     * of the same block data, unless getNextBlock is called in between.
     * @param world - A world
     * @param position - A position
     * @param facing - How the block is rotated. Some kinds of blocks may ignore it
     * @param generator - A random number generator. Some kinds of blocks may ignore it
     * @return A block info instance. May not be a valid one (returns false on isValid())
     */
    BlockInfo peekNextBlock(World world, Vec3Int position, Rotation facing, Random generator);
    /**
     * Returns the next BlockInfo that will be used to place a new block in the world
     * with placeBlock. Calling this method may change the next block placed, and the result
     * of peekNextBlock, in addition to the return value of this method
     * @param world - A world
     * @param position - A position
     * @param facing - How the block is rotated. Some kinds of blocks may ignore it
     * @param generator - A random number generator. Some kinds of blocks may ignore it
     * @return A block info instance. May not be a valid one (returns false on isValid())
     */
    BlockInfo getNextBlock(World world, Vec3Int position, Rotation facing, Random generator);
    /**
     * Places the block in world, ideally using getNextBlock with the same parameters
     * to get the id and meta to place, along with the tileEntityData.
     * @param world - A valid world
     * @param position - A valid position in world
     * @param facing - How the block is rotated. Some kinds of blocks may ignore it
     * @param generator - A random number generator. Some kinds of blocks may ignore it
     */
    void placeBlock(World world, Vec3Int position, Rotation facing, Random generator);
    
    
    IBlock clone();
    
	static class BlockInfo
	{
	    public final int id;
	    public final int meta;
	    private final NBTTagCompound tileEntityData;
	    
	    protected BlockInfo(int blockID, int blockMeta, NBTTagCompound tileEntityData)
	    {
	        this.id = blockID;
	        this.meta = blockMeta;
	        this.tileEntityData = (NBTTagCompound)(tileEntityData != null ? tileEntityData.copy() : null);
	    }
	    
	    public final NBTTagCompound getTileEntityData() { return (NBTTagCompound)(this.tileEntityData != null ? this.tileEntityData.copy() : null); }
	    
	    public final boolean isValid() { return this.id >= 0 && this.meta >= 0; } 
	}
}
