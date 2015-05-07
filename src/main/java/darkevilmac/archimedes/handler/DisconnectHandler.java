package darkevilmac.archimedes.handler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class DisconnectHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onClientDisconnectFromServer(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
        //if (event.manager != null) {
        //    NetHandlerPlayServer handlerPlayServer = ((NetHandlerPlayServer) event.manager.getNetHandler());
        //    EntityPlayer player = handlerPlayServer.playerEntity;
        //    if (player != null && player.ridingEntity != null && player.ridingEntity instanceof EntitySeat) {
        //        player.mountEntity(null);
        //    }
        //}
    }
}
