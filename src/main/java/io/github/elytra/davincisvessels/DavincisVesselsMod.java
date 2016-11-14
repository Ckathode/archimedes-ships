package io.github.elytra.davincisvessels;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import io.github.elytra.davincisvessels.client.ClientProxy;
import io.github.elytra.davincisvessels.common.CommonProxy;
import io.github.elytra.davincisvessels.common.DavincisVesselsConfig;
import io.github.elytra.davincisvessels.common.command.*;
import io.github.elytra.davincisvessels.common.entity.EntityParachute;
import io.github.elytra.davincisvessels.common.entity.EntitySeat;
import io.github.elytra.davincisvessels.common.entity.EntityShip;
import io.github.elytra.davincisvessels.common.handler.ConnectionHandler;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;
import io.github.elytra.davincisvessels.common.object.DavincisVesselsObjects;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

@Mod(modid = DavincisVesselsMod.MOD_ID, name = DavincisVesselsMod.MOD_NAME, version = DavincisVesselsMod.MOD_VERSION, dependencies = "required-after:movingworld", guiFactory = DavincisVesselsMod.MOD_GUIFACTORY)
public class DavincisVesselsMod {

    public static final String MOD_ID = "davincisvessels";
    public static final String MOD_VERSION = "@DVESSELSVER@";
    public static final String MOD_NAME = "Davinci's Vessels";
    public static final String RESOURCE_DOMAIN = "davincisvessels:";
    public static final String MOD_GUIFACTORY = "io.github.elytra.davincisvessels.client.gui.DavincisVesselsGUIFactory";

    @Mod.Instance(MOD_ID)
    public static DavincisVesselsMod INSTANCE;
    @SidedProxy(clientSide = "io.github.elytra.davincisvessels.client.ClientProxy", serverSide = "io.github.elytra.davincisvessels.common.CommonProxy")
    public static CommonProxy PROXY;

    public static final DavincisVesselsObjects OBJECTS = new DavincisVesselsObjects();
    public static Logger LOG;

    public static CreativeTabs CREATIVE_TAB = new CreativeTabs("davincisTab") {
        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(DavincisVesselsObjects.blockMarkShip);
        }
    };

    private DavincisVesselsConfig localConfig;

    public DavincisVesselsConfig getNetworkConfig() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            if (((ClientProxy) PROXY).syncedConfig != null)
                return ((ClientProxy) PROXY).syncedConfig;
        }
        return localConfig;
    }

    public DavincisVesselsConfig getLocalConfig() {
        return localConfig;
    }

    @Mod.EventHandler
    public void preInitMod(FMLPreInitializationEvent event) {
        LOG = event.getModLog();

        MinecraftForge.EVENT_BUS.register(this);

        OBJECTS.preInit(event);

        localConfig = new DavincisVesselsConfig(new Configuration(event.getSuggestedConfigurationFile()));
        localConfig.loadAndSave();

        localConfig.postLoad();
        PROXY.registerRenderers(event.getModState());
    }

    @Mod.EventHandler
    public void initMod(FMLInitializationEvent event) {
        DavincisVesselsNetworking.setupNetwork();
        OBJECTS.init(event);

        MinecraftForge.EVENT_BUS.register(new ConnectionHandler());
        MinecraftForge.EVENT_BUS.register(new ConnectionHandler());

        EntityRegistry.registerModEntity(EntityShip.class, "shipmod", 1, this, 64, localConfig.getShared().shipEntitySyncRate, true);
        EntityRegistry.registerModEntity(EntitySeat.class, "attachment.seat", 2, this, 64, 20, false);
        EntityRegistry.registerModEntity(EntityParachute.class, "parachute", 3, this, 32, localConfig.getShared().shipEntitySyncRate, true);

        PROXY.registerKeyHandlers(localConfig);
        PROXY.registerEventHandlers();
        PROXY.registerRenderers(event.getModState());

        localConfig.addBlacklistWhitelistEntries();
    }

    @Mod.EventHandler
    public void postInitMod(FMLPostInitializationEvent event) {
        PROXY.registerRenderers(event.getModState());
    }

    @Mod.EventHandler
    public void missingMappingsFound(FMLMissingMappingsEvent event) {
        if (event != null && event.getAll() != null && !event.getAll().isEmpty()) {
            ListMultimap<String, FMLMissingMappingsEvent.MissingMapping> missing = ReflectionHelper.getPrivateValue(FMLMissingMappingsEvent.class, event, "missing");
            if (missing != null) {
                List<FMLMissingMappingsEvent.MissingMapping> missingMappingList = ImmutableList.copyOf(missing.get("archimedesshipsplus"));

                if (missingMappingList != null && !missingMappingList.isEmpty()) {
                    Logger log = LogManager.getLogger(MOD_ID);

                    log.info("ARCHIMEDES LEGACY MAPPINGS FOUND");

                    for (FMLMissingMappingsEvent.MissingMapping mapping : missingMappingList) {
                        if (mapping != null && mapping.type != null && mapping.name != null && !mapping.name.isEmpty()) {

                            String name = mapping.name.substring("archimedesshipsplus:".length());

                            if (mapping.type == GameRegistry.Type.BLOCK) {
                                mapping.remap(Block.REGISTRY.getObject(new ResourceLocation(DavincisVesselsMod.MOD_ID, name)));
                            } else {
                                mapping.remap(Item.getItemFromBlock(GameRegistry.findBlock(DavincisVesselsMod.MOD_ID, name)));
                            }

                            log.debug("archimedesshipsplus:" + name + " ~~~> " + DavincisVesselsMod.MOD_ID + name);
                        }
                    }

                    log.info("REMAPPED TO DAVINCI'S VESSELS, ENJOY! ~Darkevilmac");
                }
            }
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        registerASCommand(event, new CommandASHelp());
        registerASCommand(event, new CommandDisassembleShip());
        registerASCommand(event, new CommandShipInfo());
        registerASCommand(event, new CommandDisassembleNear());
        registerASCommand(event, new CommandASTP());
        Collections.sort(CommandASHelp.asCommands);
    }

    private void registerASCommand(FMLServerStartingEvent event, CommandBase commandbase) {
        event.registerServerCommand(commandbase);
        CommandASHelp.asCommands.add(commandbase);
    }

}
