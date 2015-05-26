package darkevilmac.archimedes;

import darkevilmac.archimedes.blockitem.TileEntityGauge;
import darkevilmac.archimedes.control.ShipKeyHandler;
import darkevilmac.archimedes.entity.EntityParachute;
import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.archimedes.render.RenderParachute;
import darkevilmac.archimedes.render.TileEntityGaugeRenderer;
import darkevilmac.movingworld.render.RenderMovingWorld;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {
    public ShipKeyHandler shipKeyHandler;

    @Override
    public ClientHookContainer getHookContainer() {
        return new ClientHookContainer();
    }

    @Override
    public void registerKeyHandlers(ArchimedesConfig cfg) {
        shipKeyHandler = new ShipKeyHandler(cfg);
        FMLCommonHandler.instance().bus().register(shipKeyHandler);
    }

    @Override
    public void registerRenderers() {
        registerEntityRenderers();
        registerTileEntitySpeacialRenderers();
        registerItemRenderers();
    }

    public void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityShip.class, new RenderMovingWorld(Minecraft.getMinecraft().getRenderManager()));
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, new RenderParachute(Minecraft.getMinecraft().getRenderManager()));
    }

    public void registerTileEntitySpeacialRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGauge.class, new TileEntityGaugeRenderer());
    }

    public void registerItemRenderers() {
        Item itemToRegister = null;
        ModelResourceLocation modelResourceLocation = null;

        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        // Do some general render registrations for blocks, not considering meta.

        for (int i = 0; i < ArchimedesShipMod.instance.registeredBlocks.size(); i++) {
            modelResourceLocation = new ModelResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + ArchimedesShipMod.instance.registeredBlocks.keySet().toArray()[i], "inventory");
            itemToRegister = Item.getItemFromBlock((Block) ArchimedesShipMod.instance.registeredBlocks.values().toArray()[i]);

            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }

        // Some specific meta registrations for blocks, like for extended gauges.
        itemToRegister = Item.getItemFromBlock(ArchimedesShipMod.blockGauge);
        modelResourceLocation = new ModelResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + "gauge_ext", "inventory");

        modelMesher.register(itemToRegister, 1, modelResourceLocation);

    }

}
