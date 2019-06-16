package com.tridevmc.davincisvessels.common.entity;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.api.block.IBlockBalloon;
import com.tridevmc.davincisvessels.common.content.block.BlockHelm;
import com.tridevmc.davincisvessels.common.handler.ConnectionHandler;
import com.tridevmc.davincisvessels.common.tileentity.AnchorInstance;
import com.tridevmc.davincisvessels.common.tileentity.TileAnchorPoint;
import com.tridevmc.davincisvessels.common.tileentity.TileEntitySecuredBed;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import com.tridevmc.movingworld.MovingWorldMod;
import com.tridevmc.movingworld.common.chunk.LocatedBlock;
import com.tridevmc.movingworld.common.chunk.MovingWorldAssemblyInteractor;
import com.tridevmc.movingworld.common.chunk.assembly.CanAssemble;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

import static com.tridevmc.movingworld.common.chunk.assembly.AssembleResult.ResultType.RESULT_NONE;

public class VesselAssemblyInteractor extends MovingWorldAssemblyInteractor {

    private static List allowedBlocks = ForgeRegistries.BLOCKS.getEntries().stream()
            .filter(e -> e.getKey().getNamespace().equals(DavincisVesselsMod.MOD_ID) && !e.getKey().getPath().equals("buffer"))
            .collect(Collectors.toList());

    private int balloonCount;

    public VesselAssemblyInteractor() {
    }

    @Override
    public void toByteBuf(ByteBuf byteBuf) {
        byteBuf.writeInt(getBalloonCount());
    }

    @Override
    public MovingWorldAssemblyInteractor fromByteBuf(byte resultCode, ByteBuf buf) {
        if (resultCode == RESULT_NONE.toByte()) {
            return new VesselAssemblyInteractor();
        }
        int balloons = buf.readInt();

        VesselAssemblyInteractor assemblyInteractor = new VesselAssemblyInteractor();
        assemblyInteractor.setBalloonCount(balloons);

        return assemblyInteractor;
    }

    @Override
    public MovingWorldAssemblyInteractor fromNBT(CompoundNBT tag, World world) {
        VesselAssemblyInteractor mov = new VesselAssemblyInteractor();
        mov.setBalloonCount(tag.getInt("balloonCount"));
        return mov;
    }

    @Override
    public void blockAssembled(LocatedBlock locatedBlock) {
        Block block = locatedBlock.state.getBlock();
        if (block instanceof IBlockBalloon) {
            try {
                balloonCount += ((IBlockBalloon) block).getBalloonWorth(locatedBlock.tile);
            } catch (NullPointerException e) {
                MovingWorldMod.LOG.error("IBlockBalloon didn't check if something was null or not, report to mod author of the following block, " + block.toString());
            }
        } else if (DavincisVesselsMod.BLOCK_CONFIG.isBalloon(block)) {
            balloonCount++;
        }
    }

    @Override
    public void blockDisassembled(LocatedBlock locatedBlock) {
        super.blockDisassembled(locatedBlock); // Currently unimplemented but leaving there just in case.

        if (locatedBlock.state.getBlock() == DavincisVesselsMod.CONTENT.blockSecuredBed) {
            if (locatedBlock.tile instanceof TileEntitySecuredBed) {
                TileEntitySecuredBed securedBed = (TileEntitySecuredBed) locatedBlock.tile;

                securedBed.doMove = true;
                ConnectionHandler.playerBedMap.remove(securedBed.getPlayerID());
                securedBed.addToConnectionMap(securedBed.getPlayerID());
                securedBed.moveBed(locatedBlock.pos);
            }
        }
    }

    @Override
    public boolean isBlockMovingWorldMarker(Block block) {
        if (block != null)
            return block == DavincisVesselsMod.CONTENT.blockHelm;
        else
            return false;
    }

    @Override
    public boolean isTileMovingWorldMarker(TileEntity tile) {
        if (tile != null)
            return tile instanceof TileHelm;
        else
            return false;
    }

    @Override
    public CanAssemble isBlockAllowed(World world, LocatedBlock lb) {
        BlockState state = lb.state;
        CanAssemble canAssemble = super.isBlockAllowed(world, lb);

        if (DavincisVesselsMod.BLOCK_CONFIG.isSticky(state.getBlock()))
            canAssemble.assembleThenCancel = true;

        if (lb.tile instanceof TileAnchorPoint
                && ((TileAnchorPoint) lb.tile).getInstance().getType() == AnchorInstance.InstanceType.LAND)
            canAssemble.justCancel = true;

        if (canAssemble.justCancel) {
            canAssemble.justCancel = !allowedBlocks.contains(lb.getBlock());
        } else {
            canAssemble.justCancel = lb.getBlock() == DavincisVesselsMod.CONTENT.blockBuffer;
        }

        return canAssemble;
    }

    @Override
    public Direction getFrontDirection(LocatedBlock marker) {
        return marker.state.get(BlockHelm.FACING).getOpposite();
    }

    public int getBalloonCount() {
        return balloonCount;
    }

    public void setBalloonCount(int balloonCount) {
        this.balloonCount = balloonCount;
    }

    @Override
    public void writeNBTFully(CompoundNBT tag) {
        writeNBTMetadata(tag);
    }

    @Override
    public void writeNBTMetadata(CompoundNBT tag) {
        tag.putInt("balloonCount", getBalloonCount());
    }
}
