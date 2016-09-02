package elytra.davincisvessels.client.control;

import net.minecraft.entity.player.EntityPlayer;

import elytra.davincisvessels.common.control.ShipControllerCommon;
import elytra.davincisvessels.common.entity.EntityShip;
import elytra.davincisvessels.common.network.DavincisVesselsNetworking;

public class ShipControllerClient extends ShipControllerCommon {
    @Override
    public void updateControl(EntityShip ship, EntityPlayer player, int control) {
        super.updateControl(ship, player, control);
        DavincisVesselsNetworking.NETWORK.send().packet("ControlInputMessage")
                .with("dimID", ship.worldObj.provider.getDimension())
                .with("entityID", ship.getEntityId())
                .with("control", control).toServer();
    }
}
