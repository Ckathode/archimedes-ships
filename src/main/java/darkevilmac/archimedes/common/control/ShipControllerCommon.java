package darkevilmac.archimedes.common.control;

import net.minecraft.entity.player.EntityPlayer;

import darkevilmac.archimedes.common.entity.EntityShip;

public class ShipControllerCommon {
    private int shipControl = 0;

    public void updateControl(EntityShip ship, EntityPlayer player, int i) {
        shipControl = i;
    }

    public int getShipControl() {
        return shipControl;
    }
}
