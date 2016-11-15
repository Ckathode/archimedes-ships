package io.github.elytra.davincisvessels.common.object.block;

import net.minecraft.block.BlockBed;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

import io.github.elytra.davincisvessels.common.object.DavincisVesselsObjects;
import io.github.elytra.davincisvessels.common.tileentity.TileEntitySecuredBed;

public class BlockSecuredBed extends BlockBed implements ITileEntityProvider {

    public BlockSecuredBed() {
        super();
        this.setSoundType(SoundType.CLOTH);
        disableStats();
    }

    protected EntityPlayer getPlayerInBed(World worldIn, BlockPos pos) {
        Iterator iterator = worldIn.playerEntities.iterator();
        EntityPlayer entityplayer;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entityplayer = (EntityPlayer) iterator.next();
        }

        while (!entityplayer.isPlayerSleeping() || !entityplayer.bedLocation.equals(pos));

        return entityplayer;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            EntityPlayer bedUser = null;

            if (state.getValue(PART) != BlockBed.EnumPartType.HEAD) {
                pos = pos.offset(state.getValue(FACING));
                state = worldIn.getBlockState(pos);

                if (state.getBlock() != this) {
                    return true;
                }
            }

            if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntitySecuredBed) {
                TileEntitySecuredBed tile = (TileEntitySecuredBed) worldIn.getTileEntity(pos);
                if (worldIn.provider.canRespawnHere() && worldIn.getBiome(pos) != Biome.REGISTRY.getObject(new ResourceLocation("hell"))) {
                    if (tile.occupied) {
                        EntityPlayer entityplayer1 = this.getPlayerInBed(worldIn, pos);

                        if (entityplayer1 != null) {
                            playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.occupied"));

                            bedUser = entityplayer1;
                        }
                    }

                    if (bedUser == null) {
                        tile.occupied = false;

                        EntityPlayer.SleepResult sleepResult = playerIn.trySleep(pos);

                        if (sleepResult == EntityPlayer.SleepResult.OK) {
                            tile.occupied = true;
                            bedUser = playerIn;
                        } else {
                            if (sleepResult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                                playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.noSleep"));
                            } else if (sleepResult == EntityPlayer.SleepResult.NOT_SAFE) {
                                playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.notSafe"));
                            }
                        }
                    }

                    if (bedUser != null) {
                        tile.setPlayer(bedUser);
                    }
                } else {
                    worldIn.setBlockToAir(pos);
                    BlockPos blockpos1 = pos.offset(state.getValue(FACING).getOpposite());

                    if (worldIn.getBlockState(blockpos1).getBlock() == this) {
                        worldIn.setBlockToAir(blockpos1);
                    }

                    worldIn.newExplosion(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, true);
                    return true;
                }
            }
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos) {
        return DavincisVesselsObjects.itemSecuredBed;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(PART) == BlockBed.EnumPartType.HEAD ? null : DavincisVesselsObjects.itemSecuredBed;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (this.getStateFromMeta(meta).getValue(BlockBed.PART) == EnumPartType.HEAD) {
            return new TileEntitySecuredBed();
        }

        return null;
    }

    @Override
    public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, Entity player) {
        return Objects.equals(state.getBlock(), DavincisVesselsObjects.blockSecuredBed);
    }
}
