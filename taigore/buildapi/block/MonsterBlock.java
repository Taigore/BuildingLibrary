package taigore.buildapi.block;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import taigore.buildapi.MagicalEffect;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;
import cpw.mods.fml.common.FMLLog;

public class MonsterBlock implements IAbstractBlock
{
	private Constructor<? extends Entity> entityMaker;
	private final Map<String, NBTBase> extraData = new HashMap();
	
	/**
	 * Creates an abstract block that spawns an entity of the specified class on drawing.
	 */
	public static MonsterBlock make(Class<? extends Entity> entityClass) { return make(entityClass, null); }
	/**
	 * Creates an abstract block, that spawns an entity of the specified class and then edits its NBT
	 * data to alter its equipment, health and other traits.
	 */
	public static MonsterBlock make(Class<? extends Entity> entityClass, Collection<NBTBase> extraData)
	{
		if(entityClass != null)
		{
			try
			{
				Constructor<? extends Entity> entityMaker = entityClass.getConstructor(World.class);
				
				return new MonsterBlock(entityMaker, extraData);
			}
			catch(Exception e)
			{
				FMLLog.severe("No world constructor for entity class %s. Skipping MonsterBlock creation", entityClass.getName());
				return null;
			}
		}
		else
			return null;
	}
	private MonsterBlock(Constructor<? extends Entity> entityMaker, Collection<NBTBase> extraData)
	{
		this.entityMaker = entityMaker;
		
		if(extraData != null)
		{
			for(NBTBase toSave : extraData)
			{
				if(toSave != null && !toSave.getName().isEmpty())
					this.extraData.put(toSave.getName(), toSave.copy());
			}
		}
	}
	
	@Override
	public void drawAt(Position spawnPosition, Rotation facing, Random generator, World canvas)
	{
		if(spawnPosition != null && canvas != null)
		{
			StaticBlock.air.drawAt(spawnPosition, facing, generator, canvas);
			
			try
			{
				Entity spawned = this.entityMaker.newInstance(canvas);
				
				if(spawned != null)
				{
					spawned.setPosition(spawnPosition.x + 0.5f,
										spawnPosition.y + 0.5f + spawned.yOffset,
										spawnPosition.z + 0.5f);
					
					//Adds the extra data provided
					NBTTagCompound entityData = new NBTTagCompound();
					spawned.writeToNBT(entityData);
					
					for(Map.Entry<String, NBTBase> toApply : this.extraData.entrySet())
					{
						if(!toApply.getKey().isEmpty() && toApply.getValue() != null)
							entityData.setTag(toApply.getKey(), toApply.getValue().copy());
					}
					
					spawned.readFromNBT(entityData);
					
					if(EntityLiving.class.isInstance(spawned))
						((EntityLiving)spawned).setHomeArea(spawnPosition.x, spawnPosition.y, spawnPosition.z, 20);
					
					if(!canvas.isRemote)
						canvas.spawnEntityInWorld(spawned);
				}
			}
			catch(Exception e)
			{
				FMLLog.severe("Failed to create entity at %s. Exception: %s", spawnPosition.toString(), e.toString());
			}
		}
	}
	
	@Override
	public MonsterBlock copy()
	{
		MonsterBlock copy = new MonsterBlock(this.entityMaker, null);
		
		for(Map.Entry<String, NBTBase> toCopy : this.extraData.entrySet())
		{
			if(!toCopy.getKey().isEmpty() && toCopy.getValue() != null)
				copy.extraData.put(toCopy.getKey(), toCopy.getValue().copy());
		}
		
		return copy;
	}
	/**
	 * Adds a copy of the provided tag for writing on any spawned creature from this block.
	 * It's up to the entity's readFromNBT method to read that tag.
	 * Tags lacking a name will be ignored.
	 */
	public MonsterBlock setDataTag(NBTBase toAdd)
	{
		if(toAdd != null && !toAdd.getName().isEmpty())
			this.extraData.put(toAdd.getName(), toAdd.copy());
		
		return this;
	}
	
	/**
	 * Adds the specified effect on any spawned entity.
	 * Level is defaulted to 0, and duration is defaulted to
	 * Integer.MAX_VALUE ticks (years of time).
	 */
	public MonsterBlock addPotionEffect(MagicalEffect toAdd) { return this.addPotionEffect(toAdd, 0); }
	/**
	 * Adds the specified effect, with a custom level.
	 * The duration is Integer.MAX_VALUE tick (years of time) by default.
	 */
	public MonsterBlock addPotionEffect(MagicalEffect toAdd, int level) { return this.addPotionEffect(toAdd, level, Integer.MAX_VALUE); }
	/**
	 * Adds the spcified effect, with a custom level and a custom duration in ticks.
	 */
	public MonsterBlock addPotionEffect(MagicalEffect toAdd, int level, int ticks)
	{
		NBTBase effects = this.extraData.get("ActiveEffects");
		
		if(!NBTTagList.class.isInstance(effects))
		{
			effects = new NBTTagList("ActiveEffects");
			this.extraData.put(effects.getName(), effects);
		}
		
		PotionEffect newEffect = new PotionEffect(toAdd.id, ticks, level);
		((NBTTagList)effects).appendTag(newEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
		
		return this;
	}
	
	@Override
	public MonsterBlock rotate(Rotation rotation) { return this; }
}
