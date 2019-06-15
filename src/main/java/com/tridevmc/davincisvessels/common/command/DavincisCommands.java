package com.tridevmc.davincisvessels.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class DavincisCommands {
    public static void register(FMLServerStartingEvent e) {
        CommandDispatcher<CommandSource> dispatcher = e.getCommandDispatcher();
        CommandDisassembleNear.register(dispatcher);
        CommandDisassembleShip.register(dispatcher);
        CommandDVTP.register(dispatcher);
        CommandShipInfo.register(dispatcher);
    }
}
