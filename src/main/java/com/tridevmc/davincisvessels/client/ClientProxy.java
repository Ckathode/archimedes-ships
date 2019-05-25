package com.tridevmc.davincisvessels.client;

import com.tridevmc.compound.config.CompoundConfig;
import com.tridevmc.davincisvessels.client.control.DavincisKeybinds;
import com.tridevmc.davincisvessels.client.control.ShipKeyHandler;
import com.tridevmc.davincisvessels.client.handler.ClientHookContainer;
import com.tridevmc.davincisvessels.client.render.RenderParachute;
import com.tridevmc.davincisvessels.client.render.RenderSeat;
import com.tridevmc.davincisvessels.client.render.TileEntityGaugeRenderer;
import com.tridevmc.davincisvessels.client.render.TileEntityHelmRenderer;
import com.tridevmc.davincisvessels.common.CommonProxy;
import com.tridevmc.davincisvessels.common.entity.EntityParachute;
import com.tridevmc.davincisvessels.common.entity.EntitySeat;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.tileentity.TileGauge;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import com.tridevmc.movingworld.client.render.RenderMovingWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ClientProxy extends CommonProxy {

    public DavincisKeybinds keybinds;

    @Override
    public void onSetup(FMLCommonSetupEvent e) {
        super.onSetup(e);
        MinecraftForge.EVENT_BUS.register(new ClientHookContainer());
        registerKeyHandlers();
        registerRenderers();
    }

    private void registerKeyHandlers() {
        keybinds = CompoundConfig.of(DavincisKeybinds.class, ModLoadingContext.get().getActiveContainer());
        keybinds.addToControlsMenu();
        MinecraftForge.EVENT_BUS.register(new ShipKeyHandler(keybinds));
    }

    private void registerRenderers() {
        registerEntityRenderers();
        registerTileRenderers();
    }

    private void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityShip.class, RenderMovingWorld::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySeat.class, RenderSeat::new);
    }

    private void registerTileRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileGauge.class, new TileEntityGaugeRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileHelm.class, new TileEntityHelmRenderer());
    }

}
