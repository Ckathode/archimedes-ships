package elytra.davincisvessels.common;

import elytra.davincisvessels.common.handler.CommonPlayerTicker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import elytra.davincisvessels.DavincisVesselsMod;
import elytra.davincisvessels.client.gui.DavincisVesselsGuiHandler;
import elytra.davincisvessels.common.handler.CommonHookContainer;

public class CommonProxy {
    public CommonPlayerTicker playerTicker;
    public CommonHookContainer hookContainer;

    public CommonHookContainer getHookContainer() {
        return new CommonHookContainer();
    }

    public void registerKeyHandlers(DavincisVesselsConfig cfg) {
    }

    public void registerEventHandlers() {
        NetworkRegistry.INSTANCE.registerGuiHandler(DavincisVesselsMod.instance, new DavincisVesselsGuiHandler());

        playerTicker = new CommonPlayerTicker();
        MinecraftForge.EVENT_BUS.register(playerTicker);
        MinecraftForge.EVENT_BUS.register(hookContainer = getHookContainer());
    }

    public void registerRenderers(LoaderState.ModState state) {
    }

}
