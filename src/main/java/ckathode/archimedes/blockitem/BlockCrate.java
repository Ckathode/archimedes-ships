package ckathode.archimedes.blockitem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import ckathode.archimedes.entity.EntityShip;

public class BlockCrate extends BlockContainer
{
	public BlockCrate(Material material)
	{
		super(material);
		setBlockBounds(0F, 0F, 0F, 1F, 0.1F, 1F);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (!(entity instanceof EntityPlayer) && entity instanceof EntityLivingBase || (entity instanceof EntityBoat && !(entity instanceof EntityShip)) || entity instanceof EntityMinecart)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityCrate)
			{
				if (((TileEntityCrate) te).canCatchEntity() && ((TileEntityCrate) te).getContainedEntity() == null)
				{
					((TileEntityCrate) te).setContainedEntity(entity);
				}
			}
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityCrate();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int side, float dx, float dy, float dz)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrate)
		{
			((TileEntityCrate) te).releaseEntity();
		}
		return false;
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z))
		{
			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}
	}
}
