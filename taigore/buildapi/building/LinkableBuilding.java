package taigore.buildapi.building;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Vec3Int;
import taigore.buildapi.Rotation;

public abstract class LinkableBuilding extends AbstractBuilding
{
	private List<BuildingPositionPair> attachedBuildings = new LinkedList();
	
	/**
	 * Links the specified building to this building.
	 * When this building gets drawn, all attached buildings will be
	 * drawn at their own offset from the draw point of this building.
	 */
	public LinkableBuilding attachBuilding(AbstractBuilding toAttach, int offsetX, int offsetY, int offsetZ)
	{
		if(toAttach != null)
			this.attachedBuildings.add(new BuildingPositionPair(toAttach, new Vec3Int(offsetX, offsetY, offsetZ)));
		
		return this;
	}
	/**
	 * Called by subtypes to draw any attached buildings.
	 * Allows the subtypes draw method to specify a custom
	 * draw origin (RepeatingBuildings gets its drawOrigin from
	 * the number of repeats)
	 */
	protected void drawAttachedBuildings(Vec3Int drawOrigin, Rotation facing, Random generator, World canvas)
	{
		if(drawOrigin != null)
		{
			Vec3Int shiftedDrawOrigin = new Vec3Int();
			
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
		protected AbstractBuilding building;
		protected Vec3Int position;
		
		private BuildingPositionPair(AbstractBuilding building, Vec3Int position)
		{
			this.building = building;
			this.position = position;
		}
		
		public BuildingPositionPair copy()
		{
			return new BuildingPositionPair(this.building, new Vec3Int(this.position));
		}
	}
}
