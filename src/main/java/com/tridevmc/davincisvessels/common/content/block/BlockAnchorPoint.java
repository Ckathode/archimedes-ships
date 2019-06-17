package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.DavincisUIHooks;
import com.tridevmc.davincisvessels.common.tileentity.AnchorInstance;
import com.tridevmc.davincisvessels.common.tileentity.BlockLocation;
import com.tridevmc.davincisvessels.common.tileentity.TileAnchorPoint;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class BlockAnchorPoint extends ContainerBlock {

    public static final EnumProperty AXIS = EnumProperty.create("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);

    private VoxelShape xShape, zShape;

    public BlockAnchorPoint() {
        super(Block.Properties.create(Material.WOOD).sound(SoundType.METAL).hardnessAndResistance(1F));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction.Axis axis = (Direction.Axis) state.get(AXIS);
        if (axis == Direction.Axis.X && xShape == null) {
            xShape = VoxelShapes.create(0.375f, 0.0F, 0, 0.625f, 1.0F, 1F);
        } else if (axis == Direction.Axis.Z && zShape == null) {
            zShape = VoxelShapes.create(0, 0.0F, 0.375f, 0, 1.0F, 0.625f);
        }
        return axis == Direction.Axis.X ? xShape : zShape;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(AXIS, context.getPlacementHorizontalFacing().getAxis());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (worldIn != null && !worldIn.isRemote && worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileAnchorPoint) {
            if (stack != null && stack.getTag() != null && stack.getTag().contains("INSTANCE")) {
                TileAnchorPoint anchorPoint = (TileAnchorPoint) worldIn.getTileEntity(pos);
                AnchorInstance instance = new AnchorInstance();
                instance.deserializeNBT(stack.getTag().getCompound("INSTANCE"));
                instance.setIdentifier(UUID.randomUUID());
                anchorPoint.setInstance(instance);

                for (Map.Entry<UUID, BlockLocation> relation : anchorPoint.getInstance().getRelatedAnchors().entrySet()) {
                    World relationWorld = DimensionManager.getWorld(worldIn.getServer(), relation.getValue().getDim(), false, false);
                    BlockState relationState = relationWorld.getBlockState(relation.getValue().getPos());
                    if (relationState.getBlock().equals(DavincisVesselsMod.CONTENT.blockAnchorPoint)) {
                        TileEntity relationTile = relationWorld.getTileEntity(relation.getValue().getPos());
                        if (relationTile instanceof TileAnchorPoint) {
                            TileAnchorPoint relationAnchor = (TileAnchorPoint) relationTile;
                            if (!relationAnchor.getInstance().getType().equals(anchorPoint.getInstance().getType())) {
                                if (relationAnchor.getInstance().getIdentifier().equals(relation.getKey())) {
                                    relationAnchor.getInstance().addRelation(anchorPoint.getInstance().getIdentifier(), new BlockLocation(pos, worldIn.getDimension().getType()));
                                    relationAnchor.markDirty();
                                    if (worldIn instanceof ServerWorld)
                                        worldIn.getChunk(relationAnchor.getChunkPos()).setModified(true);

                                    continue;
                                }
                            }
                        }
                    }
                    anchorPoint.getInstance().removeRelation(relation.getKey());
                    break;
                }
            }
        }
    }


    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!player.isSneaking()) {
            TileAnchorPoint anchor = worldIn.getTileEntity(pos) instanceof TileAnchorPoint ? (TileAnchorPoint) worldIn.getTileEntity(pos) : null;
            if (anchor != null && player instanceof ServerPlayerEntity) {
                DavincisUIHooks.openGui((ServerPlayerEntity) player, anchor, pos);
            }
            return true;
        }

        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileAnchorPoint();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
}
