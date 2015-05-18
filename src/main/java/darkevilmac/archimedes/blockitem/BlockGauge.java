package darkevilmac.archimedes.blockitem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;


public class BlockGauge extends BlockContainer {
    public static int gaugeBlockRenderID;

    public BlockGauge() {
        super(Material.circuits);
        setBlockBounds(0F, 0F, 0F, 1F, 0.1F, 1F);
    }
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return gaugeBlockRenderID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, @SuppressWarnings("rawtypes") List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return (meta & 4) != 0 ? extendedIcon : blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconregister) {
        super.registerBlockIcons(iconregister);
        extendedIcon = iconregister.registerIcon(getTextureName() + "_ext");
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    //This will tell minecraft not to render any side of our cube.
    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemstack) {
        int meta = Math.round(entityliving.rotationYaw / 90F) & 3;
        if (itemstack.getItemDamage() == 1) {
            meta |= 4;
        }
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        TileEntityGauge tileentitygauge = new TileEntityGauge();
        return tileentitygauge;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, EnumFacing axis) {
        if (axis == EnumFacing.UP || axis == EnumFacing.DOWN) {
            boolean extended = (world.getBlockMetadata(x, y, z) & 4) != 0;
            int d = axis == EnumFacing.DOWN ? -1 : 1;
            if (!extended) {
                world.setBlockMetadataWithNotify(x, y, z, (world.getBlockMetadata(x, y, z) + d) & 3, 2);
            } else {
                world.setBlockMetadataWithNotify(x, y, z, (world.getBlockMetadata(x, y, z) + d) & 7, 2);
            }
        }
        return true;
        //return RotationHelper.rotateArchimedesBlock(this, world, x, y, z, axis); This is flawed, our meta is different.
    }
}
