package taigore.buildapi.area;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class AreaManager implements ITickHandler
{
	static Map<Chunk, HashSet<Area>> loadedAreas = Collections.synchronizedMap(new HashMap());
	
	/////////////////////
	// Area registering
	/////////////////////
	//TODO Comment
	public static void activateArea(Area toActivate, World world)
	{
		if(toActivate != null && world != null)
		{
			toActivate.world = world;
			int chunkX = (int)toActivate.xCenter / 16;
			int chunkZ = (int)toActivate.zCenter / 16;
			Chunk owner = world.getChunkFromChunkCoords(chunkX, chunkZ);
			
			addAreaToChunk(toActivate, owner);
		}
	}
	//TODO Comment
	public static void addAreaToChunk(Area toAdd, Chunk receiver)
	{
		synchronized(loadedAreas)
		{
			if(toAdd != null && receiver != null)
			{
				Set<Area> areasInChunk = getAreasInChunk(receiver); 
				
				areasInChunk.add(toAdd);
			}
		}
	}
	//TODO Comment
	public static void addAreasToChunk(Collection<Area> toAdd, Chunk receiver)
	{
		synchronized(loadedAreas)
		{
			if(toAdd != null && receiver != null)
			{
				if(!toAdd.isEmpty())
				{
					Set<Area> areasInChunk = getAreasInChunk(receiver); 
					
					areasInChunk.addAll(toAdd);
				}
			}
		}
	}

	//TODO Comment
	private static Set<Area> getAreasInChunk(Chunk owner)
	{
		if(owner != null)
		{
			HashSet<Area> chunkAreas = loadedAreas.get(owner);
			
			if(chunkAreas == null)
			{
				chunkAreas = new HashSet();
				loadedAreas.put(owner, chunkAreas);
			}
			
			return chunkAreas;
		}
		else return null;
	}
	
	//TODO Comment
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		synchronized(loadedAreas)
		{
			Iterator<Map.Entry<Chunk, HashSet<Area>>> chunksIterator = loadedAreas.entrySet().iterator();
			
			while(chunksIterator.hasNext())
			{
				Set<Area> areasCollection = chunksIterator.next().getValue();
				Iterator<Area> updatingIterator = areasCollection.iterator();
				
				while(updatingIterator.hasNext())
				{
					Area toUpdate = updatingIterator.next();
					
					if(toUpdate == null || toUpdate.updateArea())
						updatingIterator.remove();
				}
				
				if(areasCollection.isEmpty()) chunksIterator.remove();
			}
		}
	}
	
	//One liners
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}
	@Override
	public EnumSet<TickType> ticks() { return EnumSet.of(TickType.SERVER); }
	@Override
	public String getLabel() { return "AreaTick"; }
}
