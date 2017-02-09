package com.elytradev.davincisvessels.client.handler;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.elytradev.davincisvessels.common.handler.CommonHookContainer;
import com.elytradev.movingworld.common.entity.EntityMovingWorld;
import com.elytradev.movingworld.common.network.message.MovingWorldDataRequestMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHookContainer extends CommonHookContainer {

    public static ResourceLocation PLUS_LOCATION = new ResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN, "/textures/gui/plus.png");

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
