package darkevilmac.archimedes.common.object.block;

import darkevilmac.archimedes.common.tileentity.TileEntityCrate;
import darkevilmac.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFence;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
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

public class BlockCrate extends BlockContainer {
    public static final PropertyEnum AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockCrate(Material material) {
        super(material);
        this.setSoundType(SoundType.WOOD);
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(AXIS, EnumFacing.Axis.X).withProperty(POWERED, false));
    }

    public static int getMetaForAxis(EnumFacing.Axis axis) {
        return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0F, 0F, 0F, 1F, 0.1F, 1F);
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
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean powered = false;
        if (meta > 2) {
            powered = true;
            meta /= 2;
        }

        return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X).withProperty(POWERED, powered);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return getMetaForAxis((EnumFacing.Axis) state.getValue(AXIS)) * (state.getValue(POWERED) ? 2 : 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, POWERED);
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
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote || state.getValue(POWERED))
            return;

        if (entity != null && !(entity instanceof EntityPlayer || entity instanceof EntityMovingWorld)) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileEntityCrate) {
                if (((TileEntityCrate) te).canCatchEntity() && ((TileEntityCrate) te).getContainedEntity() == null) {
                    ((TileEntityCrate) te).setContainedEntity(entity);
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCrate();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityCrate) {
            ((TileEntityCrate) te).releaseEntity();
            return true;
        }
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return this.canBePlacedOn(worldIn, pos.down());
    }

    private boolean canBePlacedOn(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).isSideSolid(worldIn, pos, EnumFacing.UP) || worldIn.getBlockState(pos).getBlock() instanceof BlockFence;
    }


    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (world.isRemote)
            return;

        if (!canBePlacedOn(world, pos.down())) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }

        boolean powered = world.isBlockPowered(pos) || world.isBlockPowered(pos.up());

        if (powered) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileEntityCrate) {
                ((TileEntityCrate) te).releaseEntity();
                world.setBlockState(pos, world.getBlockState(pos).withProperty(POWERED, Boolean.TRUE));
            }
        } else {
            world.setBlockState(pos, world.getBlockState(pos).withProperty(POWERED, Boolean.FALSE));
        }
    }
}
