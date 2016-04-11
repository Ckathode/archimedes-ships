package darkevilmac.archimedes.client.control;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.ArchimedesConfig;
import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.network.ClientOpenGuiMessage;
import darkevilmac.movingworld.MovingWorld;
import darkevilmac.movingworld.common.network.MovingWorldClientActionMessage;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShipKeyHandler {
    private ArchimedesConfig config;
    private boolean kbShipGuiPrevState, kbDisassemblePrevState;

    public ShipKeyHandler(ArchimedesConfig cfg) {
        config = cfg;
        kbShipGuiPrevState = kbDisassemblePrevState = false;

    }

    @SubscribeEvent
    public void keyPress(InputEvent.KeyInputEvent e) {
    }

    @SubscribeEvent
    public void updateControl(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.START && e.side == Side.CLIENT && e.player == FMLClientHandler.instance().getClientPlayerEntity() && e.player.getRidingEntity() != null && e.player.getRidingEntity()  instanceof EntityShip) {
            if (config.kbShipInv.isKeyDown() && !kbShipGuiPrevState) {
                ClientOpenGuiMessage msg = new ClientOpenGuiMessage(2);
                ArchimedesShipMod.instance.network.sendToServer(msg);
            }
            kbShipGuiPrevState = config.kbShipInv.isKeyDown();

            if (config.kbDisassemble.isKeyDown() && !kbDisassemblePrevState) {
                MovingWorldClientActionMessage msg = new MovingWorldClientActionMessage((EntityShip) e.player.getRidingEntity() , MovingWorldClientActionMessage.Action.DISASSEMBLE);
                MovingWorld.instance.network.sendToServer(msg);
            }
            kbDisassemblePrevState = config.kbDisassemble.isKeyDown();

            int c = getHeightControl();
            EntityShip ship = (EntityShip) e.player.getRidingEntity() ;
            if (c != ship.getController().getShipControl()) {
                ship.getController().updateControl(ship, e.player, c);
            }
        }
    }


    public int getHeightControl() {
        if (config.kbAlign.isKeyDown()) return 4;
        if (config.kbBrake.isKeyDown()) return 3;
        int vert = 0;
        if (config.kbUp.isKeyDown()) vert++;
        if (config.kbDown.isKeyDown()) vert--;
        return vert == 0 ? 0 : vert < 0 ? 1 : vert > 0 ? 2 : 0;
    }
}
