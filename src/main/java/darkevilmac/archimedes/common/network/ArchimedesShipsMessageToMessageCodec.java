package darkevilmac.archimedes.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ArchimedesShipsMessageToMessageCodec extends FMLIndexedMessageToMessageCodec<ArchimedesShipsMessage> {

    private int index;

    public ArchimedesShipsMessageToMessageCodec() {
        index = 1;
        addDiscriminator(AssembleResultMessage.class);
        addDiscriminator(ClientHelmActionMessage.class);
        addDiscriminator(ClientOpenGuiMessage.class);
        addDiscriminator(ClientRenameShipMessage.class);
        addDiscriminator(ControlInputMessage.class);
        addDiscriminator(TranslatedChatMessage.class);
        addDiscriminator(ClientChangeSubmerseMessage.class);
        addDiscriminator(ConfigMessage.class);
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
        switch (FMLLaunchHandler.side()) {
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
