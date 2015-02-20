package ckathode.archimedes.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public abstract class ASMessage {
    public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) throws IOException;

    public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) throws IOException;

    @SideOnly(Side.CLIENT)
    public abstract void handleClientSide(EntityPlayer player);

    public abstract void handleServerSide(EntityPlayer player);
}
