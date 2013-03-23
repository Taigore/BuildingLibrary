package taigore.buildapi.building;

import java.util.Random;

import taigore.buildapi.Position;
import taigore.buildapi.Rotation;

import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;

/**
 * This class represents a building that is made with a single unit,
 * handled as a static building, repeated n times with the specified repeating pass
 */
public class RepeatingBuilding extends LinkableBuilding
{
	private int repetitions = 1;
	private Position repeatOffset = new Position(0, 0, 0);
	public IAbstractBuilding unit;
	private Rotation rotation = Rotation.NO_ROTATION;
	
	/**
	 * Constructor that defaults to a single repetition.
	 * Similar to creating a StaticBuilding.
	 * The repeatOffset will be applied increasingly each building cycle to the
	 * building coordinates. Default is 0, 0, 0
	 */
	public RepeatingBuilding(IAbstractBuilding unit) { this.unit = unit; }
	public RepeatingBuilding(IAbstractBuilding unit,
							 int repeatOffsetX,	int repeatOffsetY, int repeatOffsetZ)
	{
		this(unit);
		this.setRepeatOffset(repeatOffsetX, repeatOffsetY, repeatOffsetZ);
	}
	/**
	 * Constructor that allows to also set the number of repetitions.
	 */
	public RepeatingBuilding(IAbstractBuilding unit,
							 int repeatOffsetX, int repeatOffsetY, int repeatOffsetZ,
							 int repetitions)
	{
		this(unit, repeatOffsetX, repeatOffsetY, repeatOffsetZ);
		this.setRepeats(repetitions);
	}
	/**
	 * Sets the offset for the each next building cycle.
	 * These offsets will be multiplied by the build cycle number and added
	 * to the starting coordinates, to get the new building cycle coordinates.
	 */
	public RepeatingBuilding setRepeatOffset(int repeatOffsetX, int repeatOffsetY, int repeatOffsetZ)
	{
		this.repeatOffset.reset(repeatOffsetX, repeatOffsetY, repeatOffsetZ);
		
		return this;
	}
	public RepeatingBuilding setRepeatOffset(Position newOffset)
	{
		this.repeatOffset.reset(newOffset);
		
		return this;
	}
	/**
	 * Sets number of times the base structure should be repeated.
	 */
	public RepeatingBuilding setRepeats(int repetitions)
	{
		if(repetitions <= 0)
		{
			FMLLog.info("Can't setup a number of repeats lower than one. Defaulting to one");
			repetitions = 1;
		}
		
		this.repetitions = repetitions;
		
		return this;
	}
	/**
	 * Sets the building that has to be repeated when this object is drawn.
	 */
	public RepeatingBuilding setRepeatedUnit(IAbstractBuilding toRepeat)
	{
		this.unit = toRepeat;
		return this;
	}
	
	@Override
	public void drawOnTheWorld(Position rotationPoint, Rotation facing, Random generator, World canvas)
	{
		if(rotationPoint != null)
		{
			Rotation drawRotation = this.rotation.add(facing);
			Position drawPosition = new Position(rotationPoint);
			Position rotatedOffset = new Position(this.repeatOffset).rotate(drawRotation);
			
			if(this.unit != null)
			{
				for(int i = 0; i < this.repetitions; ++i)
				{
					this.unit.drawOnTheWorld(drawPosition, drawRotation, generator, canvas);
					drawPosition.addCoordinates(rotatedOffset);
				}
			
				drawPosition.x -= rotatedOffset.x;
				drawPosition.y -= rotatedOffset.y;
				drawPosition.z -= rotatedOffset.z;
			}
			
			this.drawAttachedBuildings(drawPosition, drawRotation, generator, canvas);
		}
	}
	@Override
	public RepeatingBuilding copy()
	{
		RepeatingBuilding copy = new RepeatingBuilding(this.unit, this.repeatOffset.x, this.repeatOffset.y, this.repeatOffset.z, this.repetitions);
		
		return (RepeatingBuilding)this.copyLinks(copy);
	}
	
	public RepeatingBuilding rotate(Rotation rotation)
	{
		this.rotation = this.rotation.add(rotation);
		return this;
	}
}
