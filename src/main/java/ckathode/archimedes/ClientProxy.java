package ckathode.archimedes;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import ckathode.archimedes.blockitem.BlockGauge;
import ckathode.archimedes.blockitem.BlockSeat;
import ckathode.archimedes.blockitem.TileEntityEngine;
import ckathode.archimedes.blockitem.TileEntityGauge;
import ckathode.archimedes.blockitem.TileEntityHelm;
import ckathode.archimedes.client.renderer.item.ItemModelEngineRenderer;
import ckathode.archimedes.client.renderer.item.ItemModelHelmRenderer;
import ckathode.archimedes.client.renderer.tilenetity.TileEntityModelEngineRenderer;
import ckathode.archimedes.client.renderer.tilenetity.TileEntityModelHelmRenderer;
import ckathode.archimedes.control.ShipKeyHandler;
import ckathode.archimedes.entity.EntityParachute;
import ckathode.archimedes.entity.EntityShip;
import ckathode.archimedes.render.RenderBlockGauge;
import ckathode.archimedes.render.RenderBlockSeat;
import ckathode.archimedes.render.RenderParachute;
import ckathode.archimedes.render.RenderShip;
import ckathode.archimedes.render.TileEntityGaugeRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy
{
	public ShipKeyHandler	shipKeyHandler;
	
	@Override
	public ClientHookContainer getHookContainer()
	{
		return new ClientHookContainer();
	}
	
	@Override
	public void registerKeyHandlers(ArchimedesConfig cfg)
	{
		shipKeyHandler = new ShipKeyHandler(cfg);
		FMLCommonHandler.instance().bus().register(shipKeyHandler);
	}
	
	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityShip.class, new RenderShip());
		RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, new RenderParachute());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGauge.class, new TileEntityGaugeRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer( TileEntityHelm.class, new TileEntityModelHelmRenderer() );
		ClientRegistry.bindTileEntitySpecialRenderer( TileEntityEngine.class, new TileEntityModelEngineRenderer() );
		MinecraftForgeClient.registerItemRenderer( Item.getItemFromBlock( ArchimedesShipMod.blockMarkShip ), new ItemModelHelmRenderer( new TileEntityModelHelmRenderer(), new TileEntityHelm() ) );
		MinecraftForgeClient.registerItemRenderer( Item.getItemFromBlock( ArchimedesShipMod.blockEngine ), new ItemModelEngineRenderer( new TileEntityModelEngineRenderer(), new TileEntityEngine() ) );
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHelm.class, new TileEntityHelmRenderer());
		BlockGauge.gaugeBlockRenderID = RenderingRegistry.getNextAvailableRenderId();
		BlockSeat.seatBlockRenderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(BlockSeat.seatBlockRenderID, new RenderBlockSeat());
		RenderingRegistry.registerBlockHandler(BlockGauge.gaugeBlockRenderID, new RenderBlockGauge());
	}
}
