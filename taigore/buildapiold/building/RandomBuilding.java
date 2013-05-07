package taigore.buildapi.building;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;

public class RandomBuilding extends AbstractBuilding
{
	private int totalChance = 0;
	private Map<LinkableBuilding, Integer> buildingChances = new LinkedHashMap();
	
	/**
	 * Adds a building for random generation.
	 * If a building is given more than one time, both generationChances
	 * will add up to give its total generation chance.
	 * Generation chance are relative to the total generation, so
	 * with three building with chance 3, 2 and 1, the first building will
	 * be chosen half of the times, the second one third of the time, and the
	 * third one sixth. 
	 */
	public RandomBuilding addBuilding(LinkableBuilding toAdd, int generationChance)
	{
		if(generationChance > 0)
		{
			this.totalChance += generationChance;
			
			if(this.buildingChances.containsKey(toAdd))
				generationChance += this.buildingChances.get(toAdd);
			
			this.buildingChances.put(toAdd, generationChance);
		}
		
		return this;
	}
	
	@Override
	public void drawOnTheWorld(Position rotationPoint, Rotation facing, Random generator, World canvas)
	{
		if(this.totalChance > 0)
		{
			int randomChance = generator.nextInt(this.totalChance);
			
			for(Map.Entry<LinkableBuilding, Integer> entry : this.buildingChances.entrySet())
			{
				randomChance -= entry.getValue();
				
				if(randomChance < 0)
				{
					Rotation drawRotation = this.rotation.add(facing);
					
					entry.getKey().drawOnTheWorld(rotationPoint, drawRotation, generator, canvas);
					break;
				}
			}
		}
	}
}
