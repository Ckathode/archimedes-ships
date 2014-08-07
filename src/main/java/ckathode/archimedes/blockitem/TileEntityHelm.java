package ckathode.archimedes.blockitem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.chunk.AssembleResult;
import ckathode.archimedes.chunk.ChunkAssembler;
import ckathode.archimedes.entity.EntityShip;
import ckathode.archimedes.entity.IShipTileEntity;
import ckathode.archimedes.entity.ShipInfo;
import ckathode.archimedes.network.MsgAssembleResult;

public class TileEntityHelm extends TileEntity implements IShipTileEntity
{
	private EntityShip	activeShip;
	private ShipInfo	info;
	private AssembleResult	assembleResult, prevResult;
	
	public TileEntityHelm()
	{
		super();
		info = new ShipInfo();
		activeShip = null;
		assembleResult = prevResult = null;
	}
	
	@Override
	public boolean canUpdate()
	{
		return false;
	}
	
	public ShipInfo getShipInfo()
	{
		return info;
	}
	
	public AssembleResult getAssembleResult()
	{
		return assembleResult;
	}
	
	public AssembleResult getPrevAssembleResult()
	{
		return prevResult;
	}
	
	public void setShipInfo(ShipInfo shipinfo)
	{
		if (shipinfo == null) throw new NullPointerException("Cannot set null ship info");
		info = shipinfo;
	}
	
	public void setAssembleResult(AssembleResult result)
	{
		assembleResult = result;
	}
	
	public void setPrevAssembleResult(AssembleResult result)
	{
		prevResult = result;
	}
	
	@Override
	public void setParentShip(EntityShip entityship, int x, int y, int z)
	{
		activeShip = entityship;
	}
	
	@Override
	public EntityShip getParentShip()
	{
		return activeShip;
	}
	
	public boolean assembleShip(EntityPlayer player)
	{
		if (!worldObj.isRemote)
		{
			prevResult = assembleResult;
			ChunkAssembler assembler = new ChunkAssembler(worldObj, xCoord, yCoord, zCoord);
			assembleResult = assembler.doAssemble();
			
			sendAssembleResult(player, false);
			sendAssembleResult(player, true);
			
			ChatComponentText c;
			switch (assembleResult.getCode())
			{
			case AssembleResult.RESULT_OK:
			case AssembleResult.RESULT_OK_WITH_WARNINGS:
				return true;
			case AssembleResult.RESULT_BLOCK_OVERFLOW:
				c = new ChatComponentText("Cannot create ship with more than " + ArchimedesShipMod.instance.modConfig.maxShipChunkBlocks + " blocks");
				player.addChatMessage(c);
				break;
			case AssembleResult.RESULT_MISSING_MARKER:
				c = new ChatComponentText("Cannot create ship with no ship marker");
				player.addChatMessage(c);
				break;
			case AssembleResult.RESULT_ERROR_OCCURED:
				c = new ChatComponentText("An error occured while assembling ship. See console log for details.");
				player.addChatMessage(c);
				break;
			case AssembleResult.RESULT_NONE:
				c = new ChatComponentText("Nothing was assembled");
				player.addChatMessage(c);
				break;
			default:
			}
		}
		return false;
	}
	
	public boolean mountShip(EntityPlayer player)
	{
		if (!worldObj.isRemote)
		{
			if (assembleResult != null && assembleResult.isOK())
			{
				assembleResult.checkConsistent(worldObj);
				sendAssembleResult(player, false);
				if (assembleResult.getCode() == AssembleResult.RESULT_INCONSISTENT)
				{
					return false;
				}
				if (assembleResult.getCode() == AssembleResult.RESULT_OK_WITH_WARNINGS)
				{
					IChatComponent c = new ChatComponentText("Ship contains changes");
					player.addChatMessage(c);
				}
				
				EntityShip entity = assembleResult.getEntity(worldObj);
				if (entity != null)
				{
					entity.setInfo(info);
					if (worldObj.spawnEntityInWorld(entity))
					{
						player.mountEntity(entity);
						assembleResult = null;
						//entity.getCapabilities().mountEntity(entityplayer);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void undoCompilation(EntityPlayer player)
	{
		assembleResult = prevResult;
		prevResult = null;
		
		sendAssembleResult(player, false);
		sendAssembleResult(player, true);
	}
	
	public void sendAssembleResult(EntityPlayer player, boolean prev)
	{
		if (!worldObj.isRemote)
		{
			AssembleResult res;
			if (prev)
			{
				res = prevResult;
			} else
			{
				res = assembleResult;
			}
			MsgAssembleResult msg = new MsgAssembleResult(res, prev);
			ArchimedesShipMod.instance.pipeline.sendTo(msg, (EntityPlayerMP) player);
		}
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound compound = new NBTTagCompound();
		writeNBTforSending(compound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
		readFromNBT(packet.func_148857_g());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		blockMetadata = compound.getInteger("meta");
		info.shipName = compound.getString("name");
		if (compound.hasKey("ship") && worldObj != null)
		{
			int id = compound.getInteger("ship");
			Entity entity = worldObj.getEntityByID(id);
			if (entity instanceof EntityShip)
			{
				activeShip = (EntityShip) entity;
			}
		}
		if (compound.hasKey("res"))
		{
			assembleResult = new AssembleResult(compound.getCompoundTag("res"), worldObj);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		compound.setInteger("meta", blockMetadata);
		compound.setString("name", info.shipName);
		if (activeShip != null && !activeShip.isDead)
		{
			compound.setInteger("ship", activeShip.getEntityId());
		}
		if (assembleResult != null)
		{
			NBTTagCompound comp = new NBTTagCompound();
			assembleResult.writeNBTFully(comp);
			compound.setTag("res", comp);
		}
	}
	
	public void writeNBTforSending(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		compound.setInteger("meta", blockMetadata);
		compound.setString("name", info.shipName);
		if (activeShip != null && !activeShip.isDead)
		{
			compound.setInteger("ship", activeShip.getEntityId());
		}
		if (assembleResult != null)
		{
			NBTTagCompound comp = new NBTTagCompound();
			assembleResult.writeNBTMetadata(comp);
			compound.setTag("res", comp);
		}
	}
}
