package darkevilmac.archimedes.control;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.archimedes.network.ControlInputMessage;
import net.minecraft.entity.player.EntityPlayer;

public class ShipControllerClient extends ShipControllerCommon {
    @Override
    public void updateControl(EntityShip ship, EntityPlayer player, int i) {
        super.updateControl(ship, player, i);
        ControlInputMessage message = new ControlInputMessage(ship, i);
        ArchimedesShipMod.instance.network.sendToServer(message);
    }
}
