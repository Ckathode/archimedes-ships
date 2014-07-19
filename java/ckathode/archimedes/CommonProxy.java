package ckathode.archimedes;

import net.minecraftforge.common.MinecraftForge;
import ckathode.archimedes.gui.ASGuiHandler;
import ckathode.archimedes.network.ASMessagePipeline;
import ckathode.archimedes.network.MsgAssembleResult;
import ckathode.archimedes.network.MsgChunkBlockUpdate;
import ckathode.archimedes.network.MsgClientHelmAction;
import ckathode.archimedes.network.MsgClientOpenGUI;
import ckathode.archimedes.network.MsgClientRenameShip;
import ckathode.archimedes.network.MsgClientShipAction;
import ckathode.archimedes.network.MsgControlInput;
import ckathode.archimedes.network.MsgFarInteract;
import ckathode.archimedes.network.MsgRequestShipData;
import ckathode.archimedes.network.MsgTileEntities;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class CommonProxy
{
	public CommonPlayerTicker	playerTicker;
	public CommonHookContainer	hookContainer;
	
	public CommonHookContainer getHookContainer()
	{
		return new CommonHookContainer();
	}
	
	public void registerPackets(ASMessagePipeline pipeline)
	{
		pipeline.registerPacket(MsgClientHelmAction.class);
		pipeline.registerPacket(MsgClientShipAction.class);
		pipeline.registerPacket(MsgClientRenameShip.class);
		pipeline.registerPacket(MsgClientOpenGUI.class);
		pipeline.registerPacket(MsgAssembleResult.class);
		pipeline.registerPacket(MsgChunkBlockUpdate.class);
		pipeline.registerPacket(MsgRequestShipData.class);
		pipeline.registerPacket(MsgTileEntities.class);
		pipeline.registerPacket(MsgControlInput.class);
		pipeline.registerPacket(MsgFarInteract.class);
	}
	
	public void registerKeyHandlers(ArchimedesConfig cfg)
	{
	}
	
	public void registerEventHandlers()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ArchimedesShipMod.instance, new ASGuiHandler());
		
		playerTicker = new CommonPlayerTicker();
		FMLCommonHandler.instance().bus().register(playerTicker);
		MinecraftForge.EVENT_BUS.register(hookContainer = getHookContainer());
	}
	
	public void registerRenderers()
	{
	}
	
}
