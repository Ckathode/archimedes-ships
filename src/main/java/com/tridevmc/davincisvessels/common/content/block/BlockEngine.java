package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.tileentity.TileEngine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockEngine extends BlockContainer {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", EnumFacing.Plane.HORIZONTAL);

    public float enginePower;
    public int engineFuelConsumption;

    public BlockEngine(float power, int fuelConsumption) {
        super(Block.Properties.create(Material.IRON).sound(SoundType.METAL));
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

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            IBlockState northState = worldIn.getBlockState(pos.north());
            IBlockState southState = worldIn.getBlockState(pos.south());
            IBlockState westState = worldIn.getBlockState(pos.west());
            IBlockState eastState = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = state.get(FACING);

            if (enumfacing == EnumFacing.NORTH && northState.isFullCube() && !southState.isFullCube()) {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && southState.isFullCube() && !northState.isFullCube()) {
                enumfacing = EnumFacing.NORTH;
            } else if (enumfacing == EnumFacing.WEST && westState.isFullCube() && !eastState.isFullCube()) {
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && eastState.isFullCube() && !westState.isFullCube()) {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.with(FACING, enumfacing), 2);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.isSneaking()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity != null) {
                playerIn.openGui(DavincisVesselsMod.INSTANCE, 3, worldIn, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }

        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if (placer instanceof EntityPlayer) {
            // TODO: Achievements are gone.
            //((EntityPlayer) placer).addStat(DavincisVesselsContent.achievementCreateEngine);
        }

        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }


    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEngine engine = (TileEngine) worldIn.getTileEntity(pos);

        if (engine != null) {
            InventoryHelper.dropInventoryItems(worldIn, pos, engine);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
