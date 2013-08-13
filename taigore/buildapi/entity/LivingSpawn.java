package taigore.buildapi.entity;

import java.util.Arrays;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import taigore.buildapi.utils.MagicalEffect;

public class LivingSpawn extends SimpleSpawn
{
    public LivingSpawn(Class<? extends EntityLiving> spawnType) { super(spawnType); }
    
    public LivingSpawn setHealth(int health)
    {
        this.setTag("Health", new NBTTagInt("Health", health));
        return this;
    }
    
    public LivingSpawn setPersistent(boolean persistence)
    {
        if(persistence)
            this.setTag("PersistenceRequired", new NBTTagByte("PersistenceRequired", (byte)1));
        else
            this.extraData.remove("PersistenceRequired");
        
        return this;
    }
    
    public LivingSpawn addPotionEffect(MagicalEffect effectType, int level, int duration)
    {
        if(effectType != null && duration > 0)
        {
            if(level < 0) level = 1;
            
            PotionEffect toApply = new PotionEffect(effectType.id, duration, level);
            NBTTagList effectList = (NBTTagList)this.extraData.get("ActiveEffects");
            
            if(effectList == null)
            {
                effectList = new NBTTagList("ActiveEffects");
                this.setTag("ActiveEffects", effectList);
            }
            
            effectList.appendTag(toApply.writeCustomPotionEffectToNBT(new NBTTagCompound()));
        }
        
        return this;
    }
    
    public LivingSpawn addItemWithDropChance(LivingSlot equipSlot, ItemStack toGive, float dropChance)
    {
        if(equipSlot != null)
        {
            NBTTagList weapons = (NBTTagList) this.extraData.get("Equipment");
            NBTTagList dropChances = (NBTTagList) this.extraData.get("DropChances");
            
            if(weapons == null)
            {
                weapons = new NBTTagList("Equipment");
                
                for(int i = 0; i < 5; ++i) weapons.appendTag(new NBTTagCompound());
                
                this.setTag("Equipment", weapons);
            }
            if(dropChances == null)
            {
                dropChances = new NBTTagList("DropChances");
                
                for(int i = 0; i < 5; ++i) dropChances.appendTag(new NBTTagFloat("", 0.05f));
                
                this.setTag("DropChances", dropChances);
            }
            
            if(toGive != null) toGive.writeToNBT((NBTTagCompound)weapons.tagAt(equipSlot.getPosition()));
            else ((NBTTagCompound)weapons.tagAt(equipSlot.getPosition())).getTags().clear();
            
            ((NBTTagFloat)dropChances.tagAt(equipSlot.getPosition())).data = dropChance;
        }
        
        return this;
    }
    
    public enum LivingSlot
    {
        WEAPON,
        BOOTS,
        LEGS,
        CHEST,
        HEAD;
        
        public int getPosition() { return Arrays.asList(LivingSlot.values()).indexOf(this); }
    }
}
