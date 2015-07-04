package darkevilmac.archimedes.common.network;

import darkevilmac.archimedes.common.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ClientChangeSubmerseMessage extends ShipMessage {

    public boolean submerse;

    public ClientChangeSubmerseMessage() {
        super();
        submerse = false;
    }

    public ClientChangeSubmerseMessage(EntityShip ship, boolean submerse) {
        super(ship);
        this.submerse = submerse;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        super.encodeInto(ctx, buf, side);
        buf.writeBoolean(submerse);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player, Side side) {
        super.decodeInto(ctx, buf, player, side);
        submerse = buf.readBoolean();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        ship.setSubmerge(submerse);
    }
}
