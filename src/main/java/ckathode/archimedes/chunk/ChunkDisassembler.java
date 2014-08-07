package ckathode.archimedes.chunk;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.blockitem.TileEntityHelm;
import ckathode.archimedes.entity.EntityShip;
import ckathode.archimedes.entity.IShipTileEntity;
import ckathode.archimedes.util.MathHelperMod;

public class ChunkDisassembler
{
	private EntityShip	ship;
	public boolean		overwrite;
	
	public ChunkDisassembler(EntityShip entityship)
	{
		ship = entityship;
		overwrite = false;
	}
	
	public boolean canDisassemble()
	{
		if (overwrite)
		{
			return true;
		}
		World world = ship.worldObj;
		MobileChunk chunk = ship.getShipChunk();
		float yaw = Math.round(ship.rotationYaw / 90F) * 90F;
		yaw = (float) Math.toRadians(ship.rotationYaw);
		
		float ox = -chunk.getCenterX();
		float oy = -chunk.minY(); //Created the normal way, through a VehicleFiller, this value will always be 0.
		float oz = -chunk.getCenterZ();
		
		Vec3 vec = Vec3.createVectorHelper(0D, 0D, 0D);
		Block block;
		int ix, iy, iz;
		for (int i = chunk.minX(); i < chunk.maxX(); i++)
		{
			for (int j = chunk.minY(); j < chunk.maxY(); j++)
			{
				for (int k = chunk.minZ(); k < chunk.maxZ(); k++)
				{
					if (chunk.isAirBlock(i, j, k)) continue;
					vec.xCoord = i + ox;
					vec.yCoord = j + oy;
					vec.zCoord = k + oz;
					vec.rotateAroundY(yaw);
					
					ix = MathHelperMod.round_double(vec.xCoord + ship.posX);
					iy = MathHelperMod.round_double(vec.yCoord + ship.posY);
					iz = MathHelperMod.round_double(vec.zCoord + ship.posZ);
					
					block = world.getBlock(ix, iy, iz);
					if (block != null && !block.isAir(world, ix, iy, iz) && !block.getMaterial().isLiquid() && !ArchimedesShipMod.instance.modConfig.overwritableBlocks.contains(block))
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public AssembleResult doDisassemble()
	{
		World world = ship.worldObj;
		MobileChunk chunk = ship.getShipChunk();
		AssembleResult result = new AssembleResult();
		result.xOffset = Integer.MAX_VALUE;
		result.yOffset = Integer.MAX_VALUE;
		result.zOffset = Integer.MAX_VALUE;
		
		int currentrot = Math.round(ship.rotationYaw / 90F) & 3;
		int deltarot = (-currentrot) & 3;
		ship.rotationYaw = currentrot * 90F;
		ship.rotationPitch = 0F;
		float yaw = currentrot * MathHelperMod.PI_HALF;
		
		boolean flag = world.getGameRules().getGameRuleBooleanValue("doTileDrops");
		world.getGameRules().setOrCreateGameRule("doTileDrops", "false");
		
		List<LocatedBlock> postlist = new ArrayList<LocatedBlock>(4);
		
		float ox = -chunk.getCenterX();
		float oy = -chunk.minY(); //Created the normal way, through a ChunkAssembler, this value will always be 0.
		float oz = -chunk.getCenterZ();
		
		Vec3 vec = Vec3.createVectorHelper(0D, 0D, 0D);
		TileEntity tileentity;
		Block block;
		int meta;
		int ix, iy, iz;
		for (int i = chunk.minX(); i < chunk.maxX(); i++)
		{
			for (int j = chunk.minY(); j < chunk.maxY(); j++)
			{
				for (int k = chunk.minZ(); k < chunk.maxZ(); k++)
				{
					block = chunk.getBlock(i, j, k);
					meta = chunk.getBlockMetadata(i, j, k);
					if (block == Blocks.air)
					{
						if (meta == 1) continue;
					} else if (block.isAir(world, i, j, k)) continue;
					tileentity = chunk.getTileEntity(i, j, k);
					
					meta = ArchimedesShipMod.instance.metaRotations.getRotatedMeta(block, meta, deltarot);
					
					vec.xCoord = i + ox;
					vec.yCoord = j + oy;
					vec.zCoord = k + oz;
					vec.rotateAroundY(yaw);
					
					ix = MathHelperMod.round_double(vec.xCoord + ship.posX);
					iy = MathHelperMod.round_double(vec.yCoord + ship.posY);
					iz = MathHelperMod.round_double(vec.zCoord + ship.posZ);
					
					if (!world.setBlock(ix, iy, iz, block, meta, 2) || block != world.getBlock(ix, iy, iz))
					{
						postlist.add(new LocatedBlock(block, meta, tileentity, new ChunkPosition(ix, iy, iz)));
						continue;
					}
					if (meta != world.getBlockMetadata(ix, iy, iz))
					{
						world.setBlockMetadataWithNotify(ix, iy, iz, meta, 2);
					}
					if (tileentity != null)
					{
						if (tileentity instanceof IShipTileEntity)
						{
							((IShipTileEntity) tileentity).setParentShip(null, i, j, k);
						}
						world.setTileEntity(ix, iy, iz, tileentity);
						tileentity.blockMetadata = meta;
					}
					
					if (!ArchimedesShipMod.instance.metaRotations.hasBlock(block))
					{
						//ShipMod.modLog.debug("Forge-rotating block " + Block.blockRegistry.getNameForObject(block));
						rotateBlock(block, world, ix, iy, iz, currentrot);
						block = world.getBlock(ix, iy, iz);
						meta = world.getBlockMetadata(ix, iy, iz);
						tileentity = world.getTileEntity(ix, iy, iz);
					}
					
					LocatedBlock lb = new LocatedBlock(block, meta, tileentity, new ChunkPosition(ix, iy, iz));
					result.assembleBlock(lb);
					if (block == ArchimedesShipMod.blockMarkShip && i == ship.seatX && j == ship.seatY && k == ship.seatZ)
					{
						result.shipMarkingBlock = lb;
					}
				}
			}
		}
		
		world.getGameRules().setOrCreateGameRule("doTileDrops", String.valueOf(flag));
		
		for (LocatedBlock ilb : postlist)
		{
			ix = ilb.coords.chunkPosX;
			iy = ilb.coords.chunkPosY;
			iz = ilb.coords.chunkPosZ;
			ArchimedesShipMod.modLog.debug("Post-rejoining block: " + ilb.toString());
			world.setBlock(ix, iy, iz, ilb.block, ilb.blockMeta, 0);
			result.assembleBlock(ilb);
		}
		
		ship.setDead();
		
		if (result.shipMarkingBlock == null || !(result.shipMarkingBlock.tileEntity instanceof TileEntityHelm))
		{
			result.resultCode = AssembleResult.RESULT_MISSING_MARKER;
		} else
		{
			result.checkConsistent(world);
		}
		return result;
	}
	
	private void rotateBlock(Block block, World world, int x, int y, int z, int deltarot)
	{
		deltarot &= 3;
		if (deltarot != 0)
		{
			if (deltarot == 3)
			{
				block.rotateBlock(world, x, y, z, ForgeDirection.UP);
			} else
			{
				for (int r = 0; r < deltarot; r++)
				{
					block.rotateBlock(world, x, y, z, ForgeDirection.DOWN);
				}
			}
		}
	}
}
