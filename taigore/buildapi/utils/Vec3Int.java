package taigore.buildapi.utils;

import net.minecraft.nbt.NBTTagCompound;


public class Vec3Int
{
	public int x = 0;
	public int y = 0;
	public int z = 0;
	
	public Vec3Int() {}
	public Vec3Int(Vec3Int toCopy) { this.reset(toCopy); }
	public Vec3Int(int posX, int posY, int posZ) { this.reset(posX, posY, posZ); }
	
	/**
	 * Array like method.
	 * Returns the given coordinate of this position.
	 * @param index - X: 0; Y: 1; Z: 2
	 */
	public int get(int index)
	{
		switch(index)
		{
			case 0:
				return this.x;
				
			case 1:
				return this.y;
				
			case 2:
				return this.z;
				
			default:
				throw new IndexOutOfBoundsException(String.valueOf(index));
		}
	}
	/**
	 * Array like method.
	 * Sets the given coordinate of this to the value provided.
	 * Returns this.
	 * @param index - X: 0; Y: 1; Z: 2
	 */
	public Vec3Int set(int index, int value)
	{
		switch(index)
		{
			case 0:
				this.x = value;
			break;
				
			case 1:
				this.y = value;
			break;
				
			case 2:
				this.z = value;
			break;
				
			default:
				throw new IndexOutOfBoundsException(String.valueOf(index));
		}
		
		return this;
	}
	/**
	 * Copies the coordinates of the given Position on this.
	 * Does nothing with a null parameter.
	 * Returns this.
	 */
	public Vec3Int reset(Vec3Int toCopy)
	{
		if(toCopy != null)
			this.reset(toCopy.x, toCopy.y, toCopy.z);
		
		return this;
	}
	/**
	 * Sets the coordinates of this position to the given parameters.
	 * Returns this.
	 */
	public Vec3Int reset(int posX, int posY, int posZ)
	{
		this.x = posX;
		this.y = posY;
		this.z = posZ;
		return this;
	}
	/**
	 * Rotates the coordinate of this Position around the Y axis.
	 * Returns this.
	 */
	public Vec3Int rotate(Rotation facing)
	{
		int prevX = this.x;
		int prevZ = this.z;
		
		switch(facing)
		{
			case CLOCKWISE:
				this.x = -prevZ;
				this.z = +prevX;
			break;
			
			case COUNTERCLOCKWISE:
				this.x = +prevZ;
				this.z = -prevX;
			break;
			
			case REVERSE:
				this.x = -prevX;
				this.z = -prevZ;
			break;
			
			case NO_ROTATION:
			default:
				this.x = +prevX;
				this.z = +prevZ;
		}
		
		return this;
	}
	/**
	 * Adds the given position coordinates to this.
	 * Returns this.
	 */
	public Vec3Int addCoordinates(Vec3Int toAdd)
	{
		this.x += toAdd.x;
		this.y += toAdd.y;
		this.z += toAdd.z;
		return this;
	}
	
	@Override
	public String toString()
	{
		return String.format("(%d; %d; %d)", this.x, this.y, this.z);
	}
	/**
	 * Saves this position on an NBTTag.
	 */
	public NBTTagCompound getPositionTag(NBTTagCompound positionData)
	{
		if(positionData == null) positionData = new NBTTagCompound();
		
		positionData.setInteger("X", this.x);
		positionData.setInteger("Y", this.y);
		positionData.setInteger("Z", this.z);
		
		return positionData;
	}
	/**
	 * Reads a Position from an NBTTagCompound.
	 */
	public static Vec3Int getFromTag(NBTTagCompound nbtData)
	{
		if(nbtData.hasKey("X") && nbtData.hasKey("Y") && nbtData.hasKey("Z"))
			return new Vec3Int(nbtData.getInteger("X"), nbtData.getInteger("Y"), nbtData.getInteger("Z"));
		else
			return null;
	}
	
	///////////
	// Object
	///////////
	public boolean equals(Object toCompare)
	{
	    if(this == toCompare) return true;
	    
	    if(this.getClass().isInstance(toCompare))
	    {
	        Vec3Int positionToCompare = (Vec3Int)toCompare;
	        
	        return this.x == positionToCompare.x
	            && this.y == positionToCompare.y
	            && this.z == positionToCompare.z;
	    }
	    
	    return false;
	}
}