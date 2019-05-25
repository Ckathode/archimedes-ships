package com.tridevmc.davincisvessels.common.content.block;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.tileentity.TileEntitySecuredBed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeDimension;

import javax.annotation.Nullable;

public class BlockSecuredBed extends BlockBed implements ITileEntityProvider {

    public BlockSecuredBed() {
        super(EnumDyeColor.RED, Block.Properties.create(Material.CLOTH).sound(SoundType.WOOD));
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            EntityPlayer bedUser = null;

            if (state.get(PART) != BedPart.HEAD) {
                pos = pos.offset(state.get(HORIZONTAL_FACING));
                state = worldIn.getBlockState(pos);

                if (state.getBlock() != this) {
                    return true;
                }
            }

            if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntitySecuredBed) {
                TileEntitySecuredBed tile = (TileEntitySecuredBed) worldIn.getTileEntity(pos);
                IForgeDimension.SleepResult sleepResult = worldIn.dimension.canSleepAt(player, pos);
                if (sleepResult != IForgeDimension.SleepResult.BED_EXPLODES) {
                    if (sleepResult == IForgeDimension.SleepResult.DENY)
                        return true;
                    if (tile.occupied) {
                        EntityPlayer entityplayer1 = this.getPlayerInBed(worldIn, pos);

                        if (entityplayer1 != null) {
                            player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.occupied", new Object[0]), true);

                            bedUser = entityplayer1;
                        }
                    }

                    if (bedUser == null) {
                        tile.occupied = false;
                        EntityPlayer.SleepResult playerSleepResult = player.trySleep(pos);

                        if (playerSleepResult == EntityPlayer.SleepResult.OK) {
                            tile.occupied = true;
                            bedUser = player;
                        } else {
                            if (playerSleepResult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                                player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.no_sleep", new Object[0]), true);
                            } else if (playerSleepResult == EntityPlayer.SleepResult.NOT_SAFE) {
                                player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.not_safe", new Object[0]), true);
                            } else if (playerSleepResult == EntityPlayer.SleepResult.TOO_FAR_AWAY) {
                                player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.too_far_away"), true);
                            }
                        }
                    }

                    if (bedUser != null) {
                        tile.setPlayer(bedUser);
                    }
                } else {
                    worldIn.removeBlock(pos);
                    BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());
                    if (worldIn.getBlockState(blockpos).getBlock() == this) {
                        worldIn.removeBlock(blockpos);
                    }

                    worldIn.createExplosion((Entity) null, DamageSource.netherBedExplosion(), (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, true);
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
        return DavincisVesselsMod.CONTENT.itemSecuredBed.getDefaultInstance();
    }

    @Override
    public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
        return () -> state.get(PART) == BedPart.HEAD ? DavincisVesselsMod.CONTENT.itemSecuredBed : Items.AIR;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
        return state.get(PART) == BedPart.HEAD ? new TileEntitySecuredBed() : null;
    }

    @Override
    public boolean isBed(IBlockState state, IBlockReader world, BlockPos pos, @Nullable Entity player) {
        return state.getBlock() instanceof BlockBed;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

}
