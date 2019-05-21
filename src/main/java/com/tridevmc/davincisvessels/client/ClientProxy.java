package com.tridevmc.davincisvessels.client;

import com.tridevmc.compound.config.CompoundConfig;
import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.client.control.DavincisKeybinds;
import com.tridevmc.davincisvessels.client.control.ShipKeyHandler;
import com.tridevmc.davincisvessels.client.handler.ClientHookContainer;
import com.tridevmc.davincisvessels.client.render.RenderParachute;
import com.tridevmc.davincisvessels.client.render.RenderSeat;
import com.tridevmc.davincisvessels.client.render.TileEntityGaugeRenderer;
import com.tridevmc.davincisvessels.client.render.TileEntityHelmRenderer;
import com.tridevmc.davincisvessels.common.CommonProxy;
import com.tridevmc.davincisvessels.common.content.DavincisVesselsContent;
import com.tridevmc.davincisvessels.common.entity.EntityParachute;
import com.tridevmc.davincisvessels.common.entity.EntitySeat;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.tileentity.TileGauge;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import com.tridevmc.movingworld.client.render.RenderMovingWorld;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class ClientProxy extends CommonProxy {

    public ShipKeyHandler shipKeyHandler;
    public DavincisKeybinds keybinds;

    @Override
    public ClientHookContainer getHookContainer() {
        return new ClientHookContainer();
    }

    @Override
    public void registerKeyHandlers() {
        keybinds = CompoundConfig.of(DavincisKeybinds.class, ModLoadingContext.get().getActiveContainer());
        keybinds.addToControlsMenu();
        shipKeyHandler = new ShipKeyHandler(keybinds);
        MinecraftForge.EVENT_BUS.register(shipKeyHandler);
    }

    @Override
    public void registerRenderers(LoaderState.ModState state) {
        if (state == LoaderState.ModState.PREINITIALIZED) {
            registerEntityRenderers();
        }

        if (state == LoaderState.ModState.INITIALIZED) {
            registerTileRenderers();
            registerStandardItemRenders();
        }
    }

    public void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityShip.class, RenderMovingWorld::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySeat.class, RenderSeat::new);
    }

    public void registerTileRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileGauge.class, new TileEntityGaugeRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileHelm.class, new TileEntityHelmRenderer());
    }

    public void registerVariantItemRenders() {
        Item itemToRegister;
        ModelResourceLocation modelResourceLocation;

        // Some specific meta registrations for CONTENT, like for extended gauges.
        itemToRegister = Item.getItemFromBlock(DavincisVesselsContent.blockGauge);
        modelResourceLocation = new ModelResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "gauge", "inventory");
        ModelLoader.setCustomModelResourceLocation(itemToRegister, 0, modelResourceLocation);
        modelResourceLocation = new ModelResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "gauge_ext", "inventory");
        ModelLoader.setCustomModelResourceLocation(itemToRegister, 1, modelResourceLocation);

        itemToRegister = Item.getItemFromBlock(DavincisVesselsContent.blockBalloon);

        for (EnumDyeColor color : EnumDyeColor.values()) {
            int i = color.getMetadata();
            modelResourceLocation = new ModelResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "balloon_" + color.getName(), "inventory");
            ModelLoader.setCustomModelResourceLocation(itemToRegister, i, modelResourceLocation);
            DavincisVesselsMod.LOG.info("Registering balloon model variant: " + i + " " + modelResourceLocation);
        }
    }

    public void registerStandardItemRenders() {
        Item itemToRegister = null;
        ModelResourceLocation modelResourceLocation = null;

        // Do some general render registrations for CONTENT, not considering meta.
        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        for (Map.Entry<String, Block> entry : DavincisVesselsContent.registeredBlocks.entrySet()) {
            if (DavincisVesselsContent.skipMesh.contains(entry.getKey()))
                continue;

            modelResourceLocation = new ModelResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + entry.getKey(), "inventory");
            itemToRegister = Item.getItemFromBlock(entry.getValue());

            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }

        for (Map.Entry<String, Item> entry : DavincisVesselsContent.registeredItems.entrySet()) {
            if (DavincisVesselsContent.skipMesh.contains(entry.getKey()))
                continue;

            modelResourceLocation = new ModelResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + entry.getKey(), "inventory");
            itemToRegister = entry.getValue();

            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }
    }

    @SubscribeEvent
    public void onModelRegistryEvent(ModelRegistryEvent e) {
        registerVariantItemRenders();
    }

}
