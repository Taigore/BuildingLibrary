package taigore.buildapi.building;

import cpw.mods.fml.common.FMLLog;
import taigore.buildapi.block.IBlock;
import taigore.buildapi.block.IBlock.BlockInfo;
import taigore.buildapi.block.StaticBlock;
import taigore.buildapi.utils.Rotation;

public class StaticBlock3DMap extends Block3DMap
{
    public StaticBlock3DMap(int sizeX, int sizeY, int sizeZ, IBlock filler) { super(sizeX, sizeY, sizeZ, filler); }
    public StaticBlock3DMap(Block3DMap toCopy)
    {
        this(toCopy.sizeX, toCopy.sizeY, toCopy.sizeZ, null);
        
         for(int i = 0; i < this.sizeX; ++i)
             for(int j = 0; j < this.sizeY; ++j)
                 for(int k = 0; k < this.sizeZ; ++k)
                 {
                     try
                     {
                         IBlock copyBlock = toCopy.getSpot(i, j, k);
                         this.setSpot(i, j, k, copyBlock);
                     }
                     catch(Exception e)
                     {
                         FMLLog.warning("Taigore Building Library: exception while converting Block3DMap to Static");
                         FMLLog.warning(e.getMessage());
                         
                         Throwable cause = e.getCause();
                         
                         while(cause != null)
                         {
                             FMLLog.warning(cause.getMessage());
                             cause = cause.getCause();
                         }
                     }
                 }
    }
    
    @Override
    public StaticBlock3DMap setSpot(int posX, int posY, int posZ, IBlock material) throws Exception
    {
        if(material != null && !StaticBlock.class.isInstance(material))
        {
            BlockInfo blockInfo = material.getNextBlock(null, null, Rotation.NO_ROTATION, null);
            
            if(blockInfo != null && blockInfo.isValid())
                material = new StaticBlock(blockInfo.id, blockInfo.meta, blockInfo.getTileEntityData());
            else
                material = null;
        }
        
        super.setSpot(posX, posY, posZ, material);
        
        return this;
    }
}
