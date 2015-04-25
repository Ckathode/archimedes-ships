package darkevilmac.archimedes;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.movingworld.MovingWorld;
import darkevilmac.movingworld.network.RequestMovingWorldDataMessage;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

@SideOnly(Side.CLIENT)
public class ClientHookContainer extends CommonHookContainer {

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.world.isRemote && event.entity instanceof EntityShip) {
            if (((EntityShip) event.entity).getMovingWorldChunk().chunkTileEntityMap.isEmpty()) {
                return;
            }

            RequestMovingWorldDataMessage msg = new RequestMovingWorldDataMessage((EntityShip) event.entity);
            MovingWorld.instance.network.sendToServer(msg);
        }
    }

}
