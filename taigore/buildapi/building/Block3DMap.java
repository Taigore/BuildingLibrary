package taigore.buildapi.building;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.util.Vec3;
import taigore.buildapi.block.IBlock;
import taigore.buildapi.utils.Vec3Int;
import cpw.mods.fml.common.FMLLog;

public class Block3DMap implements Cloneable
{
    //Block register
    protected Set<IBlock> blockIndex = new LinkedHashSet();
    protected byte[] blockMap;
    
    private IBlock[] blockIndexArrayFormCache;
    private List<IBlock> blockIndexListFormCache;
    
    public final int sizeX;
    public final int sizeY;
    public final int sizeZ;
    
    //Placement cache
    private int lastID = 0;
    private IBlock lastBlock = null;
    
    public Block3DMap(int sizeX, int sizeY, int sizeZ, IBlock filler)
    {
        this.sizeX = Math.max(1, sizeX);
        this.sizeY = Math.max(1, sizeY);
        this.sizeZ = Math.max(1, sizeZ);
        
        this.fill(filler);
    }
    
    /**
     * Fills the entire block map with the specified block.
     */
    public Block3DMap fill(IBlock filler)
    {
        this.blockMap = new byte[this.sizeX * this.sizeY * this.sizeZ];
        
        this.blockIndex.clear();
        this.blockIndex.add(filler);
        
        this.lastBlock = filler;
        this.lastID = 0;
        
        return this;
    }
    /**
     * Gets the block with the given ID
     * @param id - A valid ID
     * @return An IBlock from this.blockIDs
     */
<<<<<<< HEAD
    private void uncheckedSetSpot(int posX, int posY, int posZ, IBlock material)
=======
    protected IBlock getBlockFromID(int id) throws ArrayIndexOutOfBoundsException
>>>>>>> MC1.6.2
    {
        if(id < 0 || id >= this.blockIndex.size()) throw new ArrayIndexOutOfBoundsException(String.format("invalid id %d", id));
        
        if(this.blockIndexArrayFormCache == null)
        {
            this.blockIndexArrayFormCache = this.blockIndex.toArray(new IBlock[this.blockIndex.size()]);
            this.blockIndexListFormCache = Arrays.asList(this.blockIndexArrayFormCache);
        }
        
        return this.blockIndexArrayFormCache[id];
    }
    /**
     * Returns the index of the given block
     * @param block
     * @return A byte between 0 and Byte.MAX_VALUE, or -1 if the block is not index
     */
    protected byte getIDFromBlock(IBlock block)
    {
        if(this.blockIndexArrayFormCache == null)
        {
            this.blockIndexArrayFormCache = this.blockIndex.toArray(new IBlock[this.blockIndex.size()]);
            this.blockIndexListFormCache = Arrays.asList(this.blockIndexArrayFormCache);
        }
        
        return (byte)this.blockIndexListFormCache.indexOf(block);
    }
    /**
     * Adds a new block to the block index, and returns its ID.
     * @param toAdd - A block
     * @return A byte between 0 and Byte.MAX_VALUE
     * @throws RuntimeException If more than Byte.MAX_VALUE blocks are in the index
     */
<<<<<<< HEAD
    public Block3DMap setSpot(int posX, int posY, int posZ, IBlock material)
=======
    protected byte addBlockID(IBlock toAdd) throws RuntimeException
>>>>>>> MC1.6.2
    {
        //IDs are capped to a byte. With the block system, no structure should need more than
        //127 block type, since a structure that huge and complex should be made with smaller
        //subcomponents, rather than an immense super-structure.
        if(this.blockIndex.size() > Byte.MAX_VALUE) throw new RuntimeException(String.format("too many block types", Byte.MAX_VALUE));
        
        if(this.blockIndex.add(toAdd))
        {
            this.blockIndexArrayFormCache = null;
            this.blockIndexListFormCache = null;
        }
        
        return this.getIDFromBlock(toAdd);
    }
    
    //////////////////////////////
    // Low level set/get methods
    //////////////////////////////
    /**
     * Sets a spot in the block map.
     * @throws ArrayIndexOutOfBoundsException If the point is outside the map volume.
     * @throws RuntimeException When the map has more than Byte.MAX_VALUE block types.
     */
    public Block3DMap setSpot(int posX, int posY, int posZ, IBlock material) throws Exception
    {
        try
        {
            if(posX < 0 || posX >= this.sizeX) throw new ArrayIndexOutOfBoundsException(String.format("invalid x"));
            if(posY < 0 || posY >= this.sizeY) throw new ArrayIndexOutOfBoundsException(String.format("invalid y"));
            if(posZ < 0 || posZ >= this.sizeZ) throw new ArrayIndexOutOfBoundsException(String.format("invalid z"));
            
            if((this.lastBlock != null && !this.lastBlock.equals(material)) || (this.lastBlock == null && material != null))
            {
                this.lastBlock = material;
                this.lastID = this.getIDFromBlock(material);
                
                if(this.lastID < 0)
                    this.lastID = this.addBlockID(material);
            }
             
            int realIndex = ((posX * this.sizeY) + posY) * this.sizeZ + posZ;
            this.blockMap[realIndex] = (byte)(this.lastID & Byte.MAX_VALUE);
        }
        catch(Exception e)
        {
            String error = String.format("Taigore Building Library: invalid set spot %d, %d, %d on map size %d, %d, %d", posX, posY, posZ, this.sizeX, this.sizeY, this.sizeZ);
            throw (Exception)new Exception(error).initCause(e);
        }
        
        return this;
    }
    /**
     * Sets a spot in the block map.
     * Uses setSpot(int, int, int, IAbstractBlock), but allows
     * to pass a position as argument.
     * @throws Exception Containing the real cause
     * @throws ArrayIndexOutOfBoundsException If the point is outside the map volume.
     * @throws RuntimeException When the map has more than Byte.MAX_VALUE block types.
     */
<<<<<<< HEAD
    public Block3DMap setSpot(Vec3Int toSet, IBlock material)
=======
    public Block3DMap setSpot(Vec3Int toSet, IBlock material) throws Exception
>>>>>>> MC1.6.2
    {
        if(toSet != null)
            this.setSpot(toSet.x, toSet.y, toSet.z, material);
        
        return this;
    }
    
    /**
     * Gets the block at the given coordinates.
     * @throws ArrayIndexOutOfBoundsException If the point is outside the map volume.
     */
<<<<<<< HEAD
    private IBlock uncheckedGetSpot(int posX, int posY, int posZ)
    {
        int realIndex = ((posX * this.sizeY) + posY) * this.sizeZ + posZ;
        return this.blockIds.get(this.blockMap[realIndex]); 
    }
    /**
     * Gets the block at the given coordinates.
     * Returns null and gives a warning if the coordinates are outside
     * the map.
     */
    public IBlock getSpot(int posX, int posY, int posZ)
    {
        if(posX >= 0 && posX < this.sizeX && posY >= 0 && posY < this.sizeY && posZ >= 0 && posZ < this.sizeZ)
            return this.uncheckedGetSpot(posX, posY, posZ);
        else
=======
    public IBlock getSpot(int posX, int posY, int posZ) throws ArrayIndexOutOfBoundsException
    {
        try
        {
            if(posX < 0 || posX >= this.sizeX) throw new ArrayIndexOutOfBoundsException(String.format("invalid x %d", posX));
            if(posY < 0 || posY >= this.sizeY) throw new ArrayIndexOutOfBoundsException(String.format("invalid y %d", posY));
            if(posZ < 0 || posZ >= this.sizeZ) throw new ArrayIndexOutOfBoundsException(String.format("invalid z %d", posZ));
            
            int realIndex = ((posX * this.sizeY) + posY) * this.sizeZ + posZ;
            return this.getBlockFromID(this.blockMap[realIndex]);
        }
        catch(ArrayIndexOutOfBoundsException e)
>>>>>>> MC1.6.2
        {
            String error = String.format("Taigore Building Library: invalid get spot %d, %d, %d on map size %d, %d, %d", posX, posY, posZ, this.sizeX, this.sizeY, this.sizeZ);

            throw (ArrayIndexOutOfBoundsException)new ArrayIndexOutOfBoundsException(error).initCause(e);
        }
    }
    /**
     * Gets a block from the block map.
     * Uses getSpot(int, int, int), but allows
     * to pass a position as argument.
     */
<<<<<<< HEAD
    public Block3DMap getSpot(Vec3Int toGet)
=======
    public Block3DMap getSpot(Vec3Int toGet) throws ArrayIndexOutOfBoundsException
>>>>>>> MC1.6.2
    {
        if(toGet != null)
            this.getSpot(toGet.x, toGet.y, toGet.z);
        
        return this;
    }
    
    ///////////////////////
    // Drawing primitives
    ///////////////////////
    /**
     * Replaces a 3D rectangle inside the map with the given block.
     * The only blocks replaced are those inside the given rectangle
     * and the map volume.
     * Trying to draw a rectangle fully outside the volume will have no effect whatsoever.
     */
    public Block3DMap drawCube(Vec3Int startPoint, Vec3Int endPoint, IBlock toDraw)
    {
        if(startPoint != null && endPoint != null)
        {
            //Corrects the positions provided
            startPoint = new Vec3Int(startPoint);
            endPoint = new Vec3Int(endPoint);
<<<<<<< HEAD
            boolean intersects = true;
=======
>>>>>>> MC1.6.2
            
            for(int i = 0; i < 3; ++i)
            {
                int startPointCoord = startPoint.get(i);
                int endPointCoord = endPoint.get(i);
                
                if(startPointCoord > endPointCoord)
                {
                    int temp = startPointCoord;
                    startPointCoord = endPointCoord;
                    endPointCoord = temp;
                }
                    
                startPoint.set(i, startPointCoord);
                endPoint.set(i, endPointCoord);
            }
            
            //Actually draws the cube
            for(int i = Math.max(0, startPoint.x); i <= Math.min(this.sizeX - 1, endPoint.x); ++i)
                for(int j = Math.max(0, startPoint.y); j <= Math.min(this.sizeY - 1, endPoint.y); ++j)
                    for(int k = Math.max(0, startPoint.z); k <= Math.min(this.sizeZ - 1, endPoint.z); ++k)
                    {
                        try
                        {
                            this.setSpot(i, j, k, toDraw);
                        }
                        catch(Exception e)
                        {
                            Throwable cause = e.getCause();
                            
                            if(ArrayIndexOutOfBoundsException.class.isInstance(cause))
                            {
                                //Checks if any of the coordinates have been incremented, and if so,
                                //identifies the coordinate responsible for the failure and tries to
                                //remedy. Otherwise gives up and reports an error
                                if(k > Math.max(0, startPoint.z))
                                    endPoint.z = k - 1;
                                else if(j > Math.max(0, startPoint.y))
                                    endPoint.y = j - 1;
                                else if(i > Math.max(0, startPoint.x))
                                    endPoint.x = i - 1;
                                else
                                {
                                    FMLLog.info("Taigore Building Library: unable to draw cube %s - %s", String.valueOf(startPoint), String.valueOf(endPoint));
                                    FMLLog.warning(e.getMessage());
                                    if(e.getCause() != null)
                                        FMLLog.warning(e.getCause().getMessage());
                                    
                                    return this;
                                }
                            }
                            else if(RuntimeException.class.isInstance(cause))
                            {
                                //Simply gives up, there is nothing to do
                                FMLLog.warning("Taigore Building Library: unable to draw cube with material %s", String.valueOf(toDraw));
                                FMLLog.warning(e.getMessage());
                                if(e.getCause() != null)
                                    FMLLog.warning(e.getCause().getMessage());
                                    
                                return this;
                            }
                            else
                            {
                                FMLLog.info("Taigore Building Library: Unexpected exception %s", e != null ? e.getClass() : "null");
                                e.printStackTrace();
                            }
                        }
                    }
        }
        
        return this;
    }
    
    /**
     * Draws a pixelated line from the startPosition to the endPosition.
     * Nothing fancy to see, but useful to draw diagonals or slowly rising
     * ramps.
     */
    public Block3DMap drawLine(Vec3Int startPosition, Vec3Int endPosition, IBlock material)
    {
        if(startPosition != null && endPosition != null)
        {
            //Corrects the positions provided
            startPosition = new Vec3Int(startPosition);
            endPosition = new Vec3Int(endPosition);
<<<<<<< HEAD
            boolean intersects = true;
=======
>>>>>>> MC1.6.2
            
            for(int i = 0; i < 3; ++i)
            {
                int startPointCoord = startPosition.get(i);
                int endPointCoord = endPosition.get(i);
                
                if(startPointCoord > endPointCoord)
                {
                    int temp = startPointCoord;
                    startPointCoord = endPointCoord;
                    endPointCoord = temp;
                }
                
                startPosition.set(i, startPointCoord);
                endPosition.set(i, endPointCoord);
            }
            
            Vec3Int delta = new Vec3Int(endPosition).addCoordinates(new Vec3Int(-startPosition.x, -startPosition.y, -startPosition.z));
            int greatestCoord = Math.max(delta.x, Math.max(delta.y, delta.z));
            
            if(greatestCoord > 0)
            {
                Vec3 direction = Vec3.createVectorHelper((double)delta.x / greatestCoord, (double)delta.y / greatestCoord, (double)delta.z / greatestCoord);
                Vec3Int pointer = new Vec3Int();
                int i = 0;
                
                for(int j = 0; j < 3; ++j)
                {
                    int coord = startPosition.get(j);
                    double dirCoord = j == 0 ? direction.xCoord : (j == 1 ? direction.yCoord : direction.zCoord);
                    
                    if(coord < 0)
                        i = Math.max(i, (int)Math.ceil(((double)-coord) / dirCoord));
                }
                
                for(; i <= greatestCoord; ++i)
                {
                    pointer.reset(startPosition);
                    pointer.x += direction.xCoord * i;
                    pointer.y += direction.yCoord * i;
                    pointer.z += direction.zCoord * i;
                    
                    try
                    {
                        this.setSpot(pointer.x, pointer.y, pointer.z, material);
                    }
                    catch(Exception e)
                    {
                        //Reports the line drawing failure and the cause and subcauses of it
                        if(e != null)
                        {
                            String errorReport;
                            
                            if(i == 0)
                                errorReport = String.format("No cycle completed for line %s - %s", String.valueOf(startPosition), String.valueOf(endPosition));
                            else
                                errorReport = String.format("Completed %d cycles on line %s - %s. Drawn from %s to %s", i, String.valueOf(startPosition), String.valueOf(endPosition), String.valueOf(startPosition), String.valueOf(pointer));
                            
                            FMLLog.warning("Taigore Building Library: exception drawing line.");
                            FMLLog.warning(errorReport);
                            FMLLog.warning(e.getMessage());
                            
                            for(Throwable cause = e.getCause(); cause != null; cause = cause.getCause())
                                FMLLog.warning(cause.getMessage());
                        }
                    }
                }
            }
        }
        
        return this;
    }
    
<<<<<<< HEAD
    /**
     * Returns an array of blocks.
     * The blocks are ordered this way:
     * (X: 0; Y: 0; Z: 0), (X: 0; Y: 0; Z: 1)... (X: 0; Y: 1; Z: 0), (X: 0; Y: 1; Z: 1)... (X: 1; Y: 0; Z: 0)...
     */
    public IBlock[] getAsBlockArray()
    {
        IBlock[] serializedVolume = new IBlock[this.blockMap.length];
=======
    @Override
    public Block3DMap clone()
	{
	    try
	    {
    	    Block3DMap returnValue = (Block3DMap)super.clone();
    	    
    	    returnValue.blockIndex = new LinkedHashSet(this.blockIndex);
    	    returnValue.blockIndexArrayFormCache = null;
    	    returnValue.blockIndexListFormCache = null;
    	    returnValue.blockMap = this.blockMap.clone();
    	    
    	    return returnValue;
	    }
	    catch (CloneNotSupportedException e)
        {
	        FMLLog.info("Clone failed");
            e.printStackTrace();
            
            return null;
        }
	}
    
    @Override
    public boolean equals(Object toCompare)
    {
        if(toCompare == null) return false;
        if(this == toCompare) return true;
>>>>>>> MC1.6.2
        
        if(this.getClass().isInstance(toCompare))
        {
            Block3DMap mapToCompare = (Block3DMap)toCompare;
            
            if(this.sizeX == mapToCompare.sizeX
            && this.sizeY == mapToCompare.sizeY
            && this.sizeZ == mapToCompare.sizeZ)
            {
                for(int i = 0; i < this.blockMap.length; ++i)
                    if(this.blockMap[i] != mapToCompare.blockMap[i])
                        return false;
                
                return this.blockIndex.equals(mapToCompare.blockIndex);
            }
        }
        
        return false;
    }
}
