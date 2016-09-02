package elytra.davincisvessels.client.handler;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import elytra.davincisvessels.DavincisVesselsMod;
import elytra.davincisvessels.common.entity.EntityShip;
import elytra.davincisvessels.common.handler.CommonHookContainer;
import darkevilmac.movingworld.common.network.MovingWorldNetworking;

@SideOnly(Side.CLIENT)
public class ClientHookContainer extends CommonHookContainer {

    public static ResourceLocation PLUS_LOCATION = new ResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN, "/textures/gui/plus.png");

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote && event.getEntity() instanceof EntityShip) {
            if (((EntityShip) event.getEntity()).getMobileChunk().chunkTileEntityMap.isEmpty()) {
                return;
            }

            MovingWorldNetworking.NETWORK.send().packet("RequestMovingWorldDataMessage")
                    .with("dimID", event.getWorld().provider.getDimension())
                    .with("entityID", event.getEntity().getEntityId()).toServer();
        }
    }

}
