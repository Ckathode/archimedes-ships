package darkevilmac.archimedes.common.object.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.tileentity.TileEntityAnchorPoint;

public class BlockAnchorPoint extends BlockContainer {

    public static final PropertyEnum AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);

    public BlockAnchorPoint(Material material) {
        super(material);
        this.setSoundType(SoundType.METAL);
    }

    public static int getMetaForAxis(EnumFacing.Axis axis) {
        return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing.Axis axis = (EnumFacing.Axis) state.getValue(AXIS);
        float f = 0.125F;
        float f1 = 0.125F;

        if (axis == EnumFacing.Axis.Z) {
            f = 0.5F;
        }

        if (axis == EnumFacing.Axis.X) {
            f1 = 0.5F;
        }

        return new AxisAlignedBB(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);
    }


    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return getMetaForAxis((EnumFacing.Axis) state.getValue(AXIS));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{AXIS});
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(AXIS, placer.getHorizontalFacing().getAxis());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(AXIS, placer.getHorizontalFacing().getAxis()), 2);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!playerIn.isSneaking()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity != null) {
                playerIn.openGui(ArchimedesShipMod.instance, 4, worldIn, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAnchorPoint();
    }
}
