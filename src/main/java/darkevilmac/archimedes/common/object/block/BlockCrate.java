package darkevilmac.archimedes.common.object.block;

import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.tileentity.TileEntityCrate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockCrate extends BlockContainer {
    public static final PropertyEnum AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);


    public BlockCrate(Material material) {
        super(material);
        setBlockBounds(0F, 0F, 0F, 1F, 0.1F, 1F);
    }

    public static int getMetaForAxis(EnumFacing.Axis axis) {
        return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.SOLID;
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
    protected BlockState createBlockState() {
        return new BlockState(this, AXIS);
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
    public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
        if (!(entity instanceof EntityPlayer) && entity instanceof EntityLivingBase || (entity instanceof EntityBoat && !(entity instanceof EntityShip)) || entity instanceof EntityMinecart) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityCrate) {
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrate) {
            ((TileEntityCrate) te).releaseEntity();
        }
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(world, pos.add(0, -1, 0));
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!World.doesBlockHaveSolidTopSurface(world, pos.add(0, -1, 0))) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);

        System.out.println("onFall");

        if (worldIn != null && !worldIn.isRemote) {
            if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityCrate
                    && entityIn != null && !(entityIn instanceof EntityPlayer)) {
                if (((TileEntityCrate) worldIn.getTileEntity(pos)).getContainedEntity() != null) {
                    ((TileEntityCrate) worldIn.getTileEntity(pos)).setContainedEntity(entityIn);
                }
            }
        }

    }
}
