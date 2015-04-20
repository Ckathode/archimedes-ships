package darkevilmac.archimedes.control;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.archimedes.network.MsgControlInput;
import net.minecraft.entity.player.EntityPlayer;

public class ShipControllerClient extends ShipControllerCommon {
    @Override
    public void updateControl(EntityShip ship, EntityPlayer player, int i) {
        super.updateControl(ship, player, i);
        MsgControlInput msg = new MsgControlInput(ship, i);
        ArchimedesShipMod.instance.pipeline.sendToServer(msg);
    }
}
