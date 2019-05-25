package com.tridevmc.davincisvessels.client.handler;

import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.handler.CommonHookContainer;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import com.tridevmc.movingworld.common.network.message.MovingWorldDataRequestMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientHookContainer extends CommonHookContainer {

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote && event.getEntity() instanceof EntityShip) {
            if (((EntityShip) event.getEntity()).getMobileChunk().chunkTileEntityMap.isEmpty()) {
                return;
            }

            new MovingWorldDataRequestMessage((EntityMovingWorld) event.getEntity()).sendToServer();
        }
    }
}
