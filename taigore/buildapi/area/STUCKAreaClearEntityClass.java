package taigore.buildapi.area;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

public class STUCKAreaClearEntityClass extends Area
{
	//Never forget to map!
	static { Area.addMapping((byte)1, STUCKAreaClearEntityClass.class); }
	
	private final AxisAlignedBB detectionArea = AxisAlignedBB.getBoundingBox(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d);
	private final Set<Class<? extends Entity>> blacklist = new HashSet();
	private final Set<Class<? extends Entity>> whitelist = new HashSet();
	
	/**
	 * Needed for loading. Will create a useless area, that will not
	 * erase any entity. However, it's called to instantiate an object
	 * when the Areas are loaded.
	 */
	public STUCKAreaClearEntityClass() {};
	/**
	 * Sets the area bounds as specified. Also sets the black and whitelisted
	 * classes, cleaning both lists to slightly speed up checks.
	 */
	public STUCKAreaClearEntityClass(double xStart, double xEnd, double yStart, double yEnd, double zStart, double zEnd, Set<Class<? extends Entity>> blacklist, Set<Class<? extends Entity>> whitelist)
	{
		super(xStart, xEnd, yStart, yEnd, zStart, zEnd);
		
		if(whitelist != null)
			this.whitelist.addAll(whitelist);
		
		if(blacklist != null)
			this.blacklist.addAll(blacklist);
		
		//If a class in the blacklist is a subclass of another class in the whitelist
		//it's better to remove it, it won't make any difference when the logical check
		//is made. Also, EntityPlayer and subclasses is always automatically white listed,
		//to avoid funky stuff and accidental insta-gib of players, while allowing to blacklist
		//"EntityLiving" (that includes EntityPlayer)
		if(!this.whitelist.isEmpty() && !this.blacklist.isEmpty())
		{
			Iterator<Class<? extends Entity>> blacklistIterator = this.blacklist.iterator();
			
			while(blacklistIterator.hasNext())
			{
				Class blacklisted = blacklistIterator.next();
				
				if(EntityPlayer.class.isAssignableFrom(blacklisted) || this.isInWhitelist(blacklisted))
					blacklistIterator.remove();
			}
		}
	}
	
	//Class check logic, implemented in subfunctions for easier (and public) reference
	public boolean isInBlacklist(Class<? extends Entity> toCheck) { return isInClassList(toCheck, blacklist); }
	public boolean isInWhitelist(Class<? extends Entity> toCheck) { return isInClassList(toCheck, whitelist); }
	private static boolean isInClassList(Class<? extends Entity> toCheck, Set<Class<? extends Entity>> list)
	{
		if(toCheck != null)
		{
			for(Class<? extends Entity> checker : list)
			{
				if(checker.isAssignableFrom(toCheck))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Updates the bounding box, then retrieves all the entities inside it.
	 * From the entities obtained this way, selectively kills all those in
	 * the blacklist.
	 */
	@Override
	public boolean updateArea()
	{
		//This area is useless!
		//If no entity is set for removal this area has no effect.
		if(this.blacklist.isEmpty()) return true;
		
		
		this.detectionArea.setBounds(this.xCenter - this.xSide / 2,
									 this.yCenter - this.ySide / 2,
									 this.zCenter - this.zSide / 2,
									 this.xCenter + this.xSide / 2,
									 this.yCenter + this.ySide / 2,
									 this.zCenter + this.zSide / 2);
		
		Map<Class<? extends Entity>, List<Entity>> entitiesByClass = groupByClass(this.world.getEntitiesWithinAABB(Entity.class, detectionArea)); 
		Iterator<Map.Entry<Class<? extends Entity>, List<Entity>>> mapIterator = entitiesByClass.entrySet().iterator();
		
		while(mapIterator.hasNext())
		{
			Map.Entry<Class<? extends Entity>, List<Entity>> entry = mapIterator.next();
			Class toCheck = entry.getKey();
			
			byte blacklisted = (byte)-1;
			byte whitelisted = (byte)-1;
			
			//TODO Improve this nightmare!
			for(int i = 0; i < 2; ++i)
			{
				//If the white list is shorter (or equal) or the entity is in the blacklist, and no white list check happened
				if((this.whitelist.size() <= this.blacklist.size() || blacklisted > 0) && whitelisted < 0)
					whitelisted = this.isInWhitelist(toCheck) ? (byte)1 : (byte)0;
				
				//If the black list is shorter, and the entity hasn't been white listed (or checked at all) and no black list check happened
				if(this.blacklist.size() < this.blacklist.size() && whitelisted < 1 && blacklisted < 0)
					blacklisted = this.isInBlacklist(toCheck) ? (byte)1 : (byte)0;
			}
			
			if(blacklisted > 0 && whitelisted < 1)
			{
				for(Entity doomed : entry.getValue())
					doomed.setDead();
			}
		}
		
		return false;
	}
	
	/**
	 * Creates an HashMap that groups all entities provided by their class.
	 * Returns null if the list is null.
	 */
	public static Map<Class<? extends Entity>, List<Entity>> groupByClass(Collection<Entity> toSort)
	{
		if(toSort != null)
		{
			Map<Class<? extends Entity>, List<Entity>> returnValue = new HashMap();
			
			for(Entity processing : toSort)
			{
				if(processing != null)
				{
					Class<? extends Entity> entityClass = processing.getClass();
					
					List<Entity> entitiesOfClass = returnValue.get(entityClass);
					
					if(entitiesOfClass == null)
						returnValue.put(entityClass, (entitiesOfClass = new LinkedList()));
					
					entitiesOfClass.add(processing);
				}
			}
			
			return returnValue;
		}
		else
			return null;
	}
	
	///////////////
	// NBT Saving
	///////////////
	static final String blacklistTag = "ClassesToRemove";
	static final String whitelistTag = "ClassesToKeep";
	
	@Override
	protected NBTTagCompound writeMoreOnNBT(NBTTagCompound toWriteOn)
	{
		NBTTagList blacklist = new NBTTagList();
		
		for(Class<? extends Entity> toConvert : this.blacklist)
		{
			
		}
		
		return toWriteOn;
	}
	
	//TODO Load classes
}
