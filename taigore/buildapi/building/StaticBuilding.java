package taigore.buildapi.building;

import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Vec3Int;
import taigore.buildapi.Rotation;
import taigore.buildapi.block.IBlock;

/**
 * This class represents a building with a geometry that will never change,
 * no matter how many times it gets called, unless the appropriate drawing
 * methods are used.
 */
public class StaticBuilding extends LinkableBuilding
{
    public final Block3DMap buildingMap;
	
	/**
	 * Creates a new StaticBuilding, and fills the whole space defined
	 * by it with the specified filler.
	 */
	public StaticBuilding(int sizeX, int sizeY, int sizeZ, IBlock filler) { this.buildingMap = new Block3DMap(sizeX, sizeY, sizeZ, filler); }
	public StaticBuilding(StaticBuilding toCopy) { this(toCopy.buildingMap); }
	public StaticBuilding(Block3DMap toCopy) { this.buildingMap = new Block3DMap(toCopy); }
	
	@Override
	public void drawOnTheWorld(Vec3Int drawOrigin, Rotation facing, Random generator, World canvas)
	{
		if(canvas != null && drawOrigin != null)
		{
		    if(generator == null) generator = new Random();
		    
			Rotation drawRotation = this.rotation.add(facing);
			Vec3Int drawPosition = new Vec3Int(0, 0, 0);
			
			IBlock[] blocks = this.buildingMap.getAsBlockArray();
			int[] size = this.buildingMap.getDimensions();
			
			for(int i = 0, blockIndex = 0; i < size[0]; ++i)
			{
				for(int j = 0; j < size[1]; ++j)
				{
					for(int k = 0; k < size[2]; ++k, ++blockIndex)
					{
						IBlock toDraw = blocks[blockIndex];
						
						if(toDraw != null)
						{
							drawPosition.reset(i, j, k).rotate(drawRotation).addCoordinates(drawOrigin);
							
							//This leaves to the block if it needs to draw or not to draw
							toDraw.drawAt(drawPosition, drawRotation, generator, canvas);
						}
					}
				}
			}
			
			this.drawAttachedBuildings(drawOrigin, drawRotation, generator, canvas);
		}
	}
}
