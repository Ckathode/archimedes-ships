package com.elytradev.davincisvessels.client.control;

import com.elytradev.davincisvessels.common.control.ShipControllerCommon;
import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.elytradev.davincisvessels.common.network.message.ControlInputMessage;
import net.minecraft.entity.player.EntityPlayer;

public class ShipControllerClient extends ShipControllerCommon {
    @Override
    public void updateControl(EntityShip ship, EntityPlayer player, int control) {
        super.updateControl(ship, player, control);
        new ControlInputMessage(ship, control).sendToServer();
    }
}
