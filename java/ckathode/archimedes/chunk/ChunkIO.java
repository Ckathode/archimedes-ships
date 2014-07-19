package ckathode.archimedes.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import ckathode.archimedes.ArchimedesShipMod;

public abstract class ChunkIO
{
	public static void write(DataOutput out, MobileChunk chunk, Collection<ChunkPosition> blocks) throws IOException
	{
		out.writeShort(blocks.size());
		for (ChunkPosition p : blocks)
		{
			writeBlock(out, chunk, p.chunkPosX, p.chunkPosY, p.chunkPosZ);
		}
	}
	
	public static int writeAll(DataOutput out, MobileChunk chunk) throws IOException
	{
		int count = 0;
		for (int i = chunk.minX(); i < chunk.maxX(); i++)
		{
			for (int j = chunk.minY(); j < chunk.maxY(); j++)
			{
				for (int k = chunk.minZ(); k < chunk.maxZ(); k++)
				{
					Block block = chunk.getBlock(i, j, k);
					if (block != Blocks.air)
					{
						count++;
					}
				}
			}
		}
		ArchimedesShipMod.modLog.debug("Writing mobile chunk data: " + count + " blocks");
		
		out.writeShort(count);
		for (int i = chunk.minX(); i < chunk.maxX(); i++)
		{
			for (int j = chunk.minY(); j < chunk.maxY(); j++)
			{
				for (int k = chunk.minZ(); k < chunk.maxZ(); k++)
				{
					Block block = chunk.getBlock(i, j, k);
					if (block != Blocks.air)
					{
						writeBlock(out, Block.getIdFromBlock(block), chunk.getBlockMetadata(i, j, k), i, j, k);
					}
				}
			}
		}
		
		return count;
		
	}
	
	public static void writeBlock(DataOutput out, MobileChunk chunk, int x, int y, int z) throws IOException
	{
		writeBlock(out, Block.getIdFromBlock(chunk.getBlock(x, y, z)), chunk.getBlockMetadata(x, y, z), x, y, z);
	}
	
	public static void writeBlock(DataOutput out, int id, int meta, int x, int y, int z) throws IOException
	{
		out.writeByte(x);
		out.writeByte(y);
		out.writeByte(z);
		out.writeShort(id);
		out.writeInt(meta);
	}
	
	public static void read(DataInput in, MobileChunk chunk) throws IOException
	{
		int count = in.readShort();
		
		ArchimedesShipMod.modLog.debug("Reading mobile chunk data: " + count + " blocks");
		
		int x, y, z;
		int id;
		int meta;
		for (int i = 0; i < count; i++)
		{
			x = in.readByte();
			y = in.readByte();
			z = in.readByte();
			id = in.readShort();
			meta = in.readInt();
			chunk.setBlockIDWithMetadata(x, y, z, Block.getBlockById(id), meta);
		}
	}
	
	public static void writeCompressed(ByteBuf buf, MobileChunk chunk, Collection<ChunkPosition> blocks) throws IOException
	{
		DataOutputStream out = preCompress(buf);
		write(out, chunk, blocks);
		postCompress(buf, out, blocks.size());
	}
	
	public static void writeAllCompressed(ByteBuf buf, MobileChunk chunk) throws IOException
	{
		DataOutputStream out = preCompress(buf);
		int count = writeAll(out, chunk);
		postCompress(buf, out, count);
	}
	
	private static DataOutputStream preCompress(ByteBuf data) throws IOException
	{
		ByteBufOutputStream bbos = new ByteBufOutputStream(data);
		DataOutputStream out = new DataOutputStream(new GZIPOutputStream(bbos));
		return out;
	}
	
	private static void postCompress(ByteBuf data, DataOutputStream out, int count) throws IOException
	{
		out.flush();
		out.close();
		
		int byteswritten = data.writerIndex();
		float f = (float) byteswritten / (count * 9);
		ArchimedesShipMod.modLog.debug(String.format(Locale.ENGLISH, "%d blocks written. Efficiency: %d/%d = %.2f", count, byteswritten, count * 9, f));
		
		if (byteswritten > 32000)
		{
			ArchimedesShipMod.modLog.warn("Ship probably contains too many blocks");
		}
	}
	
	public static void readCompressed(ByteBuf data, MobileChunk chunk) throws IOException
	{
		DataInputStream in = new DataInputStream(new GZIPInputStream(new ByteBufInputStream(data)));
		read(in, chunk);
		in.close();
	}
}
