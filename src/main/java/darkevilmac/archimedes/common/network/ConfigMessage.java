package darkevilmac.archimedes.common.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.client.ClientProxy;
import darkevilmac.archimedes.common.ArchimedesConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Converts server config to JSON sends to client and then sets inside the proxy for further reference.
 */
public class ConfigMessage extends ArchimedesShipsMessage {

    public ArchimedesConfig.SharedConfig config;

    public ConfigMessage() {
        config = null;
    }

    public ConfigMessage(ArchimedesConfig.SharedConfig cfg) {
        this.config = cfg;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            if (config != null) {
                GsonBuilder builder = new GsonBuilder();
                String jsonCfg = builder.create().toJson(ArchimedesShipMod.instance.getNetworkConfig().getShared(), ArchimedesConfig.SharedConfig.class);
                ByteBufUtils.writeUTF8String(buf, jsonCfg);
            } else {
                ByteBufUtils.writeUTF8String(buf, "N");
            }
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        if (FMLCommonHandler.instance().getSide().isClient() && !buf.toString().contains("Empty")) {
            String msg = ByteBufUtils.readUTF8String(buf);
            if (!msg.equals("N")) {
                config = new Gson().fromJson(msg, ArchimedesConfig.SharedConfig.class);
            } else config = null;
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        if (config != null) {
            ((ClientProxy) ArchimedesShipMod.proxy).syncedConfig = ArchimedesShipMod.instance.getLocalConfig();
            ((ClientProxy) ArchimedesShipMod.proxy).syncedConfig.setShared(config);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
