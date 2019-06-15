package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.common.DavincisUIHooks;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;

public class BlockHelm extends DirectionalBlock implements ITileEntityProvider {

    public static final BooleanProperty IS_WHEEL = BooleanProperty.create("wheel");

    public BlockHelm(Properties properties) {
        super(properties.sound(SoundType.WOOD));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(IS_WHEEL, false));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape selectedShape = super.getShape(state, worldIn, pos, context);
        if (state == null || state.get(FACING) == null)
            return super.getShape(state, worldIn, pos, context);

        double pixelSize = 1D / 16D;
        Direction facing = state.get(FACING);
        switch (facing) {
            case NORTH: {
                selectedShape = VoxelShapes.create(1 - (pixelSize * 1), 1, 1 - (pixelSize * 3), pixelSize * 1, 0, pixelSize * 2);

                return selectedShape;
            }
            case SOUTH: {
                selectedShape = VoxelShapes.create(1 - (pixelSize * 1), 1, 1 - (pixelSize * 2), pixelSize * 1, 0, pixelSize * 3);

                return selectedShape;
            }
            case WEST: {
                selectedShape = VoxelShapes.create(pixelSize * 2, 0, pixelSize * 1, 1 - (pixelSize * 3), 1, 1 - (pixelSize * 1));

                return selectedShape;
            }
            case EAST: {
                selectedShape = VoxelShapes.create(1 - (pixelSize * 2), 1, 1 - (pixelSize * 1), pixelSize * 3, 0, pixelSize * 1);

                return selectedShape;
            }
            default: {
                return selectedShape;
            }
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!player.isSneaking()) {
            TileHelm helm = world.getTileEntity(pos) instanceof TileHelm ? (TileHelm) world.getTileEntity(pos) : null;
            if (helm != null && player instanceof ServerPlayerEntity) {
                DavincisUIHooks.openGui(player, helm);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, world, pos, newState, isMoving);
            world.removeTileEntity(pos);
        }
    }

    @Override
    public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param) {
        super.eventReceived(state, world, pos, id, param);
        TileEntity tile = world.getTileEntity(pos);
        return tile != null && tile.receiveClientEvent(id, param);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        if (context.getPlayer() != null && !(context.getPlayer() instanceof FakePlayer)) {
            // TODO: Achievements are gone.
            //((EntityPlayer) placer).addStat(DavincisVesselsContent.achievementCreateHelm);
        }

        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, IS_WHEEL);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileHelm();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileHelm();
    }
}
