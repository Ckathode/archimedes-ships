package darkevilmac.archimedes.network;

import darkevilmac.archimedes.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class ControlInputMessage extends ShipMessage {
    public int control;

    public ControlInputMessage() {
        super();
        control = 0;
    }

    public ControlInputMessage(EntityShip entityship, int controlid) {
        super(entityship);
        control = controlid;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) {
        super.encodeInto(ctx, buf);
        buf.writeByte(control);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) {
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
