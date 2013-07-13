package taigore.buildapi.building;

import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Vec3Int;
import taigore.buildapi.Rotation;
import taigore.buildapi.block.IBlock;

/**
 * This class represents a building that is made with a single unit,
 * handled as a static building, repeated n times with the specified repeating pass
 */
public class RepeatingBuilding extends StaticBuilding
{
    public final Vec3Int repeatOffset = new Vec3Int(0, 0, 0);
	private int repetitions = 1;
	
	/**
	 * Constructor that defaults to a single repetition.
	 * Similar to creating a StaticBuilding.
	 * The repeatOffset will be applied increasingly each building cycle to the
	 * building coordinates. Default is 0, 0, 0
	 * The number of repetitions is the amount of times the unit will be repeated.
	 */
	public RepeatingBuilding(Block3DMap unit) { super(unit); }
	public RepeatingBuilding(int sizeX, int sizeY, int sizeZ, IBlock filler) { super(sizeX, sizeY, sizeZ, filler); }
	/**
	 * Sets number of times the base structure should be repeated.
	 */
	public RepeatingBuilding setRepeats(int repetitions)
	{
		if(repetitions < 1) repetitions = 1;
		this.repetitions = repetitions;
		return this;
	}
	
	@Override
	public void drawOnTheWorld(Vec3Int rotationPoint, Rotation facing, Random generator, World canvas)
	{
		if(rotationPoint != null && canvas != null)
		{
		    if(generator == null) generator = new Random();
		    
			Rotation drawRotation = this.rotation.add(facing);
			Vec3Int drawPosition = new Vec3Int(rotationPoint);
			Vec3Int drawPointer = new Vec3Int(0, 0, 0);
			Vec3Int rotatedOffset = new Vec3Int(this.repeatOffset).rotate(drawRotation);
			IBlock[] serializedBuilding = this.buildingMap.getAsBlockArray();
			int[] size = this.buildingMap.getDimensions();
			
			for(int n = 0; n < this.repetitions; ++n)
			{
			    if(n != 0) drawPosition.addCoordinates(rotatedOffset);
			    
			    for(int i = 0, blockIndex = 0; i < size[0]; ++i)
			        for(int j = 0; j < size[1]; ++j)
			            for(int k = 0; k < size[2]; ++k, ++blockIndex)
			            {
			                drawPointer.reset(i, j, k).addCoordinates(drawPosition);
			                IBlock toDraw = serializedBuilding[blockIndex];
			                
			                if(toDraw != null)
			                    toDraw.drawAt(drawPointer, drawRotation, generator, canvas);
			            }
			}
			
			this.drawAttachedBuildings(drawPosition, drawRotation, generator, canvas);
		}
	}
}
