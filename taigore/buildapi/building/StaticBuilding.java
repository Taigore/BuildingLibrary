package taigore.buildapi.building;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;
import taigore.buildapi.block.IAbstractBlock;
import cpw.mods.fml.common.FMLLog;

/**
 * This class represents a building with a geometry that will never change,
 * no matter how many times it gets called, unless the appropriate drawing
 * methods are used.
 */
public class StaticBuilding extends LinkableBuilding
{
	private int[] size = {0, 0, 0};
	
	private List<IAbstractBlock> blockList = new ArrayList();
	private int[] blocksMapping;
	
	private IAbstractBlock lastWritten;
	private int lastWrittenId;
	
	private Rotation rotation = Rotation.NO_ROTATION;
	
	/**
	 * Creates a new StaticBuilding, and fills the whole space defined
	 * by it with the specified filler.
	 */
	public StaticBuilding(int sizeX, int sizeY, int sizeZ, IAbstractBlock filler)
	{
		this.size[0] = sizeX;
		this.size[1] = sizeY;
		this.size[2] = sizeZ;
		
		if(sizeX > 0 && sizeY > 0 && sizeZ > 0)
		{
			this.blocksMapping = new int[sizeX * sizeY * sizeZ];
			this.blockList.add(filler);
			this.lastWritten = filler;
			this.lastWrittenId = 0;
		}
		else
			throw new IllegalArgumentException("Cannot define a less than a block thick structure");
	}
	/**
	 * Simple argumentless constructor for internal copy.
	 */
	private StaticBuilding() {}
	
	/**
	 * Fills the entire area of the building with the specified block.
	 */
	public StaticBuilding fill(IAbstractBlock filler)
	{
		this.blocksMapping = new int[size[0] * size[1] * size[2]];
		this.blockList.clear();
		this.blockList.add(filler);
		this.lastWritten = filler;
		this.lastWrittenId = 0;
		
		return this;
	}
	
	/**
	 * Replaces a 3D rectangle inside the structure with the given block.
	 * Requires two three ints array, otherwise it will throw.
	 * Will also throw if the two point are outside the building area.
	 * @return 
	 */
	public StaticBuilding drawCube(Position startPoint, Position endPoint, IAbstractBlock toDraw)
	{
		//Argument checking
		for(int i = 0; i < 3; ++i)
		{
			if(startPoint == null || startPoint.get(i) < 0 || startPoint.get(i) >= this.size[i])
				throw new IllegalArgumentException(String.format("Invalid cube start point. Size: %s\tPoint: %s", String.valueOf(this.size), String.valueOf(startPoint)));
			if(endPoint == null || endPoint.get(i) < 0 || endPoint.get(i) >= this.size[i])
				throw new IllegalArgumentException(String.format("Invalid cube end point. Size: %s\tPoint: %s", String.valueOf(this.size), String.valueOf(endPoint)));
		}
		
		//Brings the start point to the vertex closest to the origin, and the end point
		//to the vertex farthest from the origin.
		for(int i = 0; i < 3; ++i)
		{
			if(startPoint.get(i) > endPoint.get(i))
			{
				int temp = startPoint.get(i);
				startPoint.set(i, endPoint.get(i));
				endPoint.set(i, temp);
			}
		}
		
		for(int i = startPoint.x; i <= endPoint.x; ++i)
		{
			for(int j = startPoint.y; j <= endPoint.y; ++j)
			{
				for(int k = startPoint.z; k <= endPoint.z; ++k)
				{
					this.setSpot(i, j, k, toDraw);
				}
			}
		}
		
		return this;
	}
	/**
	 * Draws a pixelated line from the startPosition to the endPosition.
	 * Nothing fancy to see, but useful to draw diagonals or slowly rising
	 * ramps.
	 */
	public StaticBuilding drawLine(Position startPosition, Position endPosition, IAbstractBlock material)
	{
		double[] delta = {endPosition.x - startPosition.x, endPosition.y - startPosition.y, endPosition.z - startPosition.z};
		double greatestDimension = delta[0];
		
		for(int i = 1; i < 3; ++i) greatestDimension = Math.max(greatestDimension, delta[i]);
		
		greatestDimension = Math.abs(greatestDimension);
		
		if(greatestDimension > 0)
		{
			for(int i = 0; i < 3; ++i)
				delta[i] /= greatestDimension;
			
			for(int i = 0; i <= greatestDimension; ++i)
			{
				int blockX = (int) Math.floor(delta[0] * i) + startPosition.x;
				int blockY = (int) Math.floor(delta[1] * i) + startPosition.y;
				int blockZ = (int) Math.floor(delta[2] * i) + startPosition.z;
				
				this.setSpot(blockX, blockY, blockZ, material);
			}
		}
		else
			this.setSpot(startPosition, material);
		
		return this;
	}
	
	@Override
	public void drawOnTheWorld(Position drawOrigin, Rotation facing, Random generator, World canvas)
	{
		if(drawOrigin != null)
		{
			Rotation drawRotation = this.rotation.add(facing);
			Position drawPosition = new Position(0, 0, 0);
			
			for(int i = 0; i < this.size[0]; ++i)
			{
				for(int j = 0; j < this.size[1]; ++j)
				{
					for(int k = 0; k < this.size[2]; ++k)
					{
						IAbstractBlock toDraw = this.getSpot(i, j, k);
						
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
	/**
	 * Sets the given spot in the structure to the given block.
	 */
	public StaticBuilding setSpot(Position toSet, IAbstractBlock material)
	{
		return this.setSpot(toSet.x, toSet.y, toSet.z, material);
	}
	/**
	 * Sets the given spot in the structure to the given block.
	 */
	public StaticBuilding setSpot(int posX, int posY, int posZ, IAbstractBlock material)
	{
		if(posX >= 0 && posX < this.size[0] && posY >= 0 && posY < this.size[1] && posZ >= 0 && posZ < this.size[2])
		{
			int realIndex = (posX * size[1] + posY) * size[2] + posZ;
			
			int idToWrite;
			
			if(this.lastWritten == material)
				idToWrite = this.lastWrittenId;
			else
			{
				idToWrite = this.blockList.indexOf(material);
				
				if(idToWrite < 0)
				{
					idToWrite = this.blockList.size();
					this.blockList.add(material);
				}
			}
			
			this.blocksMapping[realIndex] = idToWrite;
		}
		else
			FMLLog.warning("Out of bounds request for setSpot");
		
		return this;
	}
	/**
	 * Returns whatever block is in the given spot.
	 */
	public IAbstractBlock getSpot(int posX, int posY, int posZ)
	{
		if(posX >= 0 && posX < this.size[0] && posY >= 0 && posY < this.size[1] && posZ >= 0 && posZ < this.size[2])
		{
			int realIndex = (posX * size[1] + posY) * size[2] + posZ;
			
			return this.blockList.get(this.blocksMapping[realIndex]);
		}
		else
		{
			FMLLog.warning("Out of bounds request for getSpot");
			return null;
		}
			
	}
	@Override
	public StaticBuilding copy()
	{
		StaticBuilding copy = new StaticBuilding();
		copy.size = this.size.clone();
		copy.blockList.addAll(this.blockList);
		copy.blocksMapping = this.blocksMapping.clone();
		
		return (StaticBuilding)this.copyLinks(copy);
	}
	@Override
	public StaticBuilding rotate(Rotation rotation)
	{
		this.rotation = this.rotation.add(rotation);
		return this;
	}
}
