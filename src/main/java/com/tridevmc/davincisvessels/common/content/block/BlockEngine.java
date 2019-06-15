package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.common.DavincisUIHooks;
import com.tridevmc.davincisvessels.common.tileentity.TileEngine;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockEngine extends ContainerBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public float enginePower;
    public int engineFuelConsumption;

    public BlockEngine(float power, int fuelConsumption) {
        super(Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(2F, 3F));
        enginePower = power;
        engineFuelConsumption = fuelConsumption;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileEngine(enginePower, engineFuelConsumption);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!player.isSneaking()) {
            TileEngine engine = world.getTileEntity(pos) instanceof TileEngine ? (TileEngine) world.getTileEntity(pos) : null;
            if (engine != null && player instanceof ServerPlayerEntity) {
                DavincisUIHooks.openGui(player, engine);
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEngine engine = (TileEngine) worldIn.getTileEntity(pos);

        if (engine != null) {
            InventoryHelper.dropInventoryItems(worldIn, pos, engine);
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
