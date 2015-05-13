package darkevilmac.archimedes.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class TranslatedChatMessage extends ArchimedesShipsMessage {

    public String message;

    public TranslatedChatMessage(String message) {
        this.message = message;
    }

    public TranslatedChatMessage() {
        message = "";
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        ByteBufUtils.writeUTF8String(buf, message);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player, Side side) {
        message = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        String[] split = message.split("~");
        ChatComponentText text = new ChatComponentText("");

        if (split.length > 0) {
            for (String string : split) {
                if (string.startsWith("TR:")) {
                    text.appendSibling(new ChatComponentTranslation(string.substring(3)));
                } else {
                    text.appendSibling(new ChatComponentText(string));
                }
            }
        }

        player.addChatComponentMessage(text);
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
