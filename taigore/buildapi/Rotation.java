package taigore.buildapi;

import java.util.Arrays;
import java.util.List;

//DO NOT TOUCH!
//Everything may break otherwise.
public enum Rotation
{
	NO_ROTATION,
	CLOCKWISE,
	REVERSE,
	COUNTERCLOCKWISE;
	
	private static List<Rotation> allFacings = Arrays.asList(Rotation.values());
	
	/**
	 * Returns a unique numeric id for the facing.
	 * IDs are ordered in increasing number from NO_ROTATION,
	 * with each clockwise rotation incrementing by one.
	 * @return
	 */
	public int getID() { return allFacings.indexOf(this); }
	/**
	 * Returns the rotation associated with the given ID.
	 */
	public static Rotation getFromID(int id)
	{
		if(id >= 0 && id < allFacings.size())
			return allFacings.get(id);
		else
			return null;
	}
	/**
	 * Subtracts the given rotation from this rotation.
	 * Rotations work much like multiples of 90 degrees,
	 * wrapping around 360, and increasing clockwise.
	 * NO_ROTATION = 0
	 * CLOCKWISE = 90
	 * REVERSE = 180
	 * COUNTERCLOCKWISE = 270
	 */
	public Rotation subtract(Rotation rotation)
	{
		if(rotation != null && rotation != NO_ROTATION)
		{
			int thisIndex = allFacings.indexOf(this);
			int rotationIndex = allFacings.indexOf(rotation);
			int finalIndex = thisIndex - rotationIndex;
			
			while(finalIndex < 0) finalIndex += allFacings.size();
			
			return allFacings.get(finalIndex);
		}
		else return this;
	}
	/**
	 * Adds the given rotation to this rotation.
	 * Rotations work much like multiples of 90 degrees,
	 * wrapping around 360, and increasing clockwise.
	 * NO_ROTATION = 0
	 * CLOCKWISE = 90
	 * REVERSE = 180
	 * COUNTERCLOCKWISE = 270
	 */
	public Rotation add(Rotation rotation)
	{
		if(rotation != null && rotation != NO_ROTATION)
		{
			int thisIndex = allFacings.indexOf(this);
			int rotationIndex = allFacings.indexOf(rotation);
			
			return allFacings.get((thisIndex + rotationIndex) % allFacings.size());
		}
		else return this;
	}
}