package com.tridevmc.davincisvessels.common;

import com.tridevmc.davincisvessels.common.command.DavincisCommands;
import com.tridevmc.davincisvessels.common.handler.CommonHookContainer;
import com.tridevmc.davincisvessels.common.handler.CommonPlayerTicker;
import com.tridevmc.davincisvessels.common.handler.ConnectionHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonProxy {
    public void onSetup(FMLCommonSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new CommonPlayerTicker());
        MinecraftForge.EVENT_BUS.register(new CommonHookContainer());
        MinecraftForge.EVENT_BUS.register(new ConnectionHandler());

        MinecraftForge.EVENT_BUS.addListener(DavincisCommands::register);
    }
}
