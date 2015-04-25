package darkevilmac.archimedes.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ArchimedesShipsMessage {

    public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side);

    public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player, Side side);

    @SideOnly(Side.CLIENT)
    public abstract void handleClientSide(EntityPlayer player);

    public abstract void handleServerSide(EntityPlayer player);

}
