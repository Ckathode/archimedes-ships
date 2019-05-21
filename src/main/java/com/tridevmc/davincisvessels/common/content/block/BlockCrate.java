package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.common.content.DavincisVesselsContent;
import com.tridevmc.davincisvessels.common.tileentity.TileCrate;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
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

public class BlockCrate extends BlockContainer {
    private static final VoxelShape SHAPE = VoxelShapes.create(0F, 0F, 0F, 1F, 0.1F, 1F);

    public static final EnumProperty AXIS = EnumProperty.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public BlockCrate() {
        super(Block.Properties.create(Material.WOOD));
    }

    public static int getMetaForAxis(EnumFacing.Axis axis) {
        return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
    }

    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return SHAPE;
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(AXIS, context.getPlacementHorizontalFacing().getAxis());
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote || state.getValue(POWERED))
            return;

        if (entity != null && !(entity instanceof EntityPlayer || entity instanceof EntityMovingWorld)) {
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
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileCrate) {
            ((TileCrate) te).releaseEntity();
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
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.isRemote || worldIn.getBlockState(pos).getBlock() != DavincisVesselsContent.blockCrateWood)
            return;

        if (!canBePlacedOn(worldIn, pos.down())) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }

        boolean powered = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());

        if (powered) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileCrate) {
                ((TileCrate) te).releaseEntity();
                worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(POWERED, Boolean.TRUE));
            }
        } else {
            worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(POWERED, Boolean.FALSE));
        }
    }
}
