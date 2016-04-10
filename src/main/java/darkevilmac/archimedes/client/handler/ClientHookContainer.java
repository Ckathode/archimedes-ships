package darkevilmac.archimedes.client.handler;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.handler.CommonHookContainer;
import darkevilmac.movingworld.MovingWorld;
import darkevilmac.movingworld.common.network.RequestMovingWorldDataMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHookContainer extends CommonHookContainer {

    public static ResourceLocation PLUS_LOCATION = new ResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN, "/textures/gui/plus.png");

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote && event.getEntity() instanceof EntityShip) {
            if (((EntityShip) event.getEntity()).getMobileChunk().chunkTileEntityMap.isEmpty()) {
                return;
            }

            RequestMovingWorldDataMessage msg = new RequestMovingWorldDataMessage((EntityShip) event.getEntity());
            MovingWorld.instance.network.sendToServer(msg);
        }
    }

}
