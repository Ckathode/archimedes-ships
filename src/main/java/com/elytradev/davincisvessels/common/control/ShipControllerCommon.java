package com.elytradev.davincisvessels.common.control;

import com.elytradev.davincisvessels.common.entity.EntityShip;
import net.minecraft.entity.player.EntityPlayer;

public class ShipControllerCommon {
    private int shipControl = 0;

    public void updateControl(EntityShip ship, EntityPlayer player, int i) {
        shipControl = i;
    }

    public int getShipControl() {
        return shipControl;
    }
}
