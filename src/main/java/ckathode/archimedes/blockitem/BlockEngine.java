package ckathode.archimedes.blockitem;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.util.RotationHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEngine extends BlockContainer
{
	public float	enginePower;
	public int		engineFuelConsumption;
	
	private IIcon	frontIcon, backIcon;
	
	public BlockEngine(Material material, float power, int fuelconsumption)
	{
		super(material);
		enginePower = power;
		engineFuelConsumption = fuelconsumption;
	}
	
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		meta &= 3;
		switch (side)
		{
		case 0:
		case 1:
		case 4:
			return backIcon;
		case 3:
		case 2:
			return blockIcon;
		case 5:
			return frontIcon;
			
		}
		return blockIcon;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister reg)
	{
		super.registerBlockIcons(reg);
		frontIcon = reg.registerIcon(getTextureName() + "_front");
		backIcon = reg.registerIcon(getTextureName() + "_back");
	}
	*/
	
	// ------------------------------
	// Disable rendering of the block
	@Override
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
    {
        return false;
    }
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    // ------------------------------

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemstack)
	{
		int dir = Math.round(entityliving.rotationYaw / 90F) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 3);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TileEntityEngine(enginePower, engineFuelConsumption);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are)
	{
		if (!player.isSneaking())
		{
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity != null)
			{
				player.openGui(ArchimedesShipMod.instance, 3, world, x, y, z);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis)
	{
		return RotationHelper.rotateArchimedesBlock(this, world, x, y, z, axis);
	}
}
