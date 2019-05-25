package com.tridevmc.davincisvessels;

import com.tridevmc.compound.config.CompoundConfig;
import com.tridevmc.compound.network.core.CompoundNetwork;
import com.tridevmc.davincisvessels.client.ClientProxy;
import com.tridevmc.davincisvessels.client.gui.DavincisUIHandler;
import com.tridevmc.davincisvessels.common.CommonProxy;
import com.tridevmc.davincisvessels.common.DavincisVesselsBlockConfig;
import com.tridevmc.davincisvessels.common.DavincisVesselsConfig;
import com.tridevmc.davincisvessels.common.content.DavincisVesselsContent;
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

@Mod(DavincisVesselsMod.MOD_ID)
public class DavincisVesselsMod {
    public static final String MOD_ID = "davincisvessels";
    public static final String RESOURCE_DOMAIN = "davincisvessels:";

    public static DavincisVesselsMod INSTANCE;
    public static CommonProxy PROXY;
    public static final Logger LOG = LogManager.getLogger("DavincisVessels");

    public static final DavincisVesselsContent CONTENT = new DavincisVesselsContent();
    public static DavincisVesselsConfig CONFIG;
    public static DavincisVesselsBlockConfig BLOCK_CONFIG;

    public DavincisVesselsMod() {
        DavincisVesselsMod.INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        CONFIG = CompoundConfig.of(DavincisVesselsConfig.class, container, "davincis-main.toml");

        FMLJavaModLoadingContext loadingContext = FMLJavaModLoadingContext.get();
        loadingContext.getModEventBus().addListener(this::onSetup);
        loadingContext.getModEventBus().register(CONTENT);

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> DavincisUIHandler::openGui);
    }

    private void onSetup(FMLCommonSetupEvent e) {
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        BLOCK_CONFIG = CompoundConfig.of(DavincisVesselsBlockConfig.class, container, "davincis-blocks.toml");
        PROXY.onSetup(e);

        CompoundNetwork.createNetwork(container, "davincisvessels");
    }

}
