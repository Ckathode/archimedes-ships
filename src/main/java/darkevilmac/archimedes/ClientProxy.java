package darkevilmac.archimedes;

import darkevilmac.archimedes.blockitem.TileEntityGauge;
import darkevilmac.archimedes.control.ShipKeyHandler;
import darkevilmac.archimedes.entity.EntityParachute;
import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.archimedes.render.RenderParachute;
import darkevilmac.archimedes.render.TileEntityGaugeRenderer;
import darkevilmac.movingworld.render.RenderMovingWorld;
import net.minecraft.client.Minecraft;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityShip.class, new RenderMovingWorld(Minecraft.getMinecraft().getRenderManager()));
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, new RenderParachute(Minecraft.getMinecraft().getRenderManager()));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGauge.class, new TileEntityGaugeRenderer());
    }
}
