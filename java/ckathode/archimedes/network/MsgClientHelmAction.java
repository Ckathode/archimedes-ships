package ckathode.archimedes.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import ckathode.archimedes.blockitem.TileEntityHelm;

public class MsgClientHelmAction extends ASMessage
{
	public TileEntityHelm	tileEntity;
	public int				actionID;
	
	public MsgClientHelmAction()
	{
		tileEntity = null;
		actionID = -1;
	}
	
	public MsgClientHelmAction(TileEntityHelm tileentity, int id)
	{
		tileEntity = tileentity;
		actionID = id;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf)
	{
		buf.writeByte(actionID);
		buf.writeInt(tileEntity.xCoord);
		buf.writeInt(tileEntity.yCoord);
		buf.writeInt(tileEntity.zCoord);
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player)
	{
		actionID = buf.readByte();
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		TileEntity te = player.worldObj.getTileEntity(x, y, z);
		if (te instanceof TileEntityHelm)
		{
			tileEntity = (TileEntityHelm) te;
		}
	}
	
	@Override
	public void handleClientSide(EntityPlayer player)
	{
	}
	
	@Override
	public void handleServerSide(EntityPlayer player)
	{
		if (tileEntity != null)
		{
			switch (actionID)
			{
			case 0:
				tileEntity.assembleShip(player);
				break;
			case 1:
				tileEntity.mountShip(player);
				break;
			case 2:
				tileEntity.undoCompilation(player);
				break;
			default:
				break;
			}
		}
	}
}
