package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.tileentity.TileCrate;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
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
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockCrate extends ContainerBlock {
    private static final VoxelShape SHAPE = VoxelShapes.create(0F, 0F, 0F, 1F, 0.1F, 1F);

    public static final EnumProperty AXIS = EnumProperty.create("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public BlockCrate() {
        super(Block.Properties.create(Material.WOOD).hardnessAndResistance(1F));
    }

    public static int getMetaForAxis(Direction.Axis axis) {
        return axis == Direction.Axis.X ? 1 : (axis == Direction.Axis.Z ? 2 : 0);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(AXIS, context.getPlacementHorizontalFacing().getAxis());
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote || state.get(POWERED))
            return;

        if (entity != null && !(entity instanceof PlayerEntity || entity instanceof EntityMovingWorld)) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileCrate) {
                if (((TileCrate) te).canCatchEntity() && ((TileCrate) te).getContainedEntity() == null) {
                    ((TileCrate) te).setContainedEntity(entity);
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileCrate();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileCrate) {
            ((TileCrate) te).releaseEntity();
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return func_220064_c(world, blockpos) || func_220055_a(world, blockpos, Direction.UP);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
        if (world.isRemote || world.getBlockState(pos).getBlock() != DavincisVesselsMod.CONTENT.blockCrate)
            return;

        if (!isValidPosition(state, world, pos)) {
            world.destroyBlock(pos, true);
        }

        boolean powered = world.isBlockPowered(pos) || world.isBlockPowered(pos.up());

        if (powered) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileCrate) {
                ((TileCrate) te).releaseEntity();
                world.setBlockState(pos, world.getBlockState(pos).with(POWERED, Boolean.TRUE));
            }
        } else {
            world.setBlockState(pos, world.getBlockState(pos).with(POWERED, Boolean.FALSE));
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS).add(POWERED);
    }
}
