package darkevilmac.archimedes.common;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.handler.CommonHookContainer;
import darkevilmac.archimedes.common.handler.CommonPlayerTicker;
import darkevilmac.archimedes.client.gui.ASGuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
    public CommonPlayerTicker playerTicker;
    public CommonHookContainer hookContainer;

    public CommonHookContainer getHookContainer() {
        return new CommonHookContainer();
    }

    public void registerKeyHandlers(ArchimedesConfig cfg) {
    }

    public void registerEventHandlers() {
        NetworkRegistry.INSTANCE.registerGuiHandler(ArchimedesShipMod.instance, new ASGuiHandler());

        playerTicker = new CommonPlayerTicker();
        FMLCommonHandler.instance().bus().register(playerTicker);
        MinecraftForge.EVENT_BUS.register(hookContainer = getHookContainer());
    }

    public void registerRenderers(LoaderState.ModState state) {
    }

}
