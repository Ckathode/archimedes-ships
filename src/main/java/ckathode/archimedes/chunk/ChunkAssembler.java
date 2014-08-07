package ckathode.archimedes.chunk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import ckathode.archimedes.ArchimedesShipMod;

public class ChunkAssembler
{
	private World	worldObj;
	public final int	startX, startY, startZ;
	
	private final int	maxBlocks;
	
	public ChunkAssembler(World world, int x, int y, int z)
	{
		worldObj = world;
		
		startX = x;
		startY = y;
		startZ = z;
		
		maxBlocks = ArchimedesShipMod.instance.modConfig.maxShipChunkBlocks;
	}
	
	public AssembleResult doAssemble()
	{
		AssembleResult result = new AssembleResult();
		result.xOffset = startX;
		result.yOffset = startY;
		result.zOffset = startZ;
		try
		{
			if (ArchimedesShipMod.instance.modConfig.useNewAlgorithm)
			{
				assembleIterative(result, startX, startY, startZ);
			} else
			{
				assembleRecursive(result, new HashSet<ChunkPosition>(), startX, startY, startZ);
			}
			if (result.shipMarkingBlock == null)
			{
				result.resultCode = AssembleResult.RESULT_MISSING_MARKER;
			} else
			{
				result.resultCode = AssembleResult.RESULT_OK;
			}
		} catch (ShipSizeOverflowException e)
		{
			result.resultCode = AssembleResult.RESULT_BLOCK_OVERFLOW;
		} catch (Error e)
		{
			ArchimedesShipMod.modLog.error("Error while compiling ship", e);
			result.resultCode = AssembleResult.RESULT_ERROR_OCCURED;
		}
		return result;
	}
	
	private void assembleIterative(AssembleResult result, int sx, int sy, int sz) throws ShipSizeOverflowException
	{
		HashSet<ChunkPosition> openset = new HashSet<ChunkPosition>();
		HashSet<ChunkPosition> closedset = new HashSet<ChunkPosition>();
		List<ChunkPosition> iterator = new ArrayList<ChunkPosition>();
		
		int x = sx, y = sy, z = sz;
		
		openset.add(new ChunkPosition(sx, sy, sz));
		while (!openset.isEmpty())
		{
			iterator.addAll(openset);
			for (ChunkPosition pos : iterator)
			{
				openset.remove(pos);
				
				if (closedset.contains(pos))
				{
					continue;
				}
				if (result.assembledBlocks.size() > maxBlocks)
				{
					throw new ShipSizeOverflowException();
				}
				
				x = pos.chunkPosX;
				y = pos.chunkPosY;
				z = pos.chunkPosZ;
				
				closedset.add(pos);
				
				Block block = worldObj.getBlock(x, y, z);
				if (!canUseBlockForVehicle(block, x, y, z))
				{
					continue;
				}
				
				LocatedBlock lb = new LocatedBlock(block, worldObj.getBlockMetadata(x, y, z), worldObj.getTileEntity(x, y, z), pos);
				result.assembleBlock(lb);
				if (block == ArchimedesShipMod.blockMarkShip && result.shipMarkingBlock == null)
				{
					result.shipMarkingBlock = lb;
				}
				
				openset.add(new ChunkPosition(x - 1, y, z));
				openset.add(new ChunkPosition(x, y - 1, z));
				openset.add(new ChunkPosition(x, y, z - 1));
				openset.add(new ChunkPosition(x + 1, y, z));
				openset.add(new ChunkPosition(x, y + 1, z));
				openset.add(new ChunkPosition(x, y, z + 1));
				
				if (ArchimedesShipMod.instance.modConfig.connectDiagonalBlocks1)
				{
					openset.add(new ChunkPosition(x - 1, y - 1, z));
					openset.add(new ChunkPosition(x + 1, y - 1, z));
					openset.add(new ChunkPosition(x + 1, y + 1, z));
					openset.add(new ChunkPosition(x - 1, y + 1, z));
					
					openset.add(new ChunkPosition(x - 1, y, z - 1));
					openset.add(new ChunkPosition(x + 1, y, z - 1));
					openset.add(new ChunkPosition(x + 1, y, z + 1));
					openset.add(new ChunkPosition(x - 1, y, z + 1));
					
					openset.add(new ChunkPosition(x, y - 1, z - 1));
					openset.add(new ChunkPosition(x, y + 1, z - 1));
					openset.add(new ChunkPosition(x, y + 1, z + 1));
					openset.add(new ChunkPosition(x, y - 1, z + 1));
				}
			}
		}
	}
	
	private void assembleRecursive(AssembleResult result, HashSet<ChunkPosition> set, int x, int y, int z) throws ShipSizeOverflowException
	{
		if (result.assembledBlocks.size() > maxBlocks)
		{
			throw new ShipSizeOverflowException();
		}
		
		ChunkPosition pos = new ChunkPosition(x, y, z);
		if (set.contains(pos)) return;
		
		set.add(pos);
		Block block = worldObj.getBlock(x, y, z);
		if (!canUseBlockForVehicle(block, x, y, z)) return;
		
		LocatedBlock lb = new LocatedBlock(block, worldObj.getBlockMetadata(x, y, z), worldObj.getTileEntity(x, y, z), pos);
		result.assembleBlock(lb);
		if (block == ArchimedesShipMod.blockMarkShip && result.shipMarkingBlock == null)
		{
			result.shipMarkingBlock = lb;
		}
		
		assembleRecursive(result, set, x - 1, y, z);
		assembleRecursive(result, set, x, y - 1, z);
		assembleRecursive(result, set, x, y, z - 1);
		assembleRecursive(result, set, x + 1, y, z);
		assembleRecursive(result, set, x, y + 1, z);
		assembleRecursive(result, set, x, y, z + 1);
		
		if (ArchimedesShipMod.instance.modConfig.connectDiagonalBlocks1)
		{
			assembleRecursive(result, set, x - 1, y - 1, z);
			assembleRecursive(result, set, x + 1, y - 1, z);
			assembleRecursive(result, set, x + 1, y + 1, z);
			assembleRecursive(result, set, x - 1, y + 1, z);
			
			assembleRecursive(result, set, x - 1, y, z - 1);
			assembleRecursive(result, set, x + 1, y, z - 1);
			assembleRecursive(result, set, x + 1, y, z + 1);
			assembleRecursive(result, set, x - 1, y, z + 1);
			
			assembleRecursive(result, set, x, y - 1, z - 1);
			assembleRecursive(result, set, x, y + 1, z - 1);
			assembleRecursive(result, set, x, y + 1, z + 1);
			assembleRecursive(result, set, x, y - 1, z + 1);
		}
	}
	
	public boolean canUseBlockForVehicle(Block block, int x, int y, int z)
	{
		return !block.isAir(worldObj, x, y, z) && !block.getMaterial().isLiquid() && block != ArchimedesShipMod.blockBuffer && ArchimedesShipMod.instance.modConfig.isBlockAllowed(block);
	}
}
