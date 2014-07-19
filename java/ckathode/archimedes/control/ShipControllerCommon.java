package ckathode.archimedes.control;

import net.minecraft.entity.player.EntityPlayer;
import ckathode.archimedes.entity.EntityShip;

public class ShipControllerCommon
{
	private int	shipControl	= 0;
	
	public void updateControl(EntityShip ship, EntityPlayer player, int i)
	{
		shipControl = i;
	}
	
	public int getShipControl()
	{
		return shipControl;
	}
}
