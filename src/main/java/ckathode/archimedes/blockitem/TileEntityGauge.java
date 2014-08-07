package ckathode.archimedes.blockitem;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import ckathode.archimedes.entity.EntityShip;
import ckathode.archimedes.entity.IShipTileEntity;

public class TileEntityGauge extends TileEntity implements IShipTileEntity
{
	public EntityShip	parentShip;
	
	public TileEntityGauge()
	{
		parentShip = null;
	}
	
	@Override
	public boolean canUpdate()
	{
		return false;
	}
	
	@Override
	public void setParentShip(EntityShip entityvehicle, int x, int y, int z)
	{
		parentShip = entityvehicle;
	}
	
	@Override
	public EntityShip getParentShip()
	{
		return parentShip;
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound compound = new NBTTagCompound();
		writeToNBT(compound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
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
		if (compound.hasKey("vehicle") && worldObj != null)
		{
			int id = compound.getInteger("vehicle");
			Entity entity = worldObj.getEntityByID(id);
			if (entity instanceof EntityShip)
			{
				parentShip = (EntityShip) entity;
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		compound.setInteger("meta", blockMetadata);
		if (parentShip != null && !parentShip.isDead)
		{
			compound.setInteger("vehicle", parentShip.getEntityId());
		}
	}
	
}
