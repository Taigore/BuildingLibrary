package taigore.buildapi.entity;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import taigore.buildapi.Vec3Int;
import cpw.mods.fml.common.FMLLog;

public class SimpleSpawn implements IEntitySpawn
{
    private Constructor<? extends Entity> entityMaker;
    public final Map<String, NBTBase> extraData = new HashMap();
    
    public SimpleSpawn(Class<? extends Entity> spawnType)
    {
        try
        {
            this.entityMaker = spawnType.getConstructor(World.class);
        }
        catch(Exception e)
        {
            FMLLog.severe("Taigore BuildingLibrary: failed to get World constructor for entity %s", spawnType == null ? "null" : spawnType.getName());
            e.printStackTrace();
        }
    }
    
    @Override
    public IEntitySpawn setTag(String tagName, NBTBase tagValue)
    {
        if(tagValue != null && tagName != null && !tagName.isEmpty())
            this.extraData.put(tagName, tagValue);
        
        return this;
    }
    
    @Override
    public void placeInPosition(Vec3Int spawnPosition, Random generator, World canvas)
    {
        if(spawnPosition != null && canvas != null)
        {
            try
            {
                Entity created = this.entityMaker.newInstance(canvas);
                
                created.setPosition(spawnPosition.x + 0.5d, spawnPosition.y + 0.5d + created.yOffset, spawnPosition.z + 0.5d);
                
                if(!this.extraData.isEmpty())
                {
                    NBTTagCompound entityData = new NBTTagCompound();
                    created.writeToNBT(entityData);
                    
                    for(Map.Entry<String, NBTBase> toAdd : this.extraData.entrySet())
                    {
                        if(toAdd.getKey() != null && !toAdd.getKey().isEmpty() && toAdd.getValue() != null)
                            entityData.setTag(toAdd.getKey(), toAdd.getValue());
                    }
                    
                    created.readFromNBT(entityData);
                }
            }
            catch (Exception e)
            {
                FMLLog.severe("Taigore BuildingLibrary: could not create entity from constructor %s", this.entityMaker == null ? "null" : this.entityMaker.toString());
                e.printStackTrace();
            }
        }
    }
    
}
