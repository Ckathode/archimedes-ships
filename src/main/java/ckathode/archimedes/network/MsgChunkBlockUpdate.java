package ckathode.archimedes.network;

import ckathode.archimedes.chunk.ChunkIO;
import ckathode.archimedes.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkPosition;

import java.io.IOException;
import java.util.Collection;

public class MsgChunkBlockUpdate extends ASMessageShip {
    private Collection<ChunkPosition> sendQueue;

    public MsgChunkBlockUpdate() {
    }

    public MsgChunkBlockUpdate(EntityShip entityship, Collection<ChunkPosition> blocks) {
        super(entityship);
        sendQueue = blocks;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
        super.encodeInto(ctx, buf);
        ChunkIO.writeCompressed(buf, ship.getShipChunk(), sendQueue);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) throws IOException {
        super.decodeInto(ctx, buf, player);
        if (ship != null) {
            ChunkIO.readCompressed(buf, ship.getShipChunk());
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
    }
}
