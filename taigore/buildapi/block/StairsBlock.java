package taigore.buildapi.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import taigore.buildapi.Rotation;
import taigore.buildapi.Vec3Int;

public class StairsBlock extends FacingBlock
{
    //TODO THIS!
    @Override
    public int getBlockID(World world, Vec3Int position, Rotation facing)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public int getBlockMeta(World world, Vec3Int position, Rotation facing)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public NBTTagCompound getBlockTileEntityNBT(World world, Vec3Int position,
            Rotation facing)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
