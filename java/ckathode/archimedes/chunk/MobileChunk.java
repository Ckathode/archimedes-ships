package ckathode.archimedes.chunk;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.ForgeDirection;
import ckathode.archimedes.entity.EntityShip;
import ckathode.archimedes.entity.IShipTileEntity;

public class MobileChunk implements IBlockAccess
{
	public static final int								CHUNK_SIZE			= 16;
	public static final int								CHUNK_SIZE_EXP		= 4;
	public static final int								CHUNK_MEMORY_USING	= CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * (4 + 2);	//(16*16*16 shorts and ints)
																																
	protected final World								worldObj;
	protected final EntityShip							entityShip;
	private Map<ChunkPosition, ExtendedBlockStorage>	blockStorageMap;
	
	public Map<ChunkPosition, TileEntity>				chunkTileEntityMap;
	
	private boolean										boundsInit;
	private int											minX, minY, minZ, maxX, maxY, maxZ;
	private int											blockCount;
	
	public boolean										isChunkLoaded;
	public boolean										isModified;
	
	private BiomeGenBase								creationSpotBiome;
	
	public MobileChunk(World world, EntityShip entityship)
	{
		worldObj = world;
		entityShip = entityship;
		blockStorageMap = new HashMap<ChunkPosition, ExtendedBlockStorage>(1);
		chunkTileEntityMap = new HashMap<ChunkPosition, TileEntity>(2);
		
		isChunkLoaded = false;
		isModified = false;
		
		boundsInit = false;
		minX = minY = minZ = maxX = maxY = maxZ = -1;
		blockCount = 0;
		
		creationSpotBiome = BiomeGenBase.ocean;
	}
	
	public ExtendedBlockStorage getBlockStorage(int x, int y, int z)
	{
		ChunkPosition pos = new ChunkPosition(x >> CHUNK_SIZE_EXP, y >> CHUNK_SIZE_EXP, z >> CHUNK_SIZE_EXP);
		return blockStorageMap.get(pos);
	}
	
	public ExtendedBlockStorage getBlockStorageOrCreate(int x, int y, int z)
	{
		ChunkPosition pos = new ChunkPosition(x >> CHUNK_SIZE_EXP, y >> CHUNK_SIZE_EXP, z >> CHUNK_SIZE_EXP);
		ExtendedBlockStorage storage = blockStorageMap.get(pos);
		if (storage != null) return storage;
		storage = new ExtendedBlockStorage(pos.chunkPosY, false);
		blockStorageMap.put(pos, storage);
		return storage;
	}
	
	public int getBlockCount()
	{
		return blockCount;
	}
	
	public float getCenterX()
	{
		return (minX + maxX) / 2F;
	}
	
	public float getCenterY()
	{
		return (minY + maxY) / 2F;
	}
	
	public float getCenterZ()
	{
		return (minZ + maxZ) / 2F;
	}
	
	public int minX()
	{
		return minX;
	}
	
	public int maxX()
	{
		return maxX;
	}
	
	public int minY()
	{
		return minY;
	}
	
	public int maxY()
	{
		return maxY;
	}
	
	public int minZ()
	{
		return minZ;
	}
	
	public int maxZ()
	{
		return maxZ;
	}
	
	public void setCreationSpotBiomeGen(BiomeGenBase biomegenbase)
	{
		creationSpotBiome = biomegenbase;
	}
	
	@Override
	public Block getBlock(int x, int y, int z)
	{
		ExtendedBlockStorage storage = getBlockStorage(x, y, z);
		if (storage == null) return Blocks.air;
		return storage.getBlockByExtId(x & 15, y & 15, z & 15);
	}
	
	/**
	 * Return the metadata corresponding to the given coordinates inside a chunk.
	 */
	@Override
	public int getBlockMetadata(int x, int y, int z)
	{
		ExtendedBlockStorage storage = getBlockStorage(x, y, z);
		if (storage == null) return 0;
		return storage.getExtBlockMetadata(x & 15, y & 15, z & 15);
	}
	
	public boolean setBlockIDWithMetadata(int x, int y, int z, Block block, int meta)
	{
		if (block == null) return false;
		
		ExtendedBlockStorage storage = getBlockStorageOrCreate(x, y, z);
		int i = x & 15;
		int j = y & 15;
		int k = z & 15;
		
		Block currentblock = storage.getBlockByExtId(i, j, k);
		int currentmeta = storage.getExtBlockMetadata(i, j, k);
		if (currentblock == block && currentmeta == meta)
		{
			return false;
		}
		
		storage.func_150818_a(i, j, k, block);
		storage.setExtBlockMetadata(i, j, k, meta);
		
		if (boundsInit)
		{
			minX = Math.min(minX, x);
			minY = Math.min(minY, y);
			minZ = Math.min(minZ, z);
			maxX = Math.max(maxX, x + 1);
			maxY = Math.max(maxY, y + 1);
			maxZ = Math.max(maxZ, z + 1);
		} else
		{
			boundsInit = true;
			minX = x;
			minY = y;
			minZ = z;
			maxX = x + 1;
			maxY = y + 1;
			maxZ = z + 1;
		}
		blockCount++;
		setChunkModified();
		
		TileEntity tileentity;
		if (block.hasTileEntity(meta))
		{
			tileentity = getTileEntity(x, y, z);
			
			if (tileentity == null)
			{
				tileentity = block.createTileEntity(worldObj, meta);
				setTileEntity(x, y, z, tileentity);
			}
			
			if (tileentity != null)
			{
				tileentity.updateContainingBlockInfo();
				tileentity.blockType = block;
				tileentity.blockMetadata = meta;
			}
		}
		
		return true;
	}
	
	public boolean setBlockMetadata(int x, int y, int z, int meta)
	{
		ExtendedBlockStorage storage = getBlockStorage(x, y, z);
		if (storage == null) return false;
		
		int currentmeta = storage.getExtBlockMetadata(x, y & 15, z);
		if (currentmeta == meta)
		{
			return false;
		}
		
		setChunkModified();
		storage.setExtBlockMetadata(x & 15, y & 15, z & 15, meta);
		Block block = storage.getBlockByExtId(x & 15, y & 15, z & 15);
		
		if (block != null && block.hasTileEntity(meta))
		{
			TileEntity tileentity = getTileEntity(x, y, z);
			
			if (tileentity != null)
			{
				tileentity.updateContainingBlockInfo();
				tileentity.blockMetadata = meta;
			}
		}
		
		return true;
	}
	
	public boolean setBlockAsFilledAir(int x, int y, int z)
	{
		ExtendedBlockStorage storage = getBlockStorage(x, y, z);
		if (storage == null) return true;
		
		Block block = storage.getBlockByExtId(x & 15, y & 15, z & 15);
		int meta = storage.getExtBlockMetadata(x & 15, y & 15, z & 15);
		if (block == Blocks.air && meta == 1)
		{
			return true;
		}
		if (block == null || block.isAir(worldObj, x, y, z))
		{
			storage.func_150818_a(x & 15, y & 15, z & 15, Blocks.air);
			storage.setExtBlockMetadata(x & 15, y & 15, z & 15, 1);
			onSetBlockAsFilledAir(x, y, z);
			return true;
		}
		return false;
	}
	
	protected void onSetBlockAsFilledAir(int x, int y, int z)
	{
	}
	
	/**
	 * Gets the TileEntity for a given block in this chunk
	 */
	@Override
	public TileEntity getTileEntity(int x, int y, int z)
	{
		ChunkPosition chunkposition = new ChunkPosition(x, y, z);
		TileEntity tileentity = chunkTileEntityMap.get(chunkposition);
		
		if (tileentity != null && tileentity.isInvalid())
		{
			chunkTileEntityMap.remove(chunkposition);
			tileentity = null;
		}
		
		if (tileentity == null)
		{
			Block block = getBlock(x, y, z);
			int meta = getBlockMetadata(x, y, z);
			
			if (block == null || !block.hasTileEntity(meta))
			{
				return null;
			}
			
			tileentity = block.createTileEntity(worldObj, meta);
			setTileEntity(x, y, z, tileentity);
			
			tileentity = chunkTileEntityMap.get(chunkposition);
		}
		
		return tileentity;
	}
	
	public void setTileEntity(int x, int y, int z, TileEntity tileentity)
	{
		if (tileentity == null || tileentity.isInvalid())
		{
			return;
		}
		
		setChunkBlockTileEntity(x, y, z, tileentity);
	}
	
	/**
	 * Sets the TileEntity for a given block in this chunk
	 */
	private void setChunkBlockTileEntity(int x, int y, int z, TileEntity tileentity)
	{
		ChunkPosition chunkposition = new ChunkPosition(x, y, z);
		tileentity.setWorldObj(worldObj);
		int ox = tileentity.xCoord;
		int oy = tileentity.yCoord;
		int oz = tileentity.zCoord;
		tileentity.xCoord = x;
		tileentity.yCoord = y;
		tileentity.zCoord = z;
		
		Block block = getBlock(x, y, z);
		if (block != null && block.hasTileEntity(getBlockMetadata(x, y, z)))
		{
			if (chunkTileEntityMap.containsKey(chunkposition))
			{
				chunkTileEntityMap.get(chunkposition).invalidate();
			}
			
			tileentity.blockMetadata = getBlockMetadata(x, y, z);
			tileentity.validate();
			chunkTileEntityMap.put(chunkposition, tileentity);
			
			if (tileentity instanceof IShipTileEntity)
			{
				((IShipTileEntity) tileentity).setParentShip(entityShip, ox, oy, oz);
			}
			
			if (isChunkLoaded)
			{
				worldObj.addTileEntity(tileentity);
			}
		}
	}
	
	/**
	 * Adds a TileEntity to a chunk
	 */
	public void addTileEntity(TileEntity tileentity)
	{
		setChunkBlockTileEntity(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord, tileentity);
	}
	
	/**
	 * Removes the TileEntity for a given block in this chunk
	 */
	public void removeChunkBlockTileEntity(int x, int y, int z)
	{
		ChunkPosition chunkposition = new ChunkPosition(x, y, z);
		if (isChunkLoaded)
		{
			TileEntity tileentity = chunkTileEntityMap.remove(chunkposition);
			if (tileentity != null)
			{
				if (tileentity instanceof IShipTileEntity)
				{
					((IShipTileEntity) tileentity).setParentShip(null, x, y, z);
				}
				tileentity.invalidate();
			}
		}
	}
	
	/**
	 * Called when this Chunk is loaded by the ChunkProvider
	 */
	public void onChunkLoad()
	{
		isChunkLoaded = true;
		worldObj.func_147448_a(chunkTileEntityMap.values());
	}
	
	/**
	 * Called when this Chunk is unloaded by the ChunkProvider
	 */
	public void onChunkUnload()
	{
		isChunkLoaded = false;
	}
	
	public void setChunkModified()
	{
		isModified = true;
	}
	
	@Override
	public int getLightBrightnessForSkyBlocks(int i, int j, int k, int l)
	{
		int lv = EnumSkyBlock.Sky.defaultLightValue;
		return lv << 20 | l << 4;
	}
	
	@Override
	public boolean isAirBlock(int x, int y, int z)
	{
		Block block = getBlock(x, y, z);
		return block == null || block.isAir(worldObj, x, y, z);
	}
	
	public boolean isBlockTakingWaterVolume(int x, int y, int z)
	{
		Block block = getBlock(x, y, z);
		if (block == null || block.isAir(worldObj, x, y, z))
		{
			if (getBlockMetadata(x, y, z) == 1) return false;
		}
		return true;
	}
	
	@Override
	public BiomeGenBase getBiomeGenForCoords(int i, int j)
	{
		return creationSpotBiome;
	}
	
	@Override
	public int getHeight()
	{
		return CHUNK_SIZE;
	}
	
	@Override
	public boolean extendedLevelsInChunkCache()
	{
		return false;
	}
	
	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean defaultvalue)
	{
		if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000)
		{
			return defaultvalue;
		}
		
		Block block = getBlock(x, y, z);
		return block.isSideSolid(worldObj, x, y, z, side);
	}
	
	@Override
	public int isBlockProvidingPowerTo(int i, int j, int k, int l)
	{
		return 0;
	}
	
	public final int getMemoryUsage()
	{
		return 2 + blockCount * 9; // (3 bytes + 2 bytes (short) + 4 bytes (int) = 9 bytes per block) + 2 bytes (short)
	}
	
}
