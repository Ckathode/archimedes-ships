package ckathode.archimedes.network;

import ckathode.archimedes.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class MsgControlInput extends ASMessageShip {
    public int control;

    public MsgControlInput() {
        super();
        control = 0;
    }

    public MsgControlInput(EntityShip entityship, int controlid) {
        super(entityship);
        control = controlid;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
        super.encodeInto(ctx, buf);
        buf.writeByte(control);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) throws IOException {
        super.decodeInto(ctx, buf, player);
        control = buf.readByte();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        if (ship != null) {
            ship.getController().updateControl(ship, player, control);
        }
    }

}
