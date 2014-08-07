package ckathode.archimedes.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import ckathode.archimedes.chunk.AssembleResult;
import ckathode.archimedes.gui.ContainerHelm;

public class MsgAssembleResult extends ASMessage
{
	public AssembleResult	result;
	public boolean			prevFlag;
	
	public MsgAssembleResult()
	{
		result = null;
		prevFlag = false;
	}
	
	public MsgAssembleResult(AssembleResult compileresult, boolean prev)
	{
		result = compileresult;
		prevFlag = prev;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf)
	{
		buf.writeBoolean(prevFlag);
		if (result == null)
		{
			buf.writeByte(AssembleResult.RESULT_NONE);
		} else
		{
			buf.writeByte(result.getCode());
			buf.writeInt(result.getBlockCount());
			buf.writeInt(result.getBalloonCount());
			buf.writeInt(result.getTileEntityCount());
			buf.writeFloat(result.getMass());
		}
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player)
	{
		prevFlag = buf.readBoolean();
		result = new AssembleResult(buf);
	}
	
	@Override
	public void handleClientSide(EntityPlayer player)
	{
		if (player.openContainer instanceof ContainerHelm)
		{
			if (prevFlag)
			{
				((ContainerHelm) player.openContainer).tileEntity.setPrevAssembleResult(result);
			} else
			{
				((ContainerHelm) player.openContainer).tileEntity.setAssembleResult(result);
			}
		}
	}
	
	@Override
	public void handleServerSide(EntityPlayer player)
	{
	}
	
}
