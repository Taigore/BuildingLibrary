package taigore.buildapi.complex;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.world.World;
import taigore.buildapi.building.StaticStructure;
import taigore.buildapi.utils.Pair;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

public class StaticComplex implements IComplexGenerator
{
    private List<Pair<StaticStructure, Vec3Int>> structures = new LinkedList();
    private Rotation facing = Rotation.NO_ROTATION;
    
    public void addBuilding(StaticStructure toPlace, int offsetX, int offsetY, int offsetZ)
    {
        if(toPlace != null)
        {
            Vec3Int offset = new Vec3Int(offsetX, offsetY, offsetZ);
            
            Pair<StaticStructure, Vec3Int> toAdd = new Pair(toPlace.clone(), offset);
            
            this.structures.add(toAdd);
        }
    }
    public void addRepeatingBuilding(StaticStructure toPlace, Vec3Int startPosition, Vec3Int iterationOffset, int iterations)
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
                
                this.addBuilding(toPlace, offset.x, offset.y, offset.z);
            }
        }
    }
    
    public StaticComplex setRotation(Rotation facing) { this.facing = facing != null ? facing : Rotation.NO_ROTATION; return this; }
    public StaticComplex addRotation(Rotation facing) { this.facing = this.facing.add(facing); return this; }
    
    /////////////////////
    //IComplexGenerator
    /////////////////////
    @Override
    public StaticComplex peekNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator) { return this.clone().addRotation(facing); }
    @Override
    public StaticComplex getNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator) { return this.clone().addRotation(facing); }
    @Override
    public void placeNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        if(world != null && startPosition != null)
        {
            facing = this.facing.add(facing);
            Vec3Int offset = new Vec3Int();
            
            for(Pair<StaticStructure, Vec3Int> toDraw : this.structures)
            {
                if(toDraw != null && toDraw.first != null)
                {
                    if(toDraw.second != null)
                        offset.reset(toDraw.second);
                    else
                        offset.reset(0, 0, 0);
                    
                    offset.rotate(facing);
                    offset.addCoordinates(startPosition);
                    
                    toDraw.first.placeNextStructure(world, offset, facing, generator);
                }
            }
        }
    }
    
    ///////////
    // Object
    ///////////
    @Override
    public StaticComplex clone()
	{
	    try
	    {
    	    StaticComplex returnValue = (StaticComplex)super.clone();
    	    
    	    returnValue.structures.clear();
    	    
    	    for(Pair<StaticStructure, Vec3Int> toCopy : this.structures)
    	        returnValue.addBuilding(toCopy.first, toCopy.second.x, toCopy.second.y, toCopy.second.z);
    	    
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
    		StaticComplex oToCompare = (StaticComplex) toCompare;
    		
    		return this.facing == oToCompare.facing
    		    && this.structures.equals(oToCompare.structures);
    	}
    	
    	return false;
    }
}
