package taigore.buildapi.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;

public class RandomBlock implements IAbstractBlock
{
	private int totalChance = 0;
	private Map<IAbstractBlock, Integer> blocksAndChances = new HashMap();
	private Rotation rotation = Rotation.NO_ROTATION;
	
	/**
	 * Adds a series of blocks to the generation list, with the relevant
	 * spawn chance. A meta not paired with a spawn chance will be ignored
	 * (ie: the metadata array is longer than spawn chances array.)
	 * A spawn chance of 0 or less will not add the corresponding metadata.
	 */
	public RandomBlock addBlocks(int id, int[] metadata, int[] spawnChances)
	{
		if(metadata != null && spawnChances != null)
		{
			for(int i = 0; i < Math.min(metadata.length, spawnChances.length); ++i)
				this.addBlock(id, metadata[i], spawnChances[i]);
		}
		
		return this;
	}
	/**
	 * Adds the spawn chance for a block, defined by id and meta.
	 * Minimum chance is one, otherwise it won't do anything.
	 */
	public RandomBlock addBlock(int id, int metadata, int spawnChance)
	{
		return this.addBlock(new StaticBlock(id, metadata), spawnChance);
	}
	/**
	 * Adds the spawn chance for a block, defined as an AbstractBlock.
	 * Minimum chance is one, otherwise it won't do anything.
	 */
	public RandomBlock addBlock(IAbstractBlock toAdd, int spawnChance)
	{
		if(spawnChance > 0)
		{
			this.totalChance += spawnChance;
			
			if(this.blocksAndChances.containsKey(toAdd))
				spawnChance += this.blocksAndChances.get(toAdd);
			
			this.blocksAndChances.put(toAdd, spawnChance);
		}
		
		return this;
	}

	@Override
	public void drawAt(Position startPosition, Rotation facing, Random generator, World canvas)
	{
		if(startPosition != null)
		{
			int randomInt = generator.nextInt(this.totalChance);
			
			for(Map.Entry<IAbstractBlock, Integer> toCheck : this.blocksAndChances.entrySet())
			{
				randomInt -= toCheck.getValue();
				
				if(randomInt < 0)
				{
					Rotation drawRotation = this.rotation.add(facing);
					IAbstractBlock drawing = toCheck.getKey();
					
					if(drawing != StaticBlock.noEdit)
						drawing.drawAt(startPosition, drawRotation, generator, canvas);
					
					break;
				}
			}
		}
	}
	@Override
	public RandomBlock copy()
	{
		RandomBlock copy = new RandomBlock();
		copy.totalChance = this.totalChance;
		copy.blocksAndChances.putAll(this.blocksAndChances);
		
		return copy;
	}
	@Override
	public RandomBlock rotate(Rotation rotation)
	{
		this.rotation = this.rotation.add(rotation);
		return this;
	}
}
