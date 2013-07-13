package taigore.buildapi.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class FacingBlock implements IBlock
{
	protected int id;
	protected NBTTagCompound tileEntityData = null;
	
	/**
	 * This kind of block provides a different metadata depending
	 * on the rotation it is drawn with.
	 */
	protected FacingBlock(int id) { this.setID(id); }
	
	public FacingBlock setID(int id)
	{
	    if(id < 0) id = 0;
	    this.id = id & 0x00000FFF;
	    return this;
	}
	/**
     * Saves the properties of the TileEntity provided, to replicate
     * them in every block placed.
     */
    public FacingBlock setTileEntityData(TileEntity toSave)
    {
        if(toSave == null)
            this.tileEntityData = null;
        else
        {
            if(this.tileEntityData == null)
                this.tileEntityData = new NBTTagCompound();
            
            toSave.writeToNBT(this.tileEntityData);
        }
        
        return this;
    }
}