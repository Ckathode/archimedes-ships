package io.github.elytra.davincisvessels.client.control;

import io.github.elytra.davincisvessels.common.network.message.ControlInputMessage;
import net.minecraft.entity.player.EntityPlayer;

import io.github.elytra.davincisvessels.common.control.ShipControllerCommon;
import io.github.elytra.davincisvessels.common.entity.EntityShip;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;

public class ShipControllerClient extends ShipControllerCommon {
    @Override
    public void updateControl(EntityShip ship, EntityPlayer player, int control) {
        super.updateControl(ship, player, control);
        new ControlInputMessage(ship, (byte) control).sendToServer();
    }
}
