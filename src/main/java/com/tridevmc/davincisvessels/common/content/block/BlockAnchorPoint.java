package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.tileentity.AnchorInstance;
import com.tridevmc.davincisvessels.common.tileentity.BlockLocation;
import com.tridevmc.davincisvessels.common.tileentity.TileAnchorPoint;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class BlockAnchorPoint extends BlockContainer {

    public static final EnumProperty AXIS = EnumProperty.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);

    private VoxelShape xShape, zShape;

    public BlockAnchorPoint() {
        super(Block.Properties.create(Material.WOOD).sound(SoundType.METAL).hardnessAndResistance(1F));
    }

    public static int getMetaForAxis(EnumFacing.Axis axis) {
        return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
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
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        EnumFacing.Axis axis = (EnumFacing.Axis) state.get(AXIS);
        if (axis == EnumFacing.Axis.X && xShape == null) {
            xShape = VoxelShapes.create(0.375f, 0.0F, 0, 0.625f, 1.0F, 1F);
        } else if (axis == EnumFacing.Axis.Z && zShape == null) {
            zShape = VoxelShapes.create(0, 0.0F, 0.375f, 0, 1.0F, 0.625f);
        }
        return axis == EnumFacing.Axis.X ? xShape : zShape;
    }

    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(AXIS, context.getPlacementHorizontalFacing().getAxis());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (worldIn != null && !worldIn.isRemote && worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileAnchorPoint) {
            if (stack != null && stack.getTag() != null && stack.getTag().contains("INSTANCE")) {
                TileAnchorPoint anchorPoint = (TileAnchorPoint) worldIn.getTileEntity(pos);
                AnchorInstance instance = new AnchorInstance();
                instance.deserializeNBT(stack.getTag().getCompound("INSTANCE"));
                instance.setIdentifier(UUID.randomUUID());
                anchorPoint.setInstance(instance);

                for (Map.Entry<UUID, BlockLocation> relation : anchorPoint.getInstance().getRelatedAnchors().entrySet()) {
                    World relationWorld = DimensionManager.getWorld(worldIn.getServer(), relation.getValue().getDim(), false, false);
                    IBlockState relationState = relationWorld.getBlockState(relation.getValue().getPos());
                    if (relationState.getBlock().equals(DavincisVesselsMod.CONTENT.blockAnchorPoint)) {
                        TileEntity relationTile = relationWorld.getTileEntity(relation.getValue().getPos());
                        if (relationTile instanceof TileAnchorPoint) {
                            TileAnchorPoint relationAnchor = (TileAnchorPoint) relationTile;
                            if (!relationAnchor.getInstance().getType().equals(anchorPoint.getInstance().getType())) {
                                if (relationAnchor.getInstance().getIdentifier().equals(relation.getKey())) {
                                    relationAnchor.getInstance().addRelation(anchorPoint.getInstance().getIdentifier(), new BlockLocation(pos, worldIn.getDimension().getType()));
                                    relationAnchor.markDirty();
                                    if (worldIn instanceof WorldServer)
                                        ((WorldServer) worldIn).getPlayerChunkMap().markBlockForUpdate(relationAnchor.getPos());

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
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            TileAnchorPoint anchor = worldIn.getTileEntity(pos) instanceof TileAnchorPoint ? (TileAnchorPoint) worldIn.getTileEntity(pos) : null;
            if (anchor != null && player instanceof EntityPlayerMP) {
                NetworkHooks.openGui((EntityPlayerMP) player, anchor, pos);
                return true;
            }
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
}
