package ckathode.archimedes.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import ckathode.archimedes.entity.EntityShip;

public class MsgFarInteract extends ASMessageShip
{
	public MsgFarInteract()
	{
		super();
	}
	
	public MsgFarInteract(EntityShip entityship)
	{
		super(entityship);
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) throws IOException
	{
		super.encodeInto(ctx, buf);
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) throws IOException
	{
		super.decodeInto(ctx, buf, player);
	}
	
	@Override
	public void handleClientSide(EntityPlayer player)
	{
	}
	
	@Override
	public void handleServerSide(EntityPlayer player)
	{
		if (ship != null)
		{
			player.interactWith(ship);
		}
	}
	
}
