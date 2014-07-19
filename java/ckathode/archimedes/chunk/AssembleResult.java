package ckathode.archimedes.chunk;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.MaterialDensity;
import ckathode.archimedes.entity.EntityShip;

public class AssembleResult
{
	public static final int		RESULT_NONE		= 0, RESULT_OK = 1, RESULT_BLOCK_OVERFLOW = 2, RESULT_MISSING_MARKER = 3, RESULT_ERROR_OCCURED = 4,
			RESULT_BUSY_COMPILING = 5, RESULT_INCONSISTENT = 6, RESULT_OK_WITH_WARNINGS = 7;
	
	LocatedBlock				shipMarkingBlock;
	final List<LocatedBlock>	assembledBlocks	= new ArrayList<LocatedBlock>();
	int							resultCode;
	int							blockCount;
	int							balloonCount;
	int							tileEntityCount;
	float						mass;
	
	public int					xOffset, yOffset, zOffset;
	
	public AssembleResult(ByteBuf buf)
	{
		resultCode = buf.readByte();
		if (resultCode == RESULT_NONE) return;
		blockCount = buf.readInt();
		balloonCount = buf.readInt();
		tileEntityCount = buf.readInt();
		mass = buf.readFloat();
	}
	
	public AssembleResult(NBTTagCompound compound, World world)
	{
		resultCode = compound.getByte("res");
		blockCount = compound.getInteger("blockc");
		balloonCount = compound.getInteger("balloonc");
		tileEntityCount = compound.getInteger("tec");
		mass = compound.getFloat("mass");
		xOffset = compound.getInteger("xO");
		yOffset = compound.getInteger("yO");
		zOffset = compound.getInteger("zO");
		if (compound.hasKey("list"))
		{
			NBTTagList list = compound.getTagList("list", 10);
			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound comp = list.getCompoundTagAt(i);
				assembledBlocks.add(new LocatedBlock(comp, world));
			}
		}
		if (compound.hasKey("marker"))
		{
			NBTTagCompound comp = compound.getCompoundTag("marker");
			shipMarkingBlock = new LocatedBlock(comp, world);
		}
	}
	
	AssembleResult()
	{
		clear();
	}
	
	void assembleBlock(LocatedBlock lb)
	{
		assembledBlocks.add(lb);
		blockCount = assembledBlocks.size();
		if (lb.block == ArchimedesShipMod.blockBalloon)
		{
			balloonCount++;
		}
		if (lb.tileEntity != null)
		{
			tileEntityCount++;
		}
		mass += MaterialDensity.getDensity(lb.block);
		xOffset = Math.min(xOffset, lb.coords.chunkPosX);
		yOffset = Math.min(yOffset, lb.coords.chunkPosY);
		zOffset = Math.min(zOffset, lb.coords.chunkPosZ);
	}
	
	public void clear()
	{
		resultCode = RESULT_NONE;
		shipMarkingBlock = null;
		assembledBlocks.clear();
		blockCount = balloonCount = tileEntityCount = 0;
		xOffset = yOffset = zOffset = 0;
	}
	
	public EntityShip getEntity(World world)
	{
		if (!isOK()) return null;
		
		EntityShip entity = new EntityShip(world);
		entity.setPilotSeat(shipMarkingBlock.blockMeta & 3, shipMarkingBlock.coords.chunkPosX - xOffset, shipMarkingBlock.coords.chunkPosY - yOffset, shipMarkingBlock.coords.chunkPosZ - zOffset);
		entity.getShipChunk().setCreationSpotBiomeGen(world.getBiomeGenForCoords(shipMarkingBlock.coords.chunkPosX, shipMarkingBlock.coords.chunkPosZ));
		
		boolean flag = world.getGameRules().getGameRuleBooleanValue("doTileDrops");
		world.getGameRules().setOrCreateGameRule("doTileDrops", "false");
		
		try
		{
			TileEntity tileentity;
			int ix, iy, iz;
			for (LocatedBlock lb : assembledBlocks)
			{
				ix = lb.coords.chunkPosX - xOffset;
				iy = lb.coords.chunkPosY - yOffset;
				iz = lb.coords.chunkPosZ - zOffset;
				tileentity = lb.tileEntity;
				if (tileentity != null || lb.block.hasTileEntity(lb.blockMeta) && (tileentity = world.getTileEntity(lb.coords.chunkPosX, lb.coords.chunkPosY, lb.coords.chunkPosZ)) != null)
				{
					world.removeTileEntity(lb.coords.chunkPosX, lb.coords.chunkPosY, lb.coords.chunkPosZ);
					tileentity.validate();
					tileentity.updateContainingBlockInfo();
					tileentity.blockType = lb.block;
					tileentity.blockMetadata = lb.blockMeta;
				}
				if (entity.getShipChunk().setBlockIDWithMetadata(ix, iy, iz, lb.block, lb.blockMeta))
				{
					entity.getShipChunk().setTileEntity(ix, iy, iz, tileentity);
					world.setBlock(lb.coords.chunkPosX, lb.coords.chunkPosY, lb.coords.chunkPosZ, Blocks.air, 1, 2); //0b10
				}
			}
			for (LocatedBlock block : assembledBlocks)
			{
				world.setBlockToAir(block.coords.chunkPosX, block.coords.chunkPosY, block.coords.chunkPosZ);
			}
		} catch (Exception e)
		{
			resultCode = RESULT_ERROR_OCCURED;
			e.printStackTrace();
			return null;
		} finally
		{
			world.getGameRules().setOrCreateGameRule("doTileDrops", String.valueOf(flag));
		}
		
		entity.getShipChunk().setChunkModified();
		entity.getShipChunk().onChunkLoad();
		entity.setLocationAndAngles(xOffset + entity.getShipChunk().getCenterX(), yOffset, zOffset + entity.getShipChunk().getCenterZ(), 0F, 0F);
		
		return entity;
	}
	
	public int getCode()
	{
		return resultCode;
	}
	
	public boolean isOK()
	{
		return resultCode == RESULT_OK || resultCode == RESULT_OK_WITH_WARNINGS;
	}
	
	public LocatedBlock getShipMarker()
	{
		return shipMarkingBlock;
	}
	
	public int getBlockCount()
	{
		return blockCount;
	}
	
	public int getBalloonCount()
	{
		return balloonCount;
	}
	
	public int getTileEntityCount()
	{
		return tileEntityCount;
	}
	
	public float getMass()
	{
		return mass;
	}
	
	public void checkConsistent(World world)
	{
		boolean warn = false;
		for (LocatedBlock lb : assembledBlocks)
		{
			Block block = world.getBlock(lb.coords.chunkPosX, lb.coords.chunkPosY, lb.coords.chunkPosZ);
			if (block != lb.block)
			{
				resultCode = RESULT_INCONSISTENT;
				return;
			}
			int meta = world.getBlockMetadata(lb.coords.chunkPosX, lb.coords.chunkPosY, lb.coords.chunkPosZ);
			if (meta != lb.blockMeta)
			{
				warn = true;
			}
		}
		resultCode = warn ? RESULT_OK_WITH_WARNINGS : RESULT_OK;
	}
	
	public void writeNBTFully(NBTTagCompound compound)
	{
		writeNBTMetadata(compound);
		NBTTagList list = new NBTTagList();
		for (LocatedBlock lb : assembledBlocks)
		{
			NBTTagCompound comp = new NBTTagCompound();
			lb.writeToNBT(comp);
			list.appendTag(comp);
		}
		compound.setTag("list", list);
		
		if (shipMarkingBlock != null)
		{
			NBTTagCompound comp = new NBTTagCompound();
			shipMarkingBlock.writeToNBT(comp);
			compound.setTag("marker", comp);
		}
	}
	
	public void writeNBTMetadata(NBTTagCompound compound)
	{
		compound.setByte("res", (byte) getCode());
		compound.setInteger("blockc", getBlockCount());
		compound.setInteger("balloonc", getBalloonCount());
		compound.setInteger("tec", getTileEntityCount());
		compound.setFloat("mass", getMass());
		compound.setInteger("xO", xOffset);
		compound.setInteger("yO", yOffset);
		compound.setInteger("zO", zOffset);
	}
}
