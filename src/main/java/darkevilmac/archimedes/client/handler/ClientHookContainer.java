package darkevilmac.archimedes.client.handler;

import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.handler.CommonHookContainer;
import darkevilmac.movingworld.MovingWorld;
import darkevilmac.movingworld.common.network.RequestMovingWorldDataMessage;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHookContainer extends CommonHookContainer {

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.world.isRemote && event.entity instanceof EntityShip) {
            if (((EntityShip) event.entity).getMobileChunk().chunkTileEntityMap.isEmpty()) {
                return;
            }

            RequestMovingWorldDataMessage msg = new RequestMovingWorldDataMessage((EntityShip) event.entity);
            MovingWorld.instance.network.sendToServer(msg);
        }
    }

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Post e) {
        if (e.isCanceled())
            return;

    }

}
