package io.github.elytra.davincisvessels.client.control;

import net.minecraft.entity.player.EntityPlayer;

import io.github.elytra.davincisvessels.common.control.ShipControllerCommon;
import io.github.elytra.davincisvessels.common.entity.EntityShip;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;

public class ShipControllerClient extends ShipControllerCommon {
    @Override
    public void updateControl(EntityShip ship, EntityPlayer player, int control) {
        super.updateControl(ship, player, control);
        DavincisVesselsNetworking.NETWORK.send().packet("ControlInputMessage")
                .with("dimID", ship.world.provider.getDimension())
                .with("entityID", ship.getEntityId())
                .with("control", control).toServer();
    }
}
