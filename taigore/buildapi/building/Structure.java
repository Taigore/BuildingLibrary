package taigore.buildapi.building;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.utils.ParamSet;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

public class Structure implements IStructureGenerator
{
    private Map<ParamSet, StaticStructure> savedPeeks = new HashMap();
    
    public final Block3DMap buildingMap;
    private Rotation facing = Rotation.NO_ROTATION;
    
    public Structure(Block3DMap map) { this.buildingMap = map.clone(); }
    
    public Structure setRotation(Rotation facing) { this.facing = facing != null ? facing : Rotation.NO_ROTATION; return this; }
    public Structure addRotation(Rotation facing) { this.facing = this.facing.add(facing); return this; }
    
    ///////////////////////
    //IStructureGenerator
    ///////////////////////
    @Override
    public StaticStructure peekNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        StaticStructure returnValue = null; 
        facing = this.facing.add(facing);
        
        ParamSet paramSet = new ParamSet(world, startPosition, facing, generator);
        
        if(this.savedPeeks.containsKey(paramSet))
            returnValue = this.savedPeeks.get(paramSet);
        else
        {
            returnValue = new StaticStructure(this.buildingMap);
            returnValue.setRotation(facing);
            
            this.savedPeeks.put(paramSet, returnValue);
        }
        
        if(returnValue != null)
            returnValue = returnValue.clone();
        
        return returnValue;
    }
    @Override
    public StaticStructure getNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        StaticStructure returnValue = null;
        facing = this.facing.add(facing);
        
        ParamSet paramSet = new ParamSet(world, startPosition, facing, generator);
        
        if(this.savedPeeks.containsKey(paramSet))
            returnValue = this.savedPeeks.remove(paramSet);
        else
        {
            returnValue = new StaticStructure(this.buildingMap);
            returnValue.setRotation(facing);
        }
        
        this.savedPeeks.clear();
        return returnValue;
    }
    @Override
    public void placeNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        IStructureGenerator toPlace = this.getNextStructure(world, startPosition, facing, generator);
        
        if(toPlace != null)
            toPlace.placeNextStructure(world, startPosition, Rotation.NO_ROTATION, generator);
    }
    
    ///////////
    // Object
    ///////////
    @Override
    public Structure clone()
    {
        Structure returnValue = new Structure(this.buildingMap.clone());
        returnValue.facing = this.facing;
        
        returnValue.savedPeeks = new HashMap(this.savedPeeks);
        
        return returnValue;
    }
    @Override
    public boolean equals(Object toCompare)
    {
        if(toCompare == null) return false;
        if(this == toCompare) return true;
        
        if(this.getClass().isInstance(toCompare))
        {
        	Structure oToCompare = (Structure) toCompare;
            
        	return this.facing == oToCompare.facing
        	    && this.savedPeeks.equals(oToCompare.savedPeeks)
        	    && this.buildingMap.equals(oToCompare.buildingMap);
        }
        
        return false;
    }
}
