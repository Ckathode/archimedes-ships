package com.tridevmc.davincisvessels.common.control;

import com.tridevmc.davincisvessels.common.entity.EntityShip;
import net.minecraft.entity.player.PlayerEntity;

public class ShipControllerCommon {
    private int shipControl = 0;

    public void updateControl(EntityShip ship, PlayerEntity player, int i) {
        shipControl = i;
    }

    public int getShipControl() {
        return shipControl;
    }
}
