package darkevilmac.archimedes.network;

import darkevilmac.archimedes.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class FarInteractMessage extends ShipMessage {
    public FarInteractMessage() {
        super();
    }

    public FarInteractMessage(EntityShip entityship) {
        super(entityship);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) {
        super.encodeInto(ctx, buf);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) {
        super.decodeInto(ctx, buf, player);
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        if (ship != null) {
            player.interactWith(ship);
        }
    }

}
