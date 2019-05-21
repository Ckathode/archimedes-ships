package com.tridevmc.davincisvessels.common;

import com.tridevmc.davincisvessels.common.handler.CommonHookContainer;
import com.tridevmc.davincisvessels.common.handler.CommonPlayerTicker;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
    public CommonPlayerTicker playerTicker;
    public CommonHookContainer hookContainer;

    public CommonHookContainer getHookContainer() {
        return new CommonHookContainer();
    }

    public void registerKeyHandlers() {
    }

    public void registerEventHandlers() {
        playerTicker = new CommonPlayerTicker();
        MinecraftForge.EVENT_BUS.register(playerTicker);
        MinecraftForge.EVENT_BUS.register(hookContainer = getHookContainer());
    }


}
