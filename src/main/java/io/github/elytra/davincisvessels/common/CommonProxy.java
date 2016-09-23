package io.github.elytra.davincisvessels.common;

import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.client.gui.DavincisVesselsGuiHandler;
import io.github.elytra.davincisvessels.common.handler.CommonHookContainer;
import io.github.elytra.davincisvessels.common.handler.CommonPlayerTicker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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
