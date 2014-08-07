package ckathode.archimedes.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import ckathode.archimedes.ArchimedesShipMod;

public class MsgClientOpenGUI extends ASMessage
{
	public int	guiID;
	
	public MsgClientOpenGUI()
	{
		guiID = 0;
	}
	
	public MsgClientOpenGUI(int id)
	{
		guiID = id;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf)
	{
		buf.writeInt(guiID);
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player)
	{
		guiID = buf.readInt();
	}
	
	@Override
	public void handleClientSide(EntityPlayer player)
	{
	}
	
	@Override
	public void handleServerSide(EntityPlayer player)
	{
		player.openGui(ArchimedesShipMod.instance, guiID, player.worldObj, 0, 0, 0);
	}
	
}
