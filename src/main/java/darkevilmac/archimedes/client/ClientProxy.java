package darkevilmac.archimedes.client;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.client.control.ShipKeyHandler;
import darkevilmac.archimedes.client.handler.ClientHookContainer;
import darkevilmac.archimedes.client.render.RenderParachute;
import darkevilmac.archimedes.client.render.TileEntityGaugeRenderer;
import darkevilmac.archimedes.client.render.TileEntityHelmRenderer;
import darkevilmac.archimedes.common.ArchimedesConfig;
import darkevilmac.archimedes.common.CommonProxy;
import darkevilmac.archimedes.common.entity.EntityParachute;
import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.object.ArchimedesObjects;
import darkevilmac.archimedes.common.tileentity.TileEntityGauge;
import darkevilmac.archimedes.common.tileentity.TileEntityHelm;
import darkevilmac.movingworld.client.render.RenderMovingWorld;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.LoaderState;

import java.util.ArrayList;

public class ClientProxy extends CommonProxy {
    public ShipKeyHandler shipKeyHandler;

    public ArchimedesConfig syncedConfig;

    @Override
    public ClientHookContainer getHookContainer() {
        return new ClientHookContainer();
    }

    @Override
    public void registerKeyHandlers(ArchimedesConfig cfg) {
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
        RenderingRegistry.registerEntityRenderingHandler(EntityShip.class, new IRenderFactory<EntityShip>() {
            @Override
            public Render<? super EntityShip> createRenderFor(RenderManager manager) {
                return new RenderMovingWorld(manager);
            }
        });

        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, new IRenderFactory<EntityParachute>() {
            @Override
            public Render<? super EntityParachute> createRenderFor(RenderManager manager) {
                return new RenderParachute(manager);
            }
        });
    }

    public void registerTileEntitySpeacialRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGauge.class, new TileEntityGaugeRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHelm.class, new TileEntityHelmRenderer());
    }

    public void registerRendererVariants() {
        Item itemToRegister = null;
        ArrayList<ResourceLocation> variants = null;

        itemToRegister = Item.getItemFromBlock(ArchimedesObjects.blockBalloon);
        variants = new ArrayList<ResourceLocation>();

        for (EnumDyeColor color : EnumDyeColor.values()) {
            variants.add(new ResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + "balloon_" + color.getName()));
        }

        ResourceLocation[] variantsArray = new ResourceLocation[variants.size()];
        int index = 0;

        for (ResourceLocation str : variants) {
            variantsArray[index] = str;
            index++;
        }

        ModelBakery.registerItemVariants(itemToRegister, variantsArray);
        ModelBakery.registerItemVariants(Item.getItemFromBlock(ArchimedesObjects.blockGauge),
                new ResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + "gauge"),
                new ResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + "gauge_ext"));
    }

    public void registerItemRenderers() {
        Item itemToRegister = null;
        ModelResourceLocation modelResourceLocation = null;

        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        // Do some general render registrations for objects, not considering meta.
        for (int i = 0; i < ArchimedesObjects.registeredBlocks.size(); i++) {
            modelResourceLocation = new ModelResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + ArchimedesObjects.registeredBlocks.keySet().toArray()[i], "inventory");
            itemToRegister = Item.getItemFromBlock((Block) ArchimedesObjects.registeredBlocks.values().toArray()[i]);

            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }

        for (int i = 0; i < ArchimedesObjects.registeredItems.size(); i++) {
            modelResourceLocation = new ModelResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + ArchimedesObjects.registeredItems.keySet().toArray()[i], "inventory");
            itemToRegister = (Item) ArchimedesObjects.registeredItems.values().toArray()[i];
            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }

        // Some specific meta registrations for objects, like for extended gauges.
        itemToRegister = Item.getItemFromBlock(ArchimedesObjects.blockGauge);
        modelResourceLocation = new ModelResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + "gauge_ext", "inventory");

        modelMesher.register(itemToRegister, 1, modelResourceLocation);

        itemToRegister = Item.getItemFromBlock(ArchimedesObjects.blockBalloon);
        modelResourceLocation = null;

        for (EnumDyeColor color : EnumDyeColor.values()) {
            modelResourceLocation = new ModelResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + "balloon_" + color.getName(), "inventory");
            modelMesher.register(itemToRegister, color.getMetadata(), modelResourceLocation);
        }


    }

}
