package com.elytradev.davincisvessels.client;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.CommonProxy;
import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.elytradev.davincisvessels.common.object.DavincisVesselsObjects;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.LoaderState;

import java.util.ArrayList;

import com.elytradev.davincisvessels.client.control.ShipKeyHandler;
import com.elytradev.davincisvessels.client.handler.ClientHookContainer;
import com.elytradev.davincisvessels.client.render.RenderParachute;
import com.elytradev.davincisvessels.client.render.RenderSeat;
import com.elytradev.davincisvessels.client.render.TileEntityGaugeRenderer;
import com.elytradev.davincisvessels.client.render.TileEntityHelmRenderer;
import com.elytradev.davincisvessels.common.DavincisVesselsConfig;
import com.elytradev.davincisvessels.common.entity.EntityParachute;
import com.elytradev.davincisvessels.common.entity.EntitySeat;
import com.elytradev.davincisvessels.common.tileentity.TileGauge;
import com.elytradev.davincisvessels.common.tileentity.TileHelm;
import com.elytradev.movingworld.client.render.RenderMovingWorld;

public class ClientProxy extends CommonProxy {

    public ShipKeyHandler shipKeyHandler;
    public DavincisVesselsConfig syncedConfig;

    @Override
    public ClientHookContainer getHookContainer() {
        return new ClientHookContainer();
    }

    @Override
    public void registerKeyHandlers(DavincisVesselsConfig cfg) {
        shipKeyHandler = new ShipKeyHandler(cfg);
        MinecraftForge.EVENT_BUS.register(shipKeyHandler);
    }

    @Override
    public void registerRenderers(LoaderState.ModState state) {
        if (state == LoaderState.ModState.PREINITIALIZED) {
            registerEntityRenderers();
            registerRendererVariants();
        }

        if (state == LoaderState.ModState.INITIALIZED) {
            registerTileEntitySpeacialRenderers();
            registerItemRenderers();
        }

        if (state == LoaderState.ModState.POSTINITIALIZED) {

        }
    }

    public void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityShip.class, RenderMovingWorld::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySeat.class, RenderSeat::new);
    }

    public void registerTileEntitySpeacialRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileGauge.class, new TileEntityGaugeRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileHelm.class, new TileEntityHelmRenderer());
    }

    public void registerRendererVariants() {
        Item itemToRegister = null;
        ArrayList<ResourceLocation> variants = null;

        itemToRegister = Item.getItemFromBlock(DavincisVesselsObjects.blockBalloon);
        variants = new ArrayList<ResourceLocation>();

        for (EnumDyeColor color : EnumDyeColor.values()) {
            variants.add(new ResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "balloon_" + color.getName()));
        }

        ResourceLocation[] variantsArray = new ResourceLocation[variants.size()];
        int index = 0;

        for (ResourceLocation str : variants) {
            variantsArray[index] = str;
            index++;
        }

        ModelBakery.registerItemVariants(itemToRegister, variantsArray);
        ModelBakery.registerItemVariants(Item.getItemFromBlock(DavincisVesselsObjects.blockGauge),
                new ResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "gauge"),
                new ResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "gauge_ext"));
    }

    public void registerItemRenderers() {
        Item itemToRegister = null;
        ModelResourceLocation modelResourceLocation = null;

        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        // Do some general render registrations for OBJECTS, not considering meta.
        for (int i = 0; i < DavincisVesselsObjects.registeredBlocks.size(); i++) {
            modelResourceLocation = new ModelResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + DavincisVesselsObjects.registeredBlocks.keySet().toArray()[i], "inventory");
            itemToRegister = Item.getItemFromBlock((Block) DavincisVesselsObjects.registeredBlocks.values().toArray()[i]);

            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }

        for (int i = 0; i < DavincisVesselsObjects.registeredItems.size(); i++) {
            modelResourceLocation = new ModelResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + DavincisVesselsObjects.registeredItems.keySet().toArray()[i], "inventory");
            itemToRegister = (Item) DavincisVesselsObjects.registeredItems.values().toArray()[i];
            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }

        // Some specific meta registrations for OBJECTS, like for extended gauges.
        itemToRegister = Item.getItemFromBlock(DavincisVesselsObjects.blockGauge);
        modelResourceLocation = new ModelResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "gauge_ext", "inventory");

        modelMesher.register(itemToRegister, 1, modelResourceLocation);

        itemToRegister = Item.getItemFromBlock(DavincisVesselsObjects.blockBalloon);
        modelResourceLocation = null;

        for (EnumDyeColor color : EnumDyeColor.values()) {
            modelResourceLocation = new ModelResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "balloon_" + color.getName(), "inventory");
            modelMesher.register(itemToRegister, color.getMetadata(), modelResourceLocation);
        }


    }

}
