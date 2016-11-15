package io.github.elytra.davincisvessels.common.control;

import net.minecraft.entity.player.EntityPlayer;

import io.github.elytra.davincisvessels.common.entity.EntityShip;

public class ShipControllerCommon {
    private int shipControl = 0;

    public void updateControl(EntityShip ship, EntityPlayer player, int i) {
        shipControl = i;
    }

    public int getShipControl() {
        return shipControl;
    }
}
