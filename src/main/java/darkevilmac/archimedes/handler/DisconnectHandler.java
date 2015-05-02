package darkevilmac.archimedes.handler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import darkevilmac.archimedes.entity.EntityShip;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;

public class DisconnectHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onClientDisconnectFromServer(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
        System.out.println("Disconnect");
        System.out.println("Disconnect");
        System.out.println("Disconnect");
        System.out.println("Disconnect");
        System.out.println("Disconnect");

        if (event.manager != null) {
            NetHandlerPlayServer handlerPlayServer = ((NetHandlerPlayServer) event.manager.getNetHandler());
            EntityPlayer player = handlerPlayServer.playerEntity;
            if (player != null && player.ridingEntity != null && player.ridingEntity instanceof EntityShip) {
                player.mountEntity(null);
            }
        }
    }
}
