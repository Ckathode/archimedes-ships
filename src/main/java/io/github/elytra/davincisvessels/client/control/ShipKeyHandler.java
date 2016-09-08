package io.github.elytra.davincisvessels.client.control;

import io.github.elytra.davincisvessels.common.DavincisVesselsConfig;
import io.github.elytra.davincisvessels.common.entity.EntityShip;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;
import io.github.elytra.movingworld.common.network.MovingWorldClientAction;
import io.github.elytra.movingworld.common.network.MovingWorldNetworking;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShipKeyHandler {
    private DavincisVesselsConfig config;
    private boolean kbShipGuiPrevState, kbDisassemblePrevState, kbAlignPrevState;

    public ShipKeyHandler(DavincisVesselsConfig cfg) {
        config = cfg;
        kbShipGuiPrevState = kbDisassemblePrevState = false;

    }

    @SubscribeEvent
    public void keyPress(InputEvent.KeyInputEvent e) {
    }

    @SubscribeEvent
    public void updateControl(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.START && e.side == Side.CLIENT && e.player == FMLClientHandler.instance().getClientPlayerEntity() && e.player.getRidingEntity() != null && e.player.getRidingEntity() instanceof EntityShip) {
            if (config.kbShipInv.isKeyDown() && !kbShipGuiPrevState) {
                DavincisVesselsNetworking.NETWORK.send().packet("ClientOpenGUIMessage")
                        .with("guiID", 2).toServer();
            }
            kbShipGuiPrevState = config.kbShipInv.isKeyDown();

            if (config.kbDisassemble.isKeyDown() && !kbDisassemblePrevState) {
                MovingWorldNetworking.NETWORK.send().packet("MovingWorldClientActionMessage")
                        .with("dimID", e.player.worldObj.provider.getDimension())
                        .with("entityID", e.player.getRidingEntity().getEntityId())
                        .with("action", MovingWorldClientAction.DISASSEMBLE.toByte())
                        .toServer();
            }
            kbDisassemblePrevState = config.kbDisassemble.isKeyDown();

            if (config.kbAlign.isKeyDown() && !kbAlignPrevState) {
                MovingWorldNetworking.NETWORK.send().packet("MovingWorldClientActionMessage")
                        .with("dimID", e.player.worldObj.provider.getDimension())
                        .with("entityID", e.player.getRidingEntity().getEntityId())
                        .with("action", MovingWorldClientAction.ALIGN.toByte())
                        .toServer();
            }
            kbAlignPrevState = config.kbAlign.isKeyDown();

            int c = getControlCode();
            EntityShip ship = (EntityShip) e.player.getRidingEntity();
            if (c != ship.getController().getShipControl()) {
                ship.getController().updateControl(ship, e.player, c);
            }
        }
    }


    public int getControlCode() {
        if (config.kbAlign.isKeyDown()) return 4;
        if (config.kbBrake.isKeyDown()) return 3;
        int vert = 0;
        if (config.kbUp.isKeyDown()) vert++;
        if (config.kbDown.isKeyDown()) vert--;
        return vert == 0 ? 0 : vert < 0 ? 1 : vert > 0 ? 2 : 0;
    }
}
