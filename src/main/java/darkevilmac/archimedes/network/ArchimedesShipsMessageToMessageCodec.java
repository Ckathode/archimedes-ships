package darkevilmac.archimedes.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
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
        addDiscriminator(RequestSetParentShipMessage.class);
        addDiscriminator(SetParentShipMessage.class);
    }

    public FMLIndexedMessageToMessageCodec<ArchimedesShipsMessage> addDiscriminator(Class<? extends ArchimedesShipsMessage> type) {
        FMLIndexedMessageToMessageCodec<ArchimedesShipsMessage> ret = super.addDiscriminator(index, type);
        index++;
        return ret;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ArchimedesShipsMessage msg, ByteBuf target) throws Exception {
        msg.encodeInto(ctx, target, FMLCommonHandler.instance().getSide());
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, ArchimedesShipsMessage msg) {
        EntityPlayer player;
        switch (FMLCommonHandler.instance().getEffectiveSide()) {
            case CLIENT:
                player = this.getClientPlayer();
                msg.decodeInto(ctx, source, player, FMLCommonHandler.instance().getSide());
                break;
            case SERVER:
                player = getServerPlayer(ctx);
                msg.decodeInto(ctx, source, player, FMLCommonHandler.instance().getSide());
                break;
        }
    }

    @SideOnly(Side.CLIENT)
    private EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    private EntityPlayer getServerPlayer(ChannelHandlerContext ctx) {
        INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        return ((NetHandlerPlayServer) netHandler).playerEntity;
    }

}
