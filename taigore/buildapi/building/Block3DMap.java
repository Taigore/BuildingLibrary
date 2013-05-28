package taigore.buildapi.building;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.FMLLog;

import taigore.buildapi.Position;
import taigore.buildapi.block.IAbstractBlock;

public class Block3DMap
{
    private List<IAbstractBlock> blockIds = new LinkedList();
    private byte[] blockMap;
    private int sizeX = 1;
    private int sizeY = 1;
    private int sizeZ = 1;
    
    private int lastId = 0;
    private IAbstractBlock lastBlock = null;
    
    public Block3DMap(int sizeX, int sizeY, int sizeZ, IAbstractBlock filler)
    {
        if(sizeX > 1) this.sizeX = sizeX;
        if(sizeY > 1) this.sizeY = sizeY;
        if(sizeZ > 1) this.sizeZ = sizeZ;
        
        this.fill(filler);
    }
    public Block3DMap(Block3DMap toCopy)
    {
        this.sizeX = toCopy.sizeX;
        this.sizeY = toCopy.sizeY;
        this.sizeZ = toCopy.sizeZ;
        this.blockIds.addAll(toCopy.blockIds);
    }
    
    /**
     * Fills the entire block map with the specified block.
     */
    public Block3DMap fill(IAbstractBlock filler)
    {
        this.blockMap = new byte[this.sizeX * this.sizeY * this.sizeZ];
        
        this.blockIds.clear();
        this.blockIds.add(filler);
        
        this.lastBlock = filler;
        this.lastId = 0;
        
        return this;
    }
    
    //////////////////////////////
    // Low level set/get methods
    //////////////////////////////
    /**
     * Sets the given position to the given block.
     * Private method without coordinate checking.
     */
    private void uncheckedSetSpot(int posX, int posY, int posZ, IAbstractBlock material)
    {
       if(this.lastBlock != material && (this.lastBlock != null && !this.lastBlock.equals(material)))
       {
           this.lastBlock = material;
           this.lastId = this.blockIds.indexOf(material);
           
           if(this.lastId < 0)
           {
               //IDs are capped to a byte. With the block system, no structure should need more than
               //127 block type, since a structure that huge and complex should be made with smaller
               //subcomponents, rather than an immense super-structure.
               if(this.blockIds.size() > Byte.MAX_VALUE) throw new RuntimeException(String.format("Taigore BuildingLibrary: no more than %d block types allowed for a building.", Byte.MAX_VALUE));
               
               this.lastId = this.blockIds.size();
               this.blockIds.add(material);
           }
       }
        
       int realIndex = ((posX * this.sizeY) + posY) * this.sizeZ + posZ;
       this.blockMap[realIndex] = (byte)(this.lastId & Byte.MAX_VALUE);
    }
    /**
     * Sets a spot in the block map, checking that the coordinates
     * are inside the block map volume.
     */
    public Block3DMap setSpot(int posX, int posY, int posZ, IAbstractBlock material)
    {
        if(posX >= 0 && posX < this.sizeX && posY >= 0 && posY < this.sizeY && posZ >= 0 && posZ < this.sizeZ)
            this.uncheckedSetSpot(posX, posY, posZ, material);
        else
            FMLLog.warning("Taigore BuildingLibrary: invalid setSpot (%d; %d; %d) for map size (%d; %d; %d)", posX, posY, posZ, this.sizeX, this.sizeY, this.sizeZ);
        
        return this;
    }
    /**
     * Sets a spot in the block map.
     * Uses setSpot(int, int, int, IAbstractBlock), but allows
     * to pass a position as argument.
     */
    public Block3DMap setSpot(Position toSet, IAbstractBlock material)
    {
        if(toSet != null)
            this.setSpot(toSet.x, toSet.y, toSet.z, material);
        
        return this;
    }
    
    /**
     * Gets the block at the given coordinates.
     * Does not perform checks on the position and the size of the map.
     */
    private IAbstractBlock uncheckedGetSpot(int posX, int posY, int posZ)
    {
        int realIndex = ((posX * this.sizeY) + posY) * this.sizeZ + posZ;
        return this.blockIds.get(this.blockMap[realIndex]); 
    }
    /**
     * Gets the block at the given coordinates.
     * Returns null and gives a warning if the coordinates are outside
     * the map.
     */
    public IAbstractBlock getSpot(int posX, int posY, int posZ)
    {
        if(posX >= 0 && posX < this.sizeX && posY >= 0 && posY < this.sizeY && posZ >= 0 && posZ < this.sizeZ)
            return this.uncheckedGetSpot(posX, posY, posZ);
        else
        {
        	FMLLog.warning("Taigore BuildingLibrary: invalid getSpot (%d; %d; %d) for map size (%d; %d; %d)", posX, posY, posZ, this.sizeX, this.sizeY, this.sizeZ);
        	return null;
        }
    }
    /**
     * Gets a block from the block map.
     * Uses getSpot(int, int, int), but allows
     * to pass a position as argument.
     */
    public Block3DMap getSpot(Position toGet)
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
    public Block3DMap drawCube(Position startPoint, Position endPoint, IAbstractBlock toDraw)
    {
        if(startPoint != null && endPoint != null)
        {
            int[] maxIndex = {this.sizeX - 1, this.sizeY - 1, this.sizeZ - 1};
            
            //Corrects the positions provided
            startPoint = new Position(startPoint);
            endPoint = new Position(endPoint);
            boolean intersects = true;
            
            for(int i = 0; intersects && i < 3; ++i)
            {
                int startPointCoord = startPoint.get(i);
                int endPointCoord = endPoint.get(i);
                
                if((startPointCoord < 0 && endPointCoord < 0) || (startPointCoord > maxIndex[i] && endPointCoord > maxIndex[i]))
                    intersects = false;
                else
                {
                    if(startPointCoord > endPointCoord)
                    {
                        int temp = startPointCoord;
                        startPointCoord = endPointCoord;
                        endPointCoord = temp;
                    }
                    
                    startPoint.set(i, Math.max(0, Math.min(startPointCoord, maxIndex[i])));
                    endPoint.set(i, Math.max(0, Math.min(endPointCoord, maxIndex[i])));
                }
            }
            
            if(intersects)
            {
                for(int i = 0; i < this.sizeX; ++i)
                    for(int j = 0; j < this.sizeY; ++j)
                        for(int k = 0; k < this.sizeZ; ++k)
                            this.uncheckedSetSpot(i, j, k, toDraw);
            }
        }
        
        return this;
    }
    
    /**
     * Draws a pixelated line from the startPosition to the endPosition.
     * Nothing fancy to see, but useful to draw diagonals or slowly rising
     * ramps.
     */
    public Block3DMap drawLine(Position startPosition, Position endPosition, IAbstractBlock material)
    {
        if(startPosition != null && endPosition != null)
        {
            int[] maxIndex = {this.sizeX - 1, this.sizeY - 1, this.sizeZ - 1};
            
            //Corrects the positions provided
            startPosition = new Position(startPosition);
            endPosition = new Position(endPosition);
            boolean intersects = true;
            
            for(int i = 0; intersects && i < 3; ++i)
            {
                int startPointCoord = startPosition.get(i);
                int endPointCoord = endPosition.get(i);
                
                if((startPointCoord < 0 && endPointCoord < 0) || (startPointCoord > maxIndex[i] && endPointCoord > maxIndex[i]))
                    intersects = false;
                else
                {
                    
                    startPosition.set(i, Math.max(0, Math.min(startPointCoord, maxIndex[i])));
                    endPosition.set(i, Math.max(0, Math.min(endPointCoord, maxIndex[i])));
                }
            }
            
            if(intersects)
            {
                double[] delta = {endPosition.x - startPosition.x, endPosition.y - startPosition.y, endPosition.z - startPosition.z};
                double greatestDimension = 0.0d;
                
                for(int i = 0; i < 3; ++i) greatestDimension = Math.max(Math.abs(delta[i]), greatestDimension);
                
                if(greatestDimension > 0)
                {
                    for(int i = 0; i < 3; ++i)
                        delta[i] /= greatestDimension;
                    
                    for(int i = 0; i <= greatestDimension; ++i)
                    {
                        int blockX = (int) Math.floor(delta[0] * i) + startPosition.x;
                        int blockY = (int) Math.floor(delta[1] * i) + startPosition.y;
                        int blockZ = (int) Math.floor(delta[2] * i) + startPosition.z;
                        
                        this.setSpot(blockX, blockY, blockZ, material);
                    }
                }
            }
        }
        
        return this;
    }
    
    /**
     * Returns an array of blocks.
     * The blocks are ordered this way:
     * (X: 0; Y: 0; Z: 0), (X: 0; Y: 0; Z: 1)... (X: 0; Y: 1; Z: 0), (X: 0; Y: 1; Z: 1)... (X: 1; Y: 0; Z: 0)...
     */
    public IAbstractBlock[] getAsBlockArray()
    {
        IAbstractBlock[] serializedVolume = new IAbstractBlock[this.blockMap.length];
        
        for(int i = 0, blockIndex = 0; i < this.sizeX; ++i)
            for(int j = 0; j < this.sizeY; ++j)
                for(int k = 0; k < this.sizeZ; ++k, ++blockIndex)
                    serializedVolume[blockIndex] = this.uncheckedGetSpot(i, j, k);
        
        return serializedVolume;
    }
    /**
     * Returns the dimensions of this block map.
     */
    public int[] getDimensions() { return new int[]{this.sizeX, this.sizeY, this.sizeZ}; }
}
