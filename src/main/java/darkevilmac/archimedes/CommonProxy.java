package darkevilmac.archimedes;

import darkevilmac.archimedes.gui.ASGuiHandler;
import darkevilmac.archimedes.network.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import darkevilmac.movingworld.network.advanced.*;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
    public CommonPlayerTicker playerTicker;
    public CommonHookContainer hookContainer;

    public CommonHookContainer getHookContainer() {
        return new CommonHookContainer();
    }

    public void registerPackets(ASMessagePipeline pipeline) {
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

    public void registerKeyHandlers(ArchimedesConfig cfg) {
    }

    public void registerEventHandlers() {
        NetworkRegistry.INSTANCE.registerGuiHandler(ArchimedesShipMod.instance, new ASGuiHandler());

        playerTicker = new CommonPlayerTicker();
        FMLCommonHandler.instance().bus().register(playerTicker);
        MinecraftForge.EVENT_BUS.register(hookContainer = getHookContainer());
    }

    public void registerRenderers() {
    }

}
