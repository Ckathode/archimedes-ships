package ckathode.archimedes.control;

import net.minecraft.entity.player.EntityPlayer;
import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.entity.EntityShip;
import ckathode.archimedes.network.MsgControlInput;

public class ShipControllerClient extends ShipControllerCommon
{
	@Override
	public void updateControl(EntityShip ship, EntityPlayer player, int i)
	{
		super.updateControl(ship, player, i);
		MsgControlInput msg = new MsgControlInput(ship, i);
		ArchimedesShipMod.instance.pipeline.sendToServer(msg);
	}
}
