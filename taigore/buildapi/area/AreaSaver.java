package taigore.buildapi.area;

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkDataEvent;

public class AreaSaver
{
	final static String savedAreasTag = "AreasList";
	
	/**
	 * Saves the areas that are in the chunk.
	 * If the chunk is unloaded, also removes that chunk
	 * from the AreaManager, along all its managed areas.
	 */
	@ForgeSubscribe
	public void saveAreasInChunk(ChunkDataEvent.Save event)
	{
		synchronized(AreaManager.loadedAreas)
		{
			Chunk toSave = event.getChunk();
			NBTTagCompound toUpdate = event.getData();
			Collection<Area> areasInTheChunk = AreaManager.loadedAreas.get(toSave);
			
			if(areasInTheChunk != null)
			{
				NBTTagList savedAreas = new NBTTagList();
				
				for(Area toConvert : areasInTheChunk)
				{
					if(toConvert != null)
						savedAreas.appendTag(toConvert.writeOnNBT(null));
				}
				
				if(savedAreas.tagCount() > 0) toUpdate.setTag(savedAreasTag, savedAreas);
			}
			
			if(!toSave.isChunkLoaded) AreaManager.loadedAreas.remove(toSave);
		}
	}
	/**
	 * Loads any areas saved into this chunk, and adds them to the
	 * loadedAreas map in the manager.
	 */
	@ForgeSubscribe
	public void loadAreasInChunk(ChunkDataEvent.Load event)
	{
		Chunk toLoad = event.getChunk();
		NBTTagCompound toRead = event.getData();
		
		NBTTagList areasToLoad = toRead.getTagList(savedAreasTag);
		
		if(areasToLoad.tagCount() > 0)
		{
			Collection<Area> areasInTheChunk = new HashSet();
			
			for(int i = 0; i < areasToLoad.tagCount(); ++i)
			{
				Area loaded = Area.makeFromNBT((NBTTagCompound)areasToLoad.tagAt(i));
				
				loaded.world = toLoad.worldObj;
				
				if(loaded != null)
					areasInTheChunk.add(loaded);
			}
			
			AreaManager.addAreasToChunk(areasInTheChunk, toLoad);
		}
	}
}