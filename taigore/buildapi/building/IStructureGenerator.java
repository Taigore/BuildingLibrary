package taigore.buildapi.building;

import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

public interface IStructureGenerator extends Cloneable
{
    /**
     * Generates a static structure, that can be used to place a building in the world.
     * Several calls of peekStructure will return structures equals to each other, until either
     * place or getStructure are called with the same parameters.
     * Even though the return type is IStructureGenerator, to allow structures to contain each other,
     * at the end of any functioning chain of IStructureGenerator it's impossible to find anything other
     * than a null value or a StaticStructure. That's because it's the only class that can really be
     * placed in the world.
     * @param world - A world
     * @param startPosition - The start position of the structure. The lowest X, lowest Y, lowest Z point of the structure
     * @param facing - The rotation of the structure
     * @param generator - A random generator
     * @return A StaticStructure ready for placement, null if the implementation returns null for the provided parameters
     */
    IStructureGenerator peekNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator);
    /**
     * Generates a static structure, that can be used to place a building in the world.
     * It may not return equal structures each time its called.
     * Even though the return type is IStructureGenerator, to allow structures to contain each other,
     * at the end of any functioning chain of IStructureGenerator it's impossible to find anything other
     * than a null value or a StaticStructure. That's because it's the only class that can really be
     * placed in the world.
     * @param world - A world
     * @param startPosition - The start position of the structure. The lowest X, lowest Y, lowest Z point of the structure
     * @param facing - The rotation of the structure
     * @param generator - A random generator
     * @return A StaticStructure ready for placement, null if the implementation returns null for the provided parameters
     */
    IStructureGenerator getNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator);
    /**
     * Places directly the structure returned by getStructure.
     * Throws IllegalArgumentException if either startPosition or world are null
     * @param world - A valid world
     * @param startPosition - The start position of the structure. The lowest X, lowest Y, lowest Z point of the structure
     * @param facing - The rotation of the structure
     * @param generator - A random generator
     */
    void placeNextStructure(World world, Vec3Int startPosition, Rotation facing, Random generator);
    
    IStructureGenerator clone();
}
