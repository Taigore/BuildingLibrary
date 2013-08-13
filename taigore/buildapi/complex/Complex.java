package taigore.buildapi.complex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.world.World;
import taigore.buildapi.building.IStructureGenerator;
import taigore.buildapi.building.StaticStructure;
import taigore.buildapi.utils.Pair;
import taigore.buildapi.utils.ParamSet;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

public class Complex implements IComplexGenerator
{
    private Map<ParamSet, StaticComplex> savedPeeks = new HashMap();
    
    private List<Pair<IStructureGenerator, Vec3Int>> structures = new LinkedList();
    private Rotation facing = Rotation.NO_ROTATION;
    
    public void addBuilding(IStructureGenerator toPlace, int offsetX, int offsetY, int offsetZ)
    {
        if(toPlace != null)
        {
            Vec3Int offset = new Vec3Int(offsetX, offsetY, offsetZ);
            
            Pair<IStructureGenerator, Vec3Int> toAdd = new Pair(toPlace, offset);
            
            this.structures.add(toAdd);
            this.savedPeeks.clear();
        }
    }
    public void addRepeatingBuilding(IStructureGenerator toPlace, Vec3Int startPosition, Vec3Int iterationOffset, int iterations)
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
    
    public Complex setRotation(Rotation facing) { this.facing = facing != null ? facing : Rotation.NO_ROTATION; return this; }
    public Complex addRotation(Rotation facing) { this.facing = this.facing.add(facing); return this; }
    
    /////////////////////
    //IComplexGenerator
    /////////////////////
    @Override
    public StaticComplex peekNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        StaticComplex returnValue = null;
        facing = this.facing.add(facing);
        
        ParamSet paramSet = new ParamSet(world, startPosition, facing, generator);
        
        if(this.savedPeeks.containsKey(paramSet))
            returnValue = this.savedPeeks.get(paramSet);
        else
        {
            returnValue = new StaticComplex();
            Vec3Int offset = new Vec3Int();
            Vec3Int position = new Vec3Int();
            
            for(Pair<IStructureGenerator, Vec3Int> toAdd : this.structures)
            {
                if(toAdd != null && toAdd.first != null)
                {
                    offset.reset(0, 0, 0);
                    
                    if(toAdd.second != null)
                        offset.reset(toAdd.second);
                    
                    StaticStructure staticizedStructure = (StaticStructure)toAdd.first.getNextStructure(world, startPosition != null ? position.reset(startPosition).addCoordinates(offset) : null, facing, generator);
                    
                    returnValue.addBuilding(staticizedStructure, offset.x, offset.y, offset.z);
                }
            }
            
            this.savedPeeks.put(paramSet, returnValue);
        }
        
        if(returnValue != null)
            returnValue = returnValue.clone();
            
        return returnValue;
    }
    @Override
    public StaticComplex getNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        StaticComplex returnValue = null;
        facing = this.facing.add(facing);
        
        ParamSet paramSet = new ParamSet(world, startPosition, facing, generator);
        
        if(this.savedPeeks.containsKey(paramSet))
            returnValue = this.savedPeeks.remove(paramSet);
        else
        {
            returnValue = new StaticComplex();
            Vec3Int offset = new Vec3Int();
            Vec3Int position = new Vec3Int();
            
            for(Pair<IStructureGenerator, Vec3Int> toAdd : this.structures)
            {
                if(toAdd != null && toAdd.first != null)
                {
                    offset.reset(0, 0, 0);
                    
                    if(toAdd.second != null)
                        offset.reset(toAdd.second);
                    
                    StaticStructure staticizedStructure = (StaticStructure)toAdd.first.getNextStructure(world, startPosition != null ? position.reset(startPosition).addCoordinates(offset) : null, facing, generator);
                    
                    returnValue.addBuilding(staticizedStructure, offset.x, offset.y, offset.z);
                }
            }
        }
        
        this.savedPeeks.clear();
        return returnValue;
    }
    @Override
    public void placeNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        StaticComplex toPlace = this.getNextComplex(world, startPosition, facing, generator);
        
        if(toPlace != null)
            toPlace.placeNextComplex(world, startPosition, Rotation.NO_ROTATION, generator);
    }
    
    ///////////
    // Object
    ///////////
    @Override
    public Complex clone()
	{
	    try
	    {
    	    Complex returnValue = (Complex)super.clone();
    	    
    	    returnValue.structures.clear();
    	    
    	    for(Pair<IStructureGenerator, Vec3Int> toCopy : this.structures)
    	        returnValue.addBuilding(toCopy.first, toCopy.second.x, toCopy.second.y, toCopy.second.z);
    	    
    	    returnValue.savedPeeks = new HashMap();
    	    returnValue.savedPeeks.putAll(this.savedPeeks);
    	    
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
    		Complex oToCompare = (Complex) toCompare;
    		
    		return this.facing == oToCompare.facing
    		    && this.savedPeeks.equals(oToCompare.savedPeeks)
    		    && this.structures.equals(oToCompare.structures);
    	}
    	
    	return false;
    }
}
