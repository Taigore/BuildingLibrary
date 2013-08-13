package taigore.buildapi.complex;

import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.utils.Rotation;
import taigore.buildapi.utils.Vec3Int;

public interface IComplexGenerator extends Cloneable
{
    /**
     * Generates a static complex, that can be used to place several buildings in the world.
     * Several calls of peekNextComplex will return structures equals to each other, until either
     * place or getNextComplex are called with the same parameters.
     * Even though the return type is IComplexGenerator, to allow complexes to contain each other,
     * at the end of any functioning chain of IComplexGenerator it's impossible to find anything other
     * than a null value or a StaticComplex. That's because it's the only class that can really be
     * placed in the world.
     * @param world - A valid world
     * @param startPosition - The start position of the complex. Its position relative to the complex depends on how the individual instance is set.
     * @param facing - The rotation of the complex
     * @param generator - A valid random generator
     * @return A StaticComplex ready for placement, null if the implementation returns null for the provided parameters
     */
    IComplexGenerator peekNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator);
    /**
     * Generates a static complex, that can be used to place several buildings in the world.
     * It may not return the same complex each time its called with the same parameters, depending on the implementation.
     * Even though the return type is IComplexGenerator, to allow complexes to contain each other,
     * at the end of any functioning chain of IComplexGenerator it's impossible to find anything other
     * than a null value or a StaticComplex. That's because it's the only class that can really be
     * placed in the world.
     * @param world - A valid world
     * @param startPosition - The start position of the complex. Its position relative to the complex depends on how the individual instance is set.
     * @param facing - The rotation of the complex
     * @param generator - A valid random generator
     * @return A StaticComplex ready for placement, null if the implementation returns null for the provided parameters
     */
    IComplexGenerator getNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator);
    /**
     * Places directly the complex returned by getNextComplex
     * Throws IllegalArgumentException if either world or startPosition are null
     * @param world - A valid world
     * @param startPosition - The start position of the complex. The lowest X, lowest Y, lowest Z point of the structure
     * @param facing - The rotation of the structure
     * @param generator - A valid random generator
     */
    void placeNextComplex(World world, Vec3Int startPosition, Rotation facing, Random generator);
    
    IComplexGenerator clone();
}
