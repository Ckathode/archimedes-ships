package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.common.tileentity.TileEngine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockEngine extends BlockContainer {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", EnumFacing.Plane.HORIZONTAL);

    public float enginePower;
    public int engineFuelConsumption;

    public BlockEngine(float power, int fuelConsumption) {
        super(Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(2F, 3F));
        enginePower = power;
        engineFuelConsumption = fuelConsumption;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileEngine(enginePower, engineFuelConsumption);
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            TileEngine tile = world.getTileEntity(pos) instanceof TileEngine ? (TileEngine) world.getTileEntity(pos) : null;
            if (tile != null && player instanceof EntityPlayerMP) {
                NetworkHooks.openGui((EntityPlayerMP) player, tile, pos);
                return true;
            }
        }

        return false;
    }

    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
        TileEngine engine = (TileEngine) worldIn.getTileEntity(pos);

        if (engine != null) {
            InventoryHelper.dropInventoryItems(worldIn, pos, engine);
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING);
    }
}
