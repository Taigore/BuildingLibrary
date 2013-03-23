package taigore.buildapi.building;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;

public abstract class LinkableBuilding implements IAbstractBuilding
{
	private List<BuildingPositionPair> attachedBuildings = new LinkedList();
	
	/**
	 * Links the specified building to this building.
	 * When this building gets drawn, all attached buildings will be
	 * drawn at their own offset from the draw point of this building.
	 */
	public LinkableBuilding attachBuilding(IAbstractBuilding toAttach, int offsetX, int offsetY, int offsetZ)
	{
		if(toAttach != null)
			this.attachedBuildings.add(new BuildingPositionPair(toAttach, new Position(offsetX, offsetY, offsetZ)));
		
		return this;
	}
	/**
	 * Called by subtypes to draw any attached buildings.
	 * Allows the subtypes draw method to specify a custom
	 * draw origin (RepeatingBuildings gets its drawOrigin from
	 * the number of repeats)
	 */
	protected void drawAttachedBuildings(Position drawOrigin, Rotation facing, Random generator, World canvas)
	{
		if(drawOrigin != null)
		{
			Position shiftedDrawOrigin = new Position();
			
			for(BuildingPositionPair toDraw : this.attachedBuildings)
			{
				shiftedDrawOrigin.reset(toDraw.position).rotate(facing).addCoordinates(drawOrigin);
				
				toDraw.building.drawOnTheWorld(shiftedDrawOrigin, facing, generator, canvas);
			}
		}
	}
	/**
	 * Allows subtypes copy methods to copy linked buildings,
	 * along with any other data they may need.
	 */
	protected LinkableBuilding copyLinks(LinkableBuilding toCopyOn)
	{
		toCopyOn.attachedBuildings.clear();
		
		for(BuildingPositionPair toCopy : this.attachedBuildings)
			toCopyOn.attachedBuildings.add(toCopy.copy());
		
		return toCopyOn;
	}

	/**
	 * Simple pair class for attached buildings storage.
	 */
	protected class BuildingPositionPair
	{
		protected IAbstractBuilding building;
		protected Position position;
		
		private BuildingPositionPair(IAbstractBuilding building, Position position)
		{
			this.building = building;
			this.position = position;
		}
		
		public BuildingPositionPair copy()
		{
			return new BuildingPositionPair(this.building, new Position(this.position));
		}
	}
}
