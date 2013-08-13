package taigore.buildapi.utils;

import java.util.Random;

import net.minecraft.world.World;

public class ParamSet
{
    private World world;
    private Rotation facing;
    private Random generator;
    
    public ParamSet(World world, Vec3Int position, Rotation facing, Random generator)
    {
        this.world = world;
        this.facing = facing;
        this.generator = generator;
    }
    
    @Override
    public boolean equals(Object toCompare)
    {
        if(this == toCompare) return true;
        
        if(this.getClass().isInstance(toCompare))
        {
            ParamSet paramsToCompare = (ParamSet)toCompare;
            
            return this.world.equals(paramsToCompare.world)
                && this.facing == paramsToCompare.facing
                && this.generator.equals(generator);
        }
        
        return false;
    }
}