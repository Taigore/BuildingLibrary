package taigore.crypt;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityMonsterClearer extends TileEntity
{
	static
	{
		TileEntity.addMapping(TileEntityMonsterClearer.class, "MonsterClearer");
	}
	
	//The space that needs to be kept clear from spawned entities
	private final AxisAlignedBB managedSpace = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
	
	private double xOffset = 0.0d;
	private double yOffset = 0.0d;
	private double zOffset = 0.0d;
	
	@Override
	public void updateEntity()
	{
		this.managedSpace.minX = Math.min(this.xCoord, this.xCoord + this.xOffset);
		this.managedSpace.maxX = Math.max(this.xCoord, this.xCoord + this.xOffset);
		
		this.managedSpace.minY = Math.min(this.yCoord, this.yCoord + this.yOffset);
		this.managedSpace.maxY = Math.max(this.yCoord, this.yCoord + this.yOffset);
		
		this.managedSpace.minZ = Math.min(this.zCoord, this.zCoord + this.zOffset);
		this.managedSpace.maxZ = Math.max(this.zCoord, this.zCoord + this.zOffset);
		
		List<EntityLiving> toClear = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, this.managedSpace);
		NBTTagCompound entityData = new NBTTagCompound();
		
		for(EntityLiving toDelete : toClear)
		{
			if(!EntityPlayer.class.isInstance(toDelete))
			{
				//All of this because the actual field persistenceRequired is private...
				entityData.removeTag("PersistenceRequired");
				toDelete.writeToNBT(entityData);
				
				if(!entityData.getBoolean("PersistenceRequired"))
					toDelete.setDead();
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data)
	{
		super.readFromNBT(data);
		
		this.xOffset = data.getDouble("xOffset");
		this.yOffset = data.getDouble("yOffset");
		this.zOffset = data.getDouble("zOffset");
	}
	@Override
	public void writeToNBT(NBTTagCompound data)
	{
		super.writeToNBT(data);
		
		data.setDouble("xOffset", this.managedSpace.maxX - this.managedSpace.minX);
		data.setDouble("xOffset", this.managedSpace.maxX - this.managedSpace.minX);
		data.setDouble("xOffset", this.managedSpace.maxX - this.managedSpace.minX);
	}
	/**
	 * Writes the given tile entity data on the given tag.
	 * If the tag is then loaded into a TileEntity, those three doubles
	 * will define the area, starting from the TileEntity location, that
	 * will be kept clear from spawned entities.
	 */
	public static NBTTagCompound setTileEntityOffsetInNBT(double xOffset, double yOffset, double zOffset, NBTTagCompound toEdit)
	{
		toEdit.setDouble("xOffset", xOffset);
		toEdit.setDouble("xOffset", yOffset);
		toEdit.setDouble("xOffset", zOffset);
		
		return toEdit;
	}
}
