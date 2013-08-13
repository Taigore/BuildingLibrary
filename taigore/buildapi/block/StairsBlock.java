package taigore.buildapi.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import taigore.buildapi.utils.Rotation;

/**
 * Easy declaration of stairs FacingBlock
 * @author Taigore
 */
public class StairsBlock extends FacingBlock
{
    //Metadata values for rotation.
    //Value 0 has the stairs with the back facing North, then clockwise from there
    public static int[] rotationMetaNormal0BackToNorth = {3, 0, 2, 1};
    
    //If the stairs are upside down
    public boolean upsideDown = false;
    
    public StairsBlock(StairsType type, Rotation rotationFromNorth, boolean upsideDown)
    {
        super(type.type.blockID, rotationFromNorth);
        this.upsideDown = upsideDown;
    }
    
    ////////////////
    // FacingBlock
    ////////////////
    @Override
    public int getMetaForFacing(Rotation facing)
    {
        facing = this.facing.add(facing);
        
        return rotationMetaNormal0BackToNorth[facing.getIndex()] | (this.upsideDown ? 4 : 0);
    }
    
//////////////////////////////////////////////////////////////////////////////////////////////////
    public static enum StairsType
    {
        BRICK((BlockStairs)Block.stairsBrick),
        COBBLESTONE((BlockStairs)Block.stairsCobblestone),
        NETHERBRICK((BlockStairs)Block.stairsNetherBrick),
        NETHERQUARTZ((BlockStairs)Block.stairsNetherQuartz),
        SANDSTONE((BlockStairs)Block.stairsSandStone),
        STONEBRICK((BlockStairs)Block.stairsStoneBrick),
        WOODBIRCH((BlockStairs)Block.stairsWoodBirch),
        WOODJUNGLE((BlockStairs)Block.stairsWoodJungle),
        WOODOAK((BlockStairs)Block.stairsWoodOak),
        WOODSPRUCE((BlockStairs)Block.stairsWoodSpruce);
        
        public final BlockStairs type;
        
        StairsType(BlockStairs type)
        {
            if(type == null)
                throw new IllegalArgumentException("Taigore Building Library - unable to initialize enum StairsType: null stair type");
            
            this.type = type;
        }
    }
}
