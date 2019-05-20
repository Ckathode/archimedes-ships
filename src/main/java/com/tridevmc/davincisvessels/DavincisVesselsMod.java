package com.tridevmc.davincisvessels;

import com.tridevmc.compound.config.CompoundConfig;
import com.tridevmc.compound.network.core.CompoundNetwork;
import com.tridevmc.davincisvessels.client.gui.DavincisUIHandler;
import com.tridevmc.davincisvessels.common.DavincisVesselsConfig;
import com.tridevmc.davincisvessels.common.command.*;
import com.tridevmc.davincisvessels.common.content.DavincisVesselsContent;
import com.tridevmc.davincisvessels.common.entity.EntityParachute;
import com.tridevmc.davincisvessels.common.entity.EntitySeat;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.handler.ConnectionHandler;
import com.tridevmc.movingworld.client.ClientProxy;
import com.tridevmc.movingworld.common.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

@Mod(DavincisVesselsMod.MOD_ID)
public class DavincisVesselsMod {
    public static final String MOD_ID = "davincisvessels";
    public static final String RESOURCE_DOMAIN = "davincisvessels:";

    public static DavincisVesselsMod INSTANCE;
    public static CommonProxy PROXY;
    public static final Logger LOG = LogManager.getLogger("DavincisVessels");

    public static final DavincisVesselsContent CONTENT = new DavincisVesselsContent();
    public static DavincisVesselsConfig CONFIG;

    public DavincisVesselsMod() {
        DavincisVesselsMod.INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        MinecraftForge.EVENT_BUS.register(PROXY);

        FMLJavaModLoadingContext loadingContext = FMLJavaModLoadingContext.get();
        loadingContext.getModEventBus().addListener(this::onSetup);
        loadingContext.getModEventBus().register(CONTENT);

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> DavincisUIHandler::openGui);
    }

    private void onSetup(FMLCommonSetupEvent e) {
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        CompoundNetwork.createNetwork(container, "davincisvessels");

        CONFIG = CompoundConfig.of(DavincisVesselsConfig.class, container, "davincis-main.toml");
    }

    @Mod.EventHandler
    public void preInitMod(FMLPreInitializationEvent event) {
        LOG = event.getModLog();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PROXY);
        MinecraftForge.EVENT_BUS.register(CONTENT);

        CONTENT.preInit(event);

        localConfig = new DavincisVesselsConfig(new Configuration(event.getSuggestedConfigurationFile()));
        localConfig.loadAndSave();
        EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, "ship"), EntityShip.class, "shipmod", 1, this, 64, localConfig.getShared().shipEntitySyncRate, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, "seat"), EntitySeat.class, "attachment.seat", 2, this, 64, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, "parachute"), EntityParachute.class, "parachute", 3, this, 32, localConfig.getShared().shipEntitySyncRate, true);
        PROXY.registerRenderers(event.getModState());
    }

    @Mod.EventHandler
    public void initMod(FMLInitializationEvent event) {
        DavincisVesselsNetworking.setupNetwork();
        CONTENT.init(event);

        MinecraftForge.EVENT_BUS.register(new ConnectionHandler());
        PROXY.registerKeyHandlers(localConfig);
        PROXY.registerEventHandlers();
        PROXY.registerRenderers(event.getModState());

        localConfig.postLoad();
        localConfig.addBlacklistWhitelistEntries();
    }

    @Mod.EventHandler
    public void postInitMod(FMLPostInitializationEvent event) {
        PROXY.registerRenderers(event.getModState());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        registerASCommand(event, new CommandDVHelp());
        registerASCommand(event, new CommandDisassembleShip());
        registerASCommand(event, new CommandShipInfo());
        registerASCommand(event, new CommandDisassembleNear());
        registerASCommand(event, new CommandDVTP());
        Collections.sort(CommandDVHelp.asCommands);
    }

    private void registerASCommand(FMLServerStartingEvent event, CommandBase commandbase) {
        event.registerServerCommand(commandbase);
        CommandDVHelp.asCommands.add(commandbase);
    }

}
