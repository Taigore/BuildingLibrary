package taigore.crypt;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class TileEntityTreasureChest extends TileEntity
{
	static
	{
		TileEntity.addMapping(TileEntityTreasureChest.class, "TreasureChest");
	}
	
	@Override
	public void updateEntity()
	{
		Random numbers = new Random();
		TileEntityChest treasureChestEntity = new TileEntityChest();
		WeightedRandomChestContent[] loot = ChestGenHooks.getItems(ChestGenHooks.DUNGEON_CHEST, numbers);
		
		WeightedRandomChestContent.generateChestContents(numbers, loot, treasureChestEntity, 16);
		
		this.invalidate();
		this.worldObj.setBlockTileEntity(this.xCoord, this.yCoord, this.zCoord, treasureChestEntity);
	}
}
