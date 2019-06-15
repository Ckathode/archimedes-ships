package com.tridevmc.davincisvessels.client.control;

import com.tridevmc.davincisvessels.common.control.ShipControllerCommon;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.network.message.ControlInputMessage;
import net.minecraft.entity.player.PlayerEntity;

public class ShipControllerClient extends ShipControllerCommon {
    @Override
    public void updateControl(EntityShip ship, PlayerEntity player, int control) {
        super.updateControl(ship, player, control);
        new ControlInputMessage(ship, control).sendToServer();
    }
}
