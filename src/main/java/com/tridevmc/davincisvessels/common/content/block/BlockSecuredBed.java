package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.tileentity.TileEntitySecuredBed;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.extensions.IForgeDimension.SleepResult;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BlockSecuredBed extends BedBlock implements ITileEntityProvider {

    public BlockSecuredBed() {
        super(DyeColor.RED, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F));
    }

    private PlayerEntity getPlayerInBed(World world, BlockPos pos) {
        return world.getPlayers().stream().filter((p) -> p.isSleeping() && p.getBedLocation().equals(pos)).findAny().orElse(null);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) {
            return true;
        } else {
            if (state.get(PART) != BedPart.HEAD) {
                pos = pos.offset(state.get(HORIZONTAL_FACING));
                state = world.getBlockState(pos);
                if (state.getBlock() != this) {
                    return true;
                }
            }

            TileEntitySecuredBed bed = world.getTileEntity(pos) instanceof TileEntitySecuredBed ? (TileEntitySecuredBed) world.getTileEntity(pos) : null;
            SleepResult sleepResult = world.dimension.canSleepAt(player, pos);
            if (sleepResult != SleepResult.BED_EXPLODES) {
                if (sleepResult == SleepResult.DENY) return true;
                if (bed.occupied) {
                    player.sendStatusMessage(new TranslationTextComponent("block.minecraft.bed.occupied"), true);
                    return true;
                } else {
                    bed.setPlayer(player);
                    player.trySleep(pos).ifLeft((result) -> {
                        if (result != null) {
                            player.sendStatusMessage(result.getMessage(), true);
                        }
                    });
                    return true;
                }
            } else {
                world.removeBlock(pos, false);
                BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());
                if (world.getBlockState(blockpos).getBlock() == this) {
                    world.removeBlock(blockpos, false);
                }

                world.createExplosion(null, DamageSource.netherBedExplosion(), (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
                return true;
            }
        }
    }


    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return DavincisVesselsMod.CONTENT.itemSecuredBed.getDefaultInstance();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return Collections.singletonList(state.get(PART) == BedPart.HEAD ? DavincisVesselsMod.CONTENT.itemSecuredBed.getDefaultInstance() : Items.AIR.getDefaultInstance());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return state.get(PART) == BedPart.HEAD ? new TileEntitySecuredBed() : null;
    }

    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, @Nullable Entity player) {
        return state.getBlock() instanceof BedBlock;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
