package taigore.crypt;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import taigore.buildapi.Position;
import taigore.buildapi.activestructure.ActiveStructure;
import taigore.buildapi.building.StaticBuilding;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenCrypt implements IWorldGenerator
{
	private static StaticBuilding cryptComplex;
	/*
	static
	{
		Position pointA = new Position();
		Position pointB = new Position();
		Position offset = new Position();
		
		RandomBlock randomDecayWallBlock = new RandomBlock();
		{
			randomDecayWallBlock.addBlocks(Block.stoneBrick.blockID, new int[]{0, 1, 2}, new int[]{15, 1, 1});
			randomDecayWallBlock.addBlock(Block.silverfish.blockID, 2, 4);
		}
		
		//Royal guard
		//Base: Skeleton
		//Equipment: Knockback I iron sword, full iron armor with protection I
		//Special effects: immune to fire, regeneration I, resistance I
		MonsterBlock royalGuard = MonsterBlock.make(EntitySkeleton.class, null);
		{
			//Permanent entity
			royalGuard.setDataTag(new NBTTagByte("PersistenceRequired", (byte)1));
			
			//Equipment
			{
				//Slot 0: Weapon
				//Slot 1: Boots
				//Slot 2: Leggings
				//Slot 3: Chestplate
				//Slot 4: Helm 
				NBTTagList royalGuardEquipment = new NBTTagList("Equipment");
				
				//Slot 0
				ItemStack enchantedSword = new ItemStack(Item.swordSteel);
				enchantedSword.addEnchantment(Enchantment.knockback, 1);
				royalGuardEquipment.appendTag(enchantedSword.writeToNBT(new NBTTagCompound()));
				//Slot 1 to 4
				Item[] armorTypes = {Item.bootsSteel, Item.legsSteel, Item.plateSteel, Item.helmetSteel};
				for(int i = 0; i < armorTypes.length; ++i)
				{
					ItemStack toEquip = new ItemStack(armorTypes[i]);
					toEquip.addEnchantment(Enchantment.projectileProtection, 1);
					royalGuardEquipment.appendTag(toEquip.writeToNBT(new NBTTagCompound()));
				}
				
				royalGuard.setDataTag(royalGuardEquipment);
			}
			
			//Preventing equipment drops
			{
				NBTTagList dropChances = new NBTTagList("DropChances");
				
				for(int i = 0; i < 5; ++i)
				{
					NBTTagFloat dropChance = new NBTTagFloat("", 0.0f);
					dropChances.appendTag(dropChance);
				}
				
				royalGuard.setDataTag(dropChances);
			}
			/*
			//Making them sturdier
			royalGuard.setDataTag(new NBTTagInt("Health", 20));*/
			
			//Special effects
			/*{
				royalGuard.addPotionEffect(MagicalEffect.FIRE_RESISTANCE);
				royalGuard.addPotionEffect(MagicalEffect.REGENERATION);
				royalGuard.addPotionEffect(MagicalEffect.RESISTANCE);
				
				/*
				NBTTagList activeEffects = new NBTTagList("ActiveEffects");
				
				PotionEffect fireResistance = new PotionEffect(12, Integer.MAX_VALUE);
				PotionEffect resistance = new PotionEffect(11, Integer.MAX_VALUE);
				PotionEffect regeneration = new PotionEffect(10, Integer.MAX_VALUE);
				
				activeEffects.appendTag(fireResistance.writeCustomPotionEffectToNBT(new NBTTagCompound()));
				activeEffects.appendTag(resistance.writeCustomPotionEffectToNBT(new NBTTagCompound()));
				activeEffects.appendTag(regeneration.writeCustomPotionEffectToNBT(new NBTTagCompound()));
				
				royalGuard.setDataTag(activeEffects);*/
			/*}
		}
		
		FacingBlock[] normalStairs = {FacingBlock.stoneSmoothStairsPlusX.copy(), null, null, null};
		FacingBlock[] reverseStairs = {FacingBlock.stoneSmoothStairsPlusXReversed.copy(), null, null, null};
		
		for(int i = 1; i < 4; ++i)
		{
			normalStairs[i] = normalStairs[i - 1].copy().rotate(Rotation.CLOCKWISE);
			reverseStairs[i] = reverseStairs[i - 1].copy().rotate(Rotation.CLOCKWISE);
		}
		
		//Defining the entrance
		StaticBuilding entrance = new StaticBuilding(6, 8, 10, randomDecayWallBlock);
		{
			//Empty space within
			entrance.drawCube(pointA.reset(1, 1, 1), pointB.reset(4, 4, 8), StaticBlock.air);
			
			//Roof
			for(int i = 0; i < 2; ++i)
				entrance.drawCube(pointA.reset(i * 4, 6, 0), pointB.reset(i * 4 + 1, 7, 9), StaticBlock.noEdit);
			
			for(int i = 0; i < 2; ++i)
			{
				pointA.reset(i * 5, 5, 0);
				pointB.reset(2 + i, 7, 0);
				
				for(int j = 0; j < 10; ++j)
				{
					entrance.drawLine(pointA, pointB, normalStairs[(1 - i) * 2]);
					
					pointA.z += 1;
					pointB.z += 1;
				}
			}
			
			//Door
			entrance.drawCube(pointA.reset(2, 1, 9), pointB.reset(3, 2, 9), StaticBlock.ironBars);
		}
		
		RepeatingBuilding stairs;
		{
			StaticBuilding stairsUnit = new StaticBuilding(6, 6, 1, randomDecayWallBlock);
			
			//Actual stairs block
			offset.reset(1, 0, 0);
			stairsUnit.drawCube(pointA.reset(2, 0, 0), offset.addCoordinates(pointA), normalStairs[3]);
			//Empty space inside
			offset.reset(3, 3, 0);
			stairsUnit.drawCube(pointA.reset(1, 1, 0), offset.addCoordinates(pointA), StaticBlock.air);
			
			//Cutting the outer corners and filling the inner corners
			Position outerCorner = new Position();
			Position innerCorner = new Position();
			
			for(int i = 0; i < 2; ++i)
			{
				for(int j = 0; j < 2; ++j)
				{
					outerCorner.reset(i * 5, j * 5, 0);
					innerCorner.reset(1 + i * 3, 1 + j * 3, 0);
					
					stairsUnit.setSpot(outerCorner, StaticBlock.noEdit);
					stairsUnit.setSpot(innerCorner, randomDecayWallBlock);
				}
			}
			
			stairs = new RepeatingBuilding(stairsUnit, 0, -1, -1, 30);
		}
		
		StaticBuilding greatDoor = new StaticBuilding(4, 4, 1, randomDecayWallBlock);
		{
			pointA.reset(0, 0, 0);
			offset.reset(3, 1, 0);
			greatDoor.drawCube(pointA, offset.addCoordinates(pointA), StaticBlock.ironBars);
			pointA.reset(1, 0, 0);
			offset.reset(1, 2, 0);
			greatDoor.drawCube(pointA, offset.addCoordinates(pointA), StaticBlock.air);
			
			for(int i = 0; i < 2; ++i)
			{
				pointA.reset(0 + i * 3, 2, 0);
				pointB.reset(1 + i, 3, 0);
				
				greatDoor.drawLine(pointA, pointB, reverseStairs[i * 2]);
			}
		}
		
		//Main chamber
		StaticBuilding mainChamber = new StaticBuilding(12, 7, 17, randomDecayWallBlock);
		{
			//Emptying the inside
			pointA.reset(1, 1, 1);
			offset.reset(9, 4, 14);
			mainChamber.drawCube(pointA, offset.addCoordinates(pointA), StaticBlock.air);
			
			//Defining auxiliary structures
			StaticBuilding stoneBrickPillar = new StaticBuilding(3, 5, 3, StaticBlock.noEdit);
			pointA.reset(1, 0, 1);
			offset.reset(0, 4, 0);
			stoneBrickPillar.drawCube(pointA, offset.addCoordinates(pointA), randomDecayWallBlock);
			
			//Adding decorations
			pointA.reset(offset);
			offset.reset(1, 0, 0);
			for(int i = 0; i < 4; ++i)
			{
				pointB.reset(pointA).addCoordinates(offset);
				stoneBrickPillar.setSpot(pointB, reverseStairs[i]);
				
				offset.rotate(Rotation.CLOCKWISE);
			}
			
			//Creating a 3D pillar pattern, spacing 5 on X, spacing 5 on Z
			RepeatingBuilding pillarField = new RepeatingBuilding(new RepeatingBuilding(stoneBrickPillar, 5, 0, 0, 2), 0, 0, 5, 3);
			//Applying to the main chamber
			mainChamber.attachBuilding(pillarField, 2, 1, 2);
			mainChamber.attachBuilding(greatDoor, 4, 1, 16);
		}
		
		StaticBuilding guardsTomb = new StaticBuilding(12, 10, 12, randomDecayWallBlock);
		{
			//Emptying the space
			pointA.reset(1, 1, 1);
			pointB.reset(10, 8, 10);
			guardsTomb.drawCube(pointA, pointB, StaticBlock.air);
			//Attaching door
			guardsTomb.attachBuilding(greatDoor, 4, 1, 11);
			
			RepeatingBuilding supports;
			RepeatingBuilding reverseSupports;
			{
				StaticBuilding supportUnit = new StaticBuilding(3, 3, 1, randomDecayWallBlock);
				StaticBuilding reversedSupportUnit = new StaticBuilding(3, 3, 1, randomDecayWallBlock);
				
				pointA.reset(1, 0, 0);
				offset.reset(1, 1, 0);
				supportUnit.drawCube(pointA, offset.addCoordinates(pointA), StaticBlock.air);
				offset.reset(1, 1, 0).rotate(Rotation.REVERSE);
				reversedSupportUnit.drawCube(pointA, offset.addCoordinates(pointA), StaticBlock.air);
				
				pointA.reset(0, 0, 0);
				pointB.reset(2, 2, 0);
				supportUnit.drawLine(pointA, pointB, FacingBlock.stoneSmoothStairsPlusXReversed);
				
				pointA.reset(2, 0, 0);
				pointB.reset(0, 2, 0);
				reversedSupportUnit.drawLine(pointA, pointB, reverseStairs[2]);
				
				supports = new RepeatingBuilding(supportUnit, 0, 0, 5, 2);
				reverseSupports = new RepeatingBuilding(reversedSupportUnit, 0, 0, 5, 2);
				
				guardsTomb.attachBuilding(supports, 1, 6, 3);
				guardsTomb.attachBuilding(reverseSupports, 8, 6, 3);
			}
			/*
			StaticBuilding royalGuardSarcophagus = new StaticBuilding(3, 3, 2, randomDecayWallBlock);
			{
				for(int i = 0; i < 2; ++i)
				{
					pointA.reset(i * 2, 2, 0);
					offset.reset(0, 0, 1);
					royalGuardSarcophagus.drawCube(pointA, offset.addCoordinates(pointA), StaticBlock.air);
				}
				royalGuardSarcophagus.setSpot(1, 0, 0, StaticBlock.air);
				royalGuardSarcophagus.setSpot(1, 1, 0, royalGuard);
				
				pointA.reset(1, 0, 1);
				offset.reset(0, 1, 0);
				royalGuardSarcophagus.drawCube(pointA, offset.addCoordinates(pointA), new StaticBlock(Block.glass.blockID));
			}
			
			for(int i = 0; i < 2; ++i)
			{
				guardsTomb.attachBuilding(royalGuardSarcophagus, 1 + i * 7, 1, 1);
			}*/
			/*
			pointA.reset(4, 1, 4);
			offset.reset(3, 0, 0);
			guardsTomb.drawCube(pointA, offset.addCoordinates(pointA), royalGuard);
		}
		
		StaticBuilding kingsTomb = new StaticBuilding(18, 8, 17, randomDecayWallBlock);
		{
			//Withered King
			//Base: Wither Skeleton
			//Health: 20 heart (40)
			//Equipment:
			//- Knockback II diamond sword
			//- Golden helmet with custom name "Withered King's Crown", Thorns VII, Unbreaking V 
			//- Rest is an iron set with Protection II
			//Possible drops: 5% for the Crown
			//Special effects:
			//-Fire immunity (inbuilt as a Wither Skeleton)
			//-Resistance I
			//-Speed I
			//-Regeneration I
			MonsterBlock witheredKing = MonsterBlock.make(EntitySkeleton.class, null);
			{
				//Beefing him up
				witheredKing.setDataTag(new NBTTagShort("Health", (short)40));
				
				//Persistent entity
				witheredKing.setDataTag(new NBTTagByte("PersistenceRequired", (byte)1));
				
				//Wither skeleton
				witheredKing.setDataTag(new NBTTagByte("SkeletonType", (byte)1));
				
				//Equipment
				{
					NBTTagList equipment = new NBTTagList("Equipment");
					
					//Diamond sword
					ItemStack toAdd = new ItemStack(Item.swordDiamond);
					toAdd.addEnchantment(Enchantment.knockback, 2);
					NBTTagCompound nbtToAdd = new NBTTagCompound();
					toAdd.writeToNBT(nbtToAdd);
					equipment.appendTag(nbtToAdd);
					
					//Three pieces of iron armor
					Item[] armorTypes = {Item.bootsSteel, Item.legsSteel, Item.plateSteel};
					
					for(int i = 0; i < armorTypes.length; ++i)
					{
						toAdd = new ItemStack(armorTypes[i]);
						toAdd.addEnchantment(Enchantment.projectileProtection, 1);
						nbtToAdd = new NBTTagCompound();
						toAdd.writeToNBT(nbtToAdd);
						equipment.appendTag(nbtToAdd);
					}
					
					toAdd = new ItemStack(Item.helmetGold);
					toAdd.addEnchantment(Enchantment.thorns, 7);
					toAdd.addEnchantment(Enchantment.unbreaking, 5);
					toAdd.setItemName("Withered King's crown");
					nbtToAdd = new NBTTagCompound();
					toAdd.writeToNBT(nbtToAdd);
					equipment.appendTag(nbtToAdd);
					
					witheredKing.setDataTag(equipment);
				}
				
				//Drop chances (5% for the last item)
				{
					NBTTagList dropChances = new NBTTagList("DropChances");
					
					for(int i = 0; i < 5; ++i)
						dropChances.appendTag(new NBTTagFloat("", (i == 4 ? 0.05f : 0.0f)));
					
					witheredKing.setDataTag(dropChances);
				}
				
				//Special effects
				{
					witheredKing.addPotionEffect(MagicalEffect.REGENERATION);
					witheredKing.addPotionEffect(MagicalEffect.RESISTANCE);
					witheredKing.addPotionEffect(MagicalEffect.SPEED);
				}
			}
			
			//Emptying the inside
			pointA.reset(1, 1, 1);
			pointB.reset(16, 6, 15);
			kingsTomb.drawCube(pointA, pointB, StaticBlock.air);
			
			//Altar base
			for(int i = 0; i < 2; ++i)
			{
				pointA.reset(5 + i, 1 + i, 5 + i);
				offset.reset(7 - i, 0, 6 - i).addCoordinates(pointA);
				kingsTomb.drawCube(pointA, offset, randomDecayWallBlock);
			}
			
			Position[] angles = {new Position(0, 1, 0),
								 new Position(17, 1, 0),
								 pointA.reset(17, 1, 16),
								 pointB.reset(0, 1, 16)};
			Position altarSide = new Position();
			
			for(int i = 0; i < 4; ++i)
			{
				for(int j = 0; j < 2; ++j)
				{
					offset.reset(4 + j, j, 4 + j).rotate(Rotation.getFromID(i));
					offset.addCoordinates(angles[i]);
					
					altarSide.reset(8 - j - (i % 2), 0, 0).rotate(Rotation.getFromID(i));
					altarSide.addCoordinates(offset);
					
					kingsTomb.drawCube(offset, altarSide, normalStairs[(3 + i) % 4]);
				}
			}
			
			//Actual altar
			for(int i = 0; i < 2; ++i)
			{
				pointA.reset(9 - i, 3, 7);
				offset.reset(0, 0, 2).addCoordinates(pointA);
				
				kingsTomb.drawCube(pointA, offset, reverseStairs[i * 2]);
			}
			
			//Door
			kingsTomb.attachBuilding(greatDoor, 7, 1, 16);
			
			//DA KING!
			kingsTomb.setSpot(7, 3, 8, witheredKing);
			
			TileEntityBlock treasureChestBlock = TileEntityBlock.singleTreasureChest.copy().rotate(Rotation.CLOCKWISE);
			{
				pointA.reset(7, 1, 1);
				
				for(int i = 0; i < 2; ++i)
				{
					kingsTomb.setSpot(pointA, treasureChestBlock);
					pointA.x += 3;
				}
			}
		}
		
		guardsTomb.attachBuilding(kingsTomb, -3, 0, -16);
		mainChamber.attachBuilding(guardsTomb, 0, 0, -11);
		stairs.attachBuilding(mainChamber, -3, -1, -17);
		entrance.attachBuilding(stairs, 0, 0, 5);
		
		cryptComplex = entrance;*/
//	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if(chunkX == 5 && chunkZ == 5)
		{
			Position drawPosition = new Position(chunkX * 16, 0, chunkZ * 16);
			Position endPosition = new Position(chunkX * 16 + 160, 50, chunkZ * 16 + 160);
			
			ActiveStructure.fromPositions(drawPosition, endPosition).setPreventSpawn(true);
			
			//cryptComplex.drawOnTheWorld(drawPosition, Rotation.NO_ROTATION, random, world);
		}
	}
}
