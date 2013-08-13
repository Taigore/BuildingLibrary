package taigore.buildapi.building;

import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.block.IBlock;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

/**
 * This class represents a building with a geometry that will never change,
 * no matter how many times it gets called, unless the appropriate drawing
 * methods are used.
 */
public class StaticStructure implements IStructureGenerator
{
    public final StaticBlock3DMap buildingMap;
    private Rotation facing = Rotation.NO_ROTATION;
	
    public StaticStructure(Block3DMap map)
    {
        if(StaticBlock3DMap.class.isInstance(map))
            this.buildingMap = (StaticBlock3DMap)map.clone();
        else
            this.buildingMap = new StaticBlock3DMap(map);
            
    }
	
	public StaticStructure setRotation(Rotation facing) { this.facing = facing != null ? facing : Rotation.NO_ROTATION; return this; }
	public StaticStructure addRotation(Rotation facing) { this.facing = this.facing.add(facing); return this; }
	
	///////////////////////
	//IStructureGenerator
	///////////////////////
    @Override
    public StaticStructure peekNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator) { return this.clone().addRotation(facing); }
    @Override
    public StaticStructure getNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator) { return this.peekNextStructure(world, startPosition, facing, generator); }
    @Override
    public void placeNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator)
    {
        if(world != null && startPosition != null)
        {
            Vec3Int pointer = new Vec3Int();
            facing = this.facing.add(facing);
            
            for(int i = 0; i < this.buildingMap.sizeX; ++i)
                for(int j = 0; j < this.buildingMap.sizeY; ++j)
                    for(int k = 0; k < this.buildingMap.sizeZ; ++k)
                    {
                        IBlock toPlace = this.buildingMap.getSpot(i, j, k);
                        pointer.reset(i, j, k).rotate(facing).addCoordinates(startPosition);
                        
                        if(toPlace != null)
                            toPlace.placeBlock(world, pointer, facing, generator);
                    }
        }
    }
    
    ///////////
    // Object
    ///////////
    @Override
    public StaticStructure clone()
	{
	    StaticStructure returnValue = new StaticStructure(this.buildingMap.clone());
	    returnValue.facing = this.facing;
	    
	    return returnValue;
	}
    
    @Override
    public boolean equals(Object toCompare)
    {
        if(toCompare == null) return false;
        if(this == toCompare) return true;
        
        if(this.getClass().isInstance(toCompare))
        {
            StaticStructure structureToCompare = (StaticStructure)toCompare;
            
            return this.facing == structureToCompare.facing
                && this.buildingMap.equals(structureToCompare.buildingMap);
        }
        
        return false;
    }
}
