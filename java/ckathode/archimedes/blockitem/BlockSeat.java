package ckathode.archimedes.blockitem;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import ckathode.archimedes.util.RotationHelper;

public class BlockSeat extends Block/*Container*/
{
	public static int	seatBlockRenderID	= 0;
	
	public BlockSeat()
	{
		super(Material.wood);
		setBlockBounds(0F, 0F, 0F, 1F, 0.4F, 1F);
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return seatBlockRenderID;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemstack)
	{
		int dir = Math.round(entityliving.rotationYaw / 90F) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 3);
	}
	
	@Override
	public IIcon getIcon(int side, int meta)
	{
		if (side == 0) return Blocks.planks.getIcon(side, 0);
		switch (meta)
		{
		case 0:
			return side == 3 || side == 4 || side == 5 ? Blocks.planks.getIcon(side, 0) : blockIcon;
		case 1:
			return side == 2 || side == 3 || side == 4 ? Blocks.planks.getIcon(side, 0) : blockIcon;
		case 2:
			return side == 5 || side == 4 || side == 2 ? Blocks.planks.getIcon(side, 0) : blockIcon;
		case 3:
			return side == 3 || side == 2 || side == 5 ? Blocks.planks.getIcon(side, 0) : blockIcon;
		default:
			return blockIcon;
		}
	}
	
	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis)
	{
		return RotationHelper.rotateArchimedesBlock(this, world, x, y, z, axis);
	}
	/*
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrate)
		{
			if (((TileEntityCrate) te).getContainedEntity() == player)
			{
				((TileEntityCrate) te).setContainedEntity(null);
			} else if (((TileEntityCrate) te).getContainedEntity() == null)
			{
				((TileEntityCrate) te).setContainedEntity(player);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int var2)
	{
		return new TileEntityCrate();
	}*/
}
