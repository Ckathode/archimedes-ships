package darkevilmac.archimedes;

import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.movingworld.network.advanced.MsgRequestShipData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

@SideOnly(Side.CLIENT)
public class ClientHookContainer extends CommonHookContainer {
    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.world.isRemote && event.entity instanceof EntityShip) {
            if (((EntityShip) event.entity).getShipChunk().chunkTileEntityMap.isEmpty()) {
                return;
            }

            MsgRequestShipData msg = new MsgRequestShipData((EntityShip) event.entity);
            ArchimedesShipMod.instance.pipeline.sendToServer(msg);
        }
    }
}
