package darkevilmac.archimedes.common.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Sharable
public class ArchimedesShipsPacketHandler extends SimpleChannelInboundHandler<ArchimedesShipsMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ArchimedesShipsMessage msg) throws Exception {
        EntityPlayer player;
        switch (FMLCommonHandler.instance().getSide()) {
            case CLIENT:
                player = this.getClientPlayer();
                msg.handleClientSide(player);
                break;

            case SERVER:
                player = getServerPlayer(ctx);
                msg.handleServerSide(player);
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