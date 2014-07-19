package ckathode.archimedes.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import ckathode.archimedes.blockitem.TileEntityHelm;
import ckathode.archimedes.chunk.MobileChunkClient;
import ckathode.archimedes.entity.EntityShip;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MsgTileEntities extends ASMessageShip
{
	private NBTTagCompound	tagCompound;
	
	public MsgTileEntities()
	{
		super();
		tagCompound = null;
	}
	
	public MsgTileEntities(EntityShip entityship)
	{
		super(entityship);
		tagCompound = null;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) throws IOException
	{
		super.encodeInto(ctx, buf);
		tagCompound = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (TileEntity te : ship.getShipChunk().chunkTileEntityMap.values())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			if (te instanceof TileEntityHelm)
			{
				((TileEntityHelm) te).writeNBTforSending(nbt);
			} else
			{
				te.writeToNBT(nbt);
			}
			list.appendTag(nbt);
		}
		tagCompound.setTag("list", list);
		DataOutputStream out = new DataOutputStream(new ByteBufOutputStream(buf));
		try
		{
			CompressedStreamTools.write(tagCompound, out);
			out.flush();
		} catch (IOException e)
		{
			throw e;
		} finally
		{
			out.close();
		}
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) throws IOException
	{
		super.decodeInto(ctx, buf, player);
		if (ship != null)
		{
			DataInputStream in = new DataInputStream(new ByteBufInputStream(buf));
			try
			{
				tagCompound = CompressedStreamTools.read(in);
			} catch (IOException e)
			{
				throw e;
			} finally
			{
				in.close();
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer player)
	{
		if (ship != null && tagCompound != null)
		{
			NBTTagList list = tagCompound.getTagList("list", 10);
			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound nbt = list.getCompoundTagAt(i);
				if (nbt == null) continue;
				int x = nbt.getInteger("x");
				int y = nbt.getInteger("y");
				int z = nbt.getInteger("z");
				try
				{
					TileEntity te = ship.getShipChunk().getTileEntity(x, y, z);
					te.readFromNBT(nbt);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			((MobileChunkClient) ship.getShipChunk()).getRenderer().markDirty();
		}
	}
	
	@Override
	public void handleServerSide(EntityPlayer player)
	{
	}
	
}
