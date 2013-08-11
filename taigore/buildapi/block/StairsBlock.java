package taigore.buildapi.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import taigore.buildapi.Rotation;

public class StairsBlock extends FacingBlock
{
    //Metadata values for rotation.
    //Value 0 has the stairs with the back facing North, then clockwise from there
    public static int[] rotationMetaNormal0BackToNorth = {3, 0, 2, 1};
    
    //If the stairs are upside down
    public boolean upsideDown = false;
    private Rotation baseRotation = Rotation.NO_ROTATION;
    
    public StairsBlock(StairsType type, Rotation rotationFromNorth, boolean upsideDown)
    {
        super(type.type.blockID);
        this.upsideDown = upsideDown;
        this.baseRotation = rotationFromNorth;
    }
    public StairsBlock(StairsBlock toCopy)
    {
        super(toCopy.id);
        this.upsideDown = toCopy.upsideDown;
        this.baseRotation = toCopy.baseRotation;
    }
    
    ////////////////
    // FacingBlock
    ////////////////
    @Override
    public int getMetaForFacing(Rotation facing)
    {
        facing = this.baseRotation.add(facing);
        
        return rotationMetaNormal0BackToNorth[facing.getIndex()] | (this.upsideDown ? 4 : 0);
    }
    
    ///////////
    // IBlock
    ///////////
    @Override
    public StairsBlock copy() { return new StairsBlock(this); }
    
    ///////////
    // Object
    ///////////
    @Override
    public boolean equals(Object toCompare)
    {
        if(this == toCompare) return true;
        
        if(this.getClass().isInstance(toCompare))
        {
            StairsBlock stairsToCompare = (StairsBlock)toCompare;
            
            return super.equals(toCompare)
                && this.upsideDown == stairsToCompare.upsideDown
                && this.baseRotation == stairsToCompare.baseRotation;
        }
            
        
        return false;
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
