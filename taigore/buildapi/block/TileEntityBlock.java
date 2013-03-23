package taigore.buildapi.block;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import taigore.buildapi.Position;
import taigore.buildapi.Rotation;
import taigore.crypt.TileEntityTreasureChest;
import cpw.mods.fml.common.FMLLog;

public class TileEntityBlock implements IAbstractBlock
{
	//Common use
	public static final TileEntityBlock singleTreasureChest = TileEntityBlock.make(FacingBlock.simpleChestPlusX, TileEntityTreasureChest.class);
	
	private IAbstractBlock visibleBlock = StaticBlock.noEdit;
	private Constructor<? extends TileEntity> maker;
	private Map<String, NBTBase> extraData = new HashMap();
	private Rotation rotation = Rotation.NO_ROTATION;
	
	/**
	 * Creates a tile entity of the specified type with no extra data.
	 * A block of the type given will also be placed on the same coordinates.
	 */
	public static TileEntityBlock make(IAbstractBlock visible, Class<? extends TileEntity> tileEntity)
	{
		return make(visible, tileEntity, null);
	}
	
	/**
	 * Creates a tile entity of the specified type with extra data.
	 * The tile entity itself must manage this extra data in its "readFromNBT"
	 * method, otherwise it will be discarded.
	 * A block of the given type will also be placed on the same coordinates.
	 */
	public static TileEntityBlock make(IAbstractBlock visible, Class<? extends TileEntity> tileEntity, Collection<NBTBase> extraData)
	{
		if(tileEntity != null)
		{
			try
			{
				Constructor <? extends TileEntity> maker = tileEntity.getConstructor();
				
				return new TileEntityBlock(visible, maker, extraData);
			}
			catch(Exception e)
			{
				FMLLog.severe("No parameterless constructor for tileEntity class %s. Skipping TileEntityBlock creation", tileEntity.getName());
				return null;
			}
		}
		else return null;
	}
	private TileEntityBlock(IAbstractBlock visible, Constructor<? extends TileEntity> maker, Collection<NBTBase> extraData)
	{
		//TODO Potential bug: only one extra data was saved in several occasions
		this.visibleBlock = visible;
		this.maker = maker;
		
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
	public void drawAt(Position startPosition, Rotation facing,	Random generator, World canvas)
	{
		if(this.visibleBlock != StaticBlock.noEdit)
			this.visibleBlock.drawAt(startPosition, this.rotation.add(facing), generator, canvas);
		
		try
		{
			TileEntity toPlace = maker.newInstance();
			
			if(toPlace != null)
			{
				//Applying the extra data
				NBTTagCompound tileData = new NBTTagCompound();
				toPlace.writeToNBT(tileData);
				
				for(Map.Entry<String, NBTBase> toApply : this.extraData.entrySet())
					tileData.setTag(toApply.getKey(), toApply.getValue());
				
				toPlace.readFromNBT(tileData);
				
				canvas.setBlockTileEntity(startPosition.x, startPosition.y, startPosition.z, toPlace);
			}
		}
		catch(Exception e)
		{
			FMLLog.severe("Failed to create tile entity at %s. Exception: %s", startPosition.toString(), e.toString());
		}
	}
	
	@Override
	public TileEntityBlock copy()
	{
		TileEntityBlock copy = new TileEntityBlock(this.visibleBlock, this.maker, null);
		
		for(Map.Entry<String, NBTBase> toCopy : this.extraData.entrySet())
			copy.extraData.put(toCopy.getKey(), toCopy.getValue().copy());
		
		return copy;
	}
	
	@Override
	public TileEntityBlock rotate(Rotation rotation)
	{
		this.rotation = this.rotation.add(rotation);
		return this;
	}
}
