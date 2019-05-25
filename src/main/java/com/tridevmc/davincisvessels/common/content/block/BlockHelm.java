package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockHelm extends BlockDirectional implements ITileEntityProvider {

    public static final BooleanProperty IS_WHEEL = BooleanProperty.create("wheel");

    public  BlockHelm(Properties properties) {
        super(properties.sound(SoundType.WOOD));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH).with(IS_WHEEL, false));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        VoxelShape selectedShape = super.getRenderShape(state, worldIn, pos);
        if (state == null || state.get(FACING) == null)
            return super.getRenderShape(state, worldIn, pos);

        double pixelSize = 1D / 16D;
        EnumFacing facing = state.get(FACING);
        switch (facing) {
            case NORTH: {
                selectedShape = VoxelShapes.create(1 - (pixelSize * 1), 1, 1 - (pixelSize * 3), pixelSize * 1, 0, pixelSize * 2);
                selectedShape = selectedShape.withOffset(pos.getX(), pos.getY(), pos.getZ());

                return selectedShape;
            }
            case SOUTH: {
                selectedShape = VoxelShapes.create(1 - (pixelSize * 1), 1, 1 - (pixelSize * 2), pixelSize * 1, 0, pixelSize * 3);
                selectedShape = selectedShape.withOffset(pos.getX(), pos.getY(), pos.getZ());

                return selectedShape;
            }
            case WEST: {
                selectedShape = VoxelShapes.create(pixelSize * 2, 0, pixelSize * 1, 1 - (pixelSize * 3), 1, 1 - (pixelSize * 1));
                selectedShape = selectedShape.withOffset(pos.getX(), pos.getY(), pos.getZ());

                return selectedShape;
            }
            case EAST: {
                selectedShape = VoxelShapes.create(1 - (pixelSize * 2), 1, 1 - (pixelSize * 1), pixelSize * 3, 0, pixelSize * 1);
                selectedShape = selectedShape.withOffset(pos.getX(), pos.getY(), pos.getZ());

                return selectedShape;
            }
            default: {
                return selectedShape;
            }
        }
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            TileHelm helm = world.getTileEntity(pos) instanceof TileHelm ? (TileHelm) world.getTileEntity(pos) : null;
            if (helm != null && player instanceof EntityPlayerMP) {
                NetworkHooks.openGui((EntityPlayerMP) player, helm, pos);
                return true;
            }
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileHelm();
    }

    @Override
    public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving) {
        super.onReplaced(state, world, pos, newState, isMoving);
        world.removeTileEntity(pos);
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int eventID, int eventParam) {
        super.eventReceived(state, world, pos, eventID, eventParam);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(eventID, eventParam);
    }

    @Override
    public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            IBlockState block = worldIn.getBlockState(pos.north());
            IBlockState block1 = worldIn.getBlockState(pos.south());
            IBlockState block2 = worldIn.getBlockState(pos.west());
            IBlockState block3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = state.get(FACING);

            if (enumfacing == EnumFacing.NORTH && block.isFullCube() && !block1.isFullCube()) {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && block1.isFullCube() && !block.isFullCube()) {
                enumfacing = EnumFacing.NORTH;
            } else if (enumfacing == EnumFacing.WEST && block2.isFullCube() && !block3.isFullCube()) {
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && block3.isFullCube() && !block2.isFullCube()) {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.with(FACING, enumfacing), 2);
        }
    }


    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        if (context.getPlayer() != null && !(context.getPlayer() instanceof FakePlayer)) {
            // TODO: Achievements are gone.
            //((EntityPlayer) placer).addStat(DavincisVesselsContent.achievementCreateHelm);
        }

        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING, IS_WHEEL);
    }


}
