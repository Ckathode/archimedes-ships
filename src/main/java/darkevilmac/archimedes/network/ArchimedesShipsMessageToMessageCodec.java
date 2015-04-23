package darkevilmac.archimedes.network;

import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetHandlerPlayServer;


public class ArchimedesShipsMessageToMessageCodec extends FMLIndexedMessageToMessageCodec<ArchimedesShipsMessage> {

    private int index;

    public ArchimedesShipsMessageToMessageCodec() {
        index = 1;
        addDiscriminator(AssembleResultMessage.class);
        addDiscriminator(ClientHelmActionMessage.class);
        addDiscriminator(ClientOpenGuiMessage.class);
        addDiscriminator(ClientRenameShipMessage.class);
        addDiscriminator(ControlInputMessage.class);
    }

    public FMLIndexedMessageToMessageCodec<ArchimedesShipsMessage> addDiscriminator(Class<? extends ArchimedesShipsMessage> type) {
        FMLIndexedMessageToMessageCodec<ArchimedesShipsMessage> ret = super.addDiscriminator(index, type);
        index++;
        return ret;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ArchimedesShipsMessage msg, ByteBuf target) throws Exception {
        msg.encodeInto(ctx, target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, ArchimedesShipsMessage msg) {
        msg.decodeInto(ctx, source, ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity);
    }

}
