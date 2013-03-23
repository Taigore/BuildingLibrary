package taigore.buildapi.area;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;

public abstract class Area
{
	private static final Map<Byte, Class<? extends Area>> mappings = new HashMap();
	private static final Map<Class<? extends Area>, Byte> classToId = new HashMap();
	
	protected static void addMapping(byte identifier, Class<? extends Area> areaSubclass)
	{
		if(areaSubclass != null)
		{
			if(mappings.containsKey(identifier))
				FMLLog.warning("Overwriting mapping %d with class %s", identifier, areaSubclass.getName());
			
			mappings.put(identifier, areaSubclass);
			classToId.put(areaSubclass, identifier);
		}
	}
	
	public Area() {}
	
	public Area(double xStart, double xEnd, double yStart, double yEnd, double zStart, double zEnd)
	{
		this.xCenter = (xStart + xEnd) / 2;
		this.yCenter = (yStart + yEnd) / 2;
		this.zCenter = (zStart + zEnd) / 2;
		
		this.xSide = Math.abs(xStart - xEnd);
		this.ySide = Math.abs(yStart - yEnd);
		this.zSide = Math.abs(zStart - zEnd);
	}
	
	World world;
	
	double xCenter = 0.0d;
	double yCenter = 0.0d;
	double zCenter = 0.0d;
	
	double xSide = 0.0d;
	double ySide = 0.0d;
	double zSide = 0.0d;
	
	//TODO Comment
	//Mind the auto-clear mechanism
	public abstract boolean updateArea();
	
	///////////////
	// NBT Save/Load
	///////////////
	
	static final String centerTag = "CenterPosition";
	static final String sideTag = "SideSize";
	static final String areaTypeTag = "ClassId";
	//TODO Comment
	public NBTTagCompound writeOnNBT(NBTTagCompound toWriteOn)
	{
		if(toWriteOn == null) toWriteOn = new NBTTagCompound();
		
		//Saving class type
		byte classId = classToId.get(this.getClass());
		
		toWriteOn.setByte(areaTypeTag, classId);
		
		//Saving area center
		NBTTagList centerPosition = new NBTTagList();
		
		centerPosition.appendTag(new NBTTagDouble("", this.xCenter));
		centerPosition.appendTag(new NBTTagDouble("", this.yCenter));
		centerPosition.appendTag(new NBTTagDouble("", this.zCenter));
		
		toWriteOn.setTag(centerTag, centerPosition);
		
		//Saving area size
		NBTTagList sideSize = new NBTTagList();
		
		sideSize.appendTag(new NBTTagDouble("", this.xSide));
		sideSize.appendTag(new NBTTagDouble("", this.ySide));
		sideSize.appendTag(new NBTTagDouble("", this.zSide));
		
		toWriteOn.setTag(sideTag, sideSize);
		
		//Saving extra data
		return this.writeMoreOnNBT(toWriteOn);
	}
	//TODO Comment
	protected NBTTagCompound writeMoreOnNBT(NBTTagCompound toWriteOn) { return toWriteOn;}
	//TODO Comment
	public static Area makeFromNBT(NBTTagCompound toMakeFrom)
	{
		//Loading area type
		byte classId = toMakeFrom.getByte(areaTypeTag);
		Class<? extends Area> areaClass = mappings.get(classId);
		
		try
		{
			if(areaClass != null)
			{
				Area loadedArea = areaClass.newInstance();
				
				return loadedArea.readFromNBT(toMakeFrom);
			}
			else
			{
				FMLLog.warning("Skipping unknown area type with ID %d", classId);
				return null;
			}
		}
		catch(Exception e)
		{
			FMLLog.severe("Exception while loading special area, with id %d and registered class %s", classId, String.valueOf(areaClass));
			e.printStackTrace();
			return null;
		}
	}
	//TODO Comment
	public Area readFromNBT(NBTTagCompound toReadFrom)
	{
		//Loading position
		NBTTagList centerPosition = toReadFrom.getTagList(centerTag);
		
		this.xCenter = ((NBTTagDouble)centerPosition.tagAt(0)).data;
		this.yCenter = ((NBTTagDouble)centerPosition.tagAt(1)).data;
		this.zCenter = ((NBTTagDouble)centerPosition.tagAt(2)).data;
		
		//Loading size
		NBTTagList sideSize = toReadFrom.getTagList(sideTag);
		
		this.xSide = ((NBTTagDouble)sideSize.tagAt(0)).data;
		this.ySide = ((NBTTagDouble)sideSize.tagAt(1)).data;
		this.zSide = ((NBTTagDouble)sideSize.tagAt(2)).data;
		
		//Loading extra data
		this.readMoreFromNBT(toReadFrom);
		
		return this;
	}
	//TODO Comment
	protected Area readMoreFromNBT(NBTTagCompound toReadFrom) { return this; }
}
