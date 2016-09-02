package elytra.davincisvessels.common.entity;

import elytra.davincisvessels.common.object.block.BlockHelm;
import elytra.davincisvessels.common.tileentity.TileEntityHelm;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import elytra.davincisvessels.DavincisVesselsMod;
import elytra.davincisvessels.common.api.block.IBlockBalloon;
import elytra.davincisvessels.common.handler.ConnectionHandler;
import elytra.davincisvessels.common.object.DavincisVesselsObjects;
import elytra.davincisvessels.common.tileentity.TileEntitySecuredBed;
import darkevilmac.movingworld.MovingWorldMod;
import darkevilmac.movingworld.common.chunk.LocatedBlock;
import darkevilmac.movingworld.common.chunk.MovingWorldAssemblyInteractor;
import darkevilmac.movingworld.common.chunk.assembly.CanAssemble;
import io.netty.buffer.ByteBuf;

import static darkevilmac.movingworld.common.chunk.assembly.AssembleResult.ResultType.RESULT_NONE;

public class ShipAssemblyInteractor extends MovingWorldAssemblyInteractor {

    private int balloonCount;

    public ShipAssemblyInteractor() {
    }

    @Override
    public void toByteBuf(ByteBuf byteBuf) {
        byteBuf.writeInt(getBalloonCount());
    }

    @Override
    public MovingWorldAssemblyInteractor fromByteBuf(byte resultCode, ByteBuf buf) {
        if (resultCode == RESULT_NONE.toByte()) {
            return new ShipAssemblyInteractor();
        }
        int balloons = buf.readInt();

        ShipAssemblyInteractor assemblyInteractor = new ShipAssemblyInteractor();
        assemblyInteractor.setBalloonCount(balloons);

        return assemblyInteractor;
    }

    @Override
    public MovingWorldAssemblyInteractor fromNBT(NBTTagCompound tag, World world) {
        ShipAssemblyInteractor mov = new ShipAssemblyInteractor();
        mov.setBalloonCount(tag.getInteger("balloonCount"));
        return mov;
    }

    @Override
    public void blockAssembled(LocatedBlock locatedBlock) {
        Block block = locatedBlock.blockState.getBlock();
        if (block instanceof IBlockBalloon) {
            try {
                balloonCount += ((IBlockBalloon) block).getBalloonWorth(locatedBlock.tileEntity);
            } catch (NullPointerException e) {
                MovingWorldMod.logger.error("IBlockBalloon didn't check if something was null or not, report to mod author. " + block.toString());
            }
        } else if (block == DavincisVesselsObjects.blockBalloon) {
            balloonCount++;
        } else if (DavincisVesselsMod.instance.getNetworkConfig().isBalloon(block)) {
            balloonCount++;
        }
    }

    @Override
    public void blockDisassembled(LocatedBlock locatedBlock) {
        super.blockDisassembled(locatedBlock); // Currently unimplemented but leaving there just in case.

        if (locatedBlock.tileEntity != null && locatedBlock.tileEntity.getWorld() != null && !locatedBlock.tileEntity.getWorld().isRemote) {
            if (locatedBlock.tileEntity instanceof TileEntitySecuredBed) {
                TileEntitySecuredBed securedBed = (TileEntitySecuredBed) locatedBlock.tileEntity;

                securedBed.doMove = true;
                ConnectionHandler.playerBedMap.remove(securedBed.playerID);
                securedBed.addToConnectionMap(securedBed.playerID);
                securedBed.moveBed(locatedBlock.blockPos);
            }
        }
    }

    @Override
    public boolean isBlockMovingWorldMarker(Block block) {
        if (block != null)
            return block.getUnlocalizedName() == DavincisVesselsObjects.blockMarkShip.getUnlocalizedName();
        else
            return false;
    }

    @Override
    public boolean isTileMovingWorldMarker(TileEntity tile) {
        if (tile != null)
            return tile instanceof TileEntityHelm;
        else
            return false;
    }

    @Override
    public CanAssemble isBlockAllowed(World world, LocatedBlock lb) {
        IBlockState state = lb.blockState;
        BlockPos pos = lb.blockPos;
        CanAssemble canAssemble = super.isBlockAllowed(world, lb);

        if (state.getBlock() == DavincisVesselsObjects.blockStickyBuffer || DavincisVesselsMod.instance.getNetworkConfig().isSticky(state.getBlock()))
            canAssemble.assembleThenCancel = true;

        //if (lb.tileEntity != null && lb.tileEntity instanceof TileEntityAnchorPoint
        //        && ((TileEntityAnchorPoint) lb.tileEntity).anchorPointInfo != null
        //        && !((TileEntityAnchorPoint) lb.tileEntity).anchorPointInfo.forShip)
        //    canAssemble.justCancel = true;

        return canAssemble;
    }

    @Override
    public EnumFacing getFrontDirection(LocatedBlock marker) {
        return marker.blockState.getValue(BlockHelm.FACING).getOpposite();
    }

    public int getBalloonCount() {
        return balloonCount;
    }

    public void setBalloonCount(int balloonCount) {
        this.balloonCount = balloonCount;
    }

    @Override
    public void writeNBTFully(NBTTagCompound tag) {
        writeNBTMetadata(tag);
    }

    @Override
    public void writeNBTMetadata(NBTTagCompound tag) {
        tag.setInteger("balloonCount", getBalloonCount());
    }
}
