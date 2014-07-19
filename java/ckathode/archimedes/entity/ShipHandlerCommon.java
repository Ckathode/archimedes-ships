package ckathode.archimedes.entity;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.chunk.MobileChunk;

public abstract class ShipHandlerCommon
{
	public final EntityShip	ship;
	
	public ShipHandlerCommon(EntityShip entityship)
	{
		ship = entityship;
	}
	
	public boolean interact(EntityPlayer player)
	{
		return false;
	}
	
	public void onChunkUpdate()
	{
		MobileChunk chunk = ship.getShipChunk();
		ship.getCapabilities().clearBlockCount();
		for (int i = chunk.minX(); i < chunk.maxX(); i++)
		{
			for (int j = chunk.minY(); j < chunk.maxY(); j++)
			{
				for (int k = chunk.minZ(); k < chunk.maxZ(); k++)
				{
					Block block = chunk.getBlock(i, j, k);
					if (block.getMaterial() != Material.air)
					{
						ship.getCapabilities().onChunkBlockAdded(block, chunk.getBlockMetadata(i, j, k), i, j, k);
					}
				}
			}
		}
		
		ship.setSize(Math.max(chunk.maxX() - chunk.minX(), chunk.maxZ() - chunk.minZ()), chunk.maxY() - chunk.minY());
		World.MAX_ENTITY_RADIUS = Math.max(World.MAX_ENTITY_RADIUS, Math.max(ship.width, ship.height) + 2F);
		
		try
		{
			ship.fillAirBlocks(new HashSet<ChunkPosition>(), -1, -1, -1);
		} catch (StackOverflowError e)
		{
			ArchimedesShipMod.modLog.error("Failure during ship post-initialization", e);
		}
		
		ship.layeredBlockVolumeCount = new int[chunk.maxY() - chunk.minY()];
		for (int y = 0; y < ship.layeredBlockVolumeCount.length; y++)
		{
			for (int i = chunk.minX(); i < chunk.maxX(); i++)
			{
				for (int j = chunk.minZ(); j < chunk.maxZ(); j++)
				{
					if (chunk.isBlockTakingWaterVolume(i, y + chunk.minY(), j))
					{
						ship.layeredBlockVolumeCount[y]++;
					}
				}
			}
		}
		ship.isFlying = ship.getCapabilities().canFly();
	}
}
