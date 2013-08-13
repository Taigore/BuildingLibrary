package taigore.buildapi.complex;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.utils.Pair;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;
import cpw.mods.fml.common.FMLLog;

public class LinkedComplex implements IComplexGenerator
{
    private List<Pair<IComplexGenerator, Vec3Int>> complexes = new LinkedList();
    private Rotation facing = Rotation.NO_ROTATION;
    
    public void addComplex(IComplexGenerator toPlace, int offsetX, int offsetY, int offsetZ)
    {
        if(toPlace != null)
        {
            Vec3Int offset = new Vec3Int(offsetX, offsetY, offsetZ);
            
            Pair<IComplexGenerator, Vec3Int> toAdd = new Pair(toPlace, offset);
            
            this.complexes.add(toAdd);
        }
    }
    public void addRepeatingComplex(IComplexGenerator toPlace, Vec3Int startPosition, Vec3Int iterationOffset, int iterations)
    {
        if(toPlace != null && startPosition != null && iterationOffset != null && iterations > 0)
        {
            Vec3Int offset = new Vec3Int();
            
            for(int i = 0; i < iterations; ++i)
            {
                offset.reset(iterationOffset);
                offset.x *= i;
                offset.y *= i;
                offset.z *= i;
                offset.addCoordinates(startPosition);
                
                this.addComplex(toPlace, offset.x, offset.y, offset.z);
            }
        }
    }
    
    public LinkedComplex setRotation(Rotation facing) { this.facing = facing != null ? facing : Rotation.NO_ROTATION; return this; }
    public LinkedComplex addRotation(Rotation facing) { this.facing = this.facing.add(facing); return this; }
    
    /////////////////////
    //IComplexGenerator
    /////////////////////
    @Override
    public IComplexGenerator peekNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator) { return this; }
    @Override
    public IComplexGenerator getNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator) { return this.peekNextComplex(world, startPosition, facing, generator); }
    @Override
    public void placeNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        facing = this.facing.add(facing);
        
        Vec3Int offset = new Vec3Int();
        
        for(Pair<IComplexGenerator, Vec3Int> toPlace : this.complexes)
        {
            if(toPlace != null && toPlace.first != null)
            {
                offset.reset(0, 0, 0);
                
                if(toPlace.second != null)
                    offset.reset(toPlace.second);
                
                toPlace.first.placeNextComplex(world, offset.addCoordinates(startPosition), facing, generator);
            }
        }
    }
    
    ///////////
    // Object
    ///////////
    @Override
    public LinkedComplex clone()
    {
        try
        {
            LinkedComplex returnValue = (LinkedComplex)super.clone();
            
            returnValue.complexes.clear();
            
            for(Pair<IComplexGenerator, Vec3Int> toCopy : this.complexes)
                returnValue.addComplex(toCopy.first, toCopy.second.x, toCopy.second.y, toCopy.second.z);
            
            return returnValue;
        }
        catch (CloneNotSupportedException e)
        {
            FMLLog.info("Clone failed");
            e.printStackTrace();
            
            return null;
        }
    }
    @Override
    public boolean equals(Object toCompare)
    {
        if(toCompare == null) return false;
        if(toCompare == this) return true;
        
        if(this.getClass() == toCompare.getClass())
        {
            LinkedComplex oToCompare = (LinkedComplex) toCompare;
            
            return this.facing == oToCompare.facing
                && this.complexes.equals(oToCompare.complexes);
        }
        
        return false;
    }
}
