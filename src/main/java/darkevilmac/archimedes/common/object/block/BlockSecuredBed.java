package darkevilmac.archimedes.common.object.block;

import darkevilmac.archimedes.common.object.ArchimedesObjects;
import darkevilmac.archimedes.common.tileentity.TileEntitySecuredBed;
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
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.Random;

public class BlockSecuredBed extends BlockBed implements ITileEntityProvider {

    public BlockSecuredBed() {
        super();
        this.setSoundType(SoundType.CLOTH);
        disableStats();
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

            if (worldIn.provider.canRespawnHere() && worldIn.getBiomeGenForCoords(pos) != BiomeGenBase.biomeRegistry.getObject(new ResourceLocation("hell"))) {
                if (state.getValue(OCCUPIED).booleanValue()) {
                    EntityPlayer entityplayer1 = this.getPlayerInBed(worldIn, pos);

                    if (entityplayer1 != null) {
                        playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.occupied"));

                        bedUser = entityplayer1;
                    }
                }

                if (bedUser == null) {
                    state = state.withProperty(OCCUPIED, Boolean.valueOf(false));
                    worldIn.setBlockState(pos, state, 4);

                    EntityPlayer.EnumStatus enumstatus = playerIn.trySleep(pos);

                    if (enumstatus == EntityPlayer.EnumStatus.OK) {
                        state = state.withProperty(OCCUPIED, Boolean.valueOf(true));
                        worldIn.setBlockState(pos, state, 4);

                        bedUser = playerIn;
                    } else {
                        if (enumstatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
                            playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.noSleep"));
                        } else if (enumstatus == EntityPlayer.EnumStatus.NOT_SAFE) {
                            playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.notSafe"));
                        }
                    }
                }

                if (bedUser != null) {
                    if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntitySecuredBed) {
                        TileEntitySecuredBed tile = (TileEntitySecuredBed) worldIn.getTileEntity(pos);

                        tile.setPlayer(bedUser);
                    }
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

        return true;
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
        while (!entityplayer.isPlayerSleeping() || !entityplayer.playerLocation.equals(pos));

        return entityplayer;
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos) {
        return ArchimedesObjects.itemSecuredBed;
    }

    public boolean isBed(IBlockAccess world, BlockPos pos, Entity player) {
        return this instanceof BlockBed;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(PART) == BlockBed.EnumPartType.HEAD ? null : ArchimedesObjects.itemSecuredBed;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (this.getStateFromMeta(meta).getValue(BlockBed.PART) == EnumPartType.HEAD)
            return new TileEntitySecuredBed();

        return null;
    }
}
