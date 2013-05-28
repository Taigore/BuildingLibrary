package taigore.buildapi;

import net.minecraft.nbt.NBTTagCompound;


public class Position
{
	public int x = 0;
	public int y = 0;
	public int z = 0;
	
	public Position() {}
	public Position(Position toCopy) { this.reset(toCopy); }
	public Position(int posX, int posY, int posZ) { this.reset(posX, posY, posZ); }
	
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
	public Position set(int index, int value)
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
	public Position reset(Position toCopy)
	{
		if(toCopy != null)
			this.reset(toCopy.x, toCopy.y, toCopy.z);
		
		return this;
	}
	/**
	 * Sets the coordinates of this position to the given parameters.
	 * Returns this.
	 */
	public Position reset(int posX, int posY, int posZ)
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
	public Position rotate(Rotation facing)
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
	public Position addCoordinates(Position toAdd)
	{
		this.x += toAdd.x;
		this.y += toAdd.y;
		this.z += toAdd.z;
		return this;
	}
	
	@Override
	public String toString()
	{
		return String.format("X: %d\tY: %d\tZ: %d", this.x, this.y, this.z);
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
	public static Position getFromTag(NBTTagCompound nbtData)
	{
		if(nbtData.hasKey("X") && nbtData.hasKey("Y") && nbtData.hasKey("Z"))
			return new Position(nbtData.getInteger("X"), nbtData.getInteger("Y"), nbtData.getInteger("Z"));
		else
			return null;
	}
}