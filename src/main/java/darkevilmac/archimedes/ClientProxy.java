package darkevilmac.archimedes;

import darkevilmac.archimedes.blockitem.BlockGauge;
import darkevilmac.archimedes.blockitem.BlockSeat;
import darkevilmac.archimedes.blockitem.TileEntityGauge;
import darkevilmac.archimedes.control.ShipKeyHandler;
import darkevilmac.archimedes.entity.EntityParachute;
import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.archimedes.render.RenderBlockGauge;
import darkevilmac.archimedes.render.RenderBlockSeat;
import darkevilmac.archimedes.render.RenderParachute;
import darkevilmac.archimedes.render.TileEntityGaugeRenderer;
import darkevilmac.movingworld.render.RenderMovingWorld;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityShip.class, new RenderMovingWorld());
        RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, new RenderParachute());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGauge.class, new TileEntityGaugeRenderer());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHelm.class, new TileEntityHelmRenderer());
        BlockGauge.gaugeBlockRenderID = RenderingRegistry.getNextAvailableRenderId();
        BlockSeat.seatBlockRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(BlockSeat.seatBlockRenderID, new RenderBlockSeat());
        RenderingRegistry.registerBlockHandler(BlockGauge.gaugeBlockRenderID, new RenderBlockGauge());
    }
}
