package ckathode.archimedes.entity;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkPosition;
import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.chunk.MobileChunkServer;
import ckathode.archimedes.network.MsgChunkBlockUpdate;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class ShipHandlerServer extends ShipHandlerCommon
{
	private boolean	firstChunkUpdate;
	
	public ShipHandlerServer(EntityShip entityship)
	{
		super(entityship);
		firstChunkUpdate = true;
	}
	
	@Override
	public boolean interact(EntityPlayer player)
	{
		if (ship.riddenByEntity == null)
		{
			player.mountEntity(ship);
			return true;
		} else if (player.ridingEntity == null)
		{
			return ship.getCapabilities().mountEntity(player);
		}
		
		return false;
	}
	
	@Override
	public void onChunkUpdate()
	{
		super.onChunkUpdate();
		Collection<ChunkPosition> list = ((MobileChunkServer) ship.getShipChunk()).getSendQueue();
		if (firstChunkUpdate)
		{
			ship.getCapabilities().spawnSeatEntities();
		} else
		{
			MsgChunkBlockUpdate msg = new MsgChunkBlockUpdate(ship, list);
			ArchimedesShipMod.instance.pipeline.sendToAllAround(msg, new TargetPoint(ship.worldObj.provider.dimensionId, ship.posX, ship.posY, ship.posZ, 64D));
		}
		list.clear();
		firstChunkUpdate = false;
	}
}
