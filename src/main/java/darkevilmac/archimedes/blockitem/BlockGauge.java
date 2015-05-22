package darkevilmac.archimedes.blockitem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

import java.util.List;

public class BlockGauge extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool EXTENDED = PropertyBool.create("extended");

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

    /**
     * No hitbox for the gauge, it's not needed considering the size.
     *
     * @param worldIn
     * @param pos
     * @param state
     * @return
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(world, pos.subtract(new Vec3i(0, 1, 0)));
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
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
    }
}
