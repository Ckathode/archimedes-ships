package darkevilmac.archimedes.common.network;

import com.google.gson.Gson;
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

    public ArchimedesConfig config;

    public ConfigMessage() {
        config = null;
    }

    public ConfigMessage(ArchimedesConfig cfg) {
        this.config = cfg;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        if (!FMLCommonHandler.instance().getSide().isServer())
            return;

        if (config != null) {
            String jsonCfg = new Gson().toJson(ArchimedesShipMod.instance.getNetworkConfig(), ArchimedesConfig.class);
            ByteBufUtils.writeUTF8String(buf, jsonCfg);
        } else {
            ByteBufUtils.writeUTF8String(buf, "N");
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player, Side side) {
        String msg = ByteBufUtils.readUTF8String(buf);
        if (!msg.equals("N"))
            config = new Gson().fromJson(msg, ArchimedesConfig.class);
        else
            config = null;
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        ((ClientProxy) ArchimedesShipMod.proxy).syncedConfig = this.config;
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
