package com.elytradev.davincisvessels.client.control;

import com.elytradev.davincisvessels.common.DavincisVesselsConfig;
import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.elytradev.davincisvessels.common.network.message.OpenGuiMessage;
import com.elytradev.movingworld.common.network.MovingWorldClientAction;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.LogicalSide;
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
        if (e.phase == TickEvent.Phase.START && e.side == LogicalSide.CLIENT
                && e.player == Minecraft.getInstance().player
                && e.player.getRidingEntity() instanceof EntityShip) {
            EntityShip ship = (EntityShip) e.player.getRidingEntity();
            if (config.kbShipInv.isKeyDown() && !kbShipGuiPrevState) {
                new OpenGuiMessage(2).sendToServer();
            }
            kbShipGuiPrevState = config.kbShipInv.isKeyDown();

            if (config.kbDisassemble.isKeyDown() && !kbDisassemblePrevState) {
                MovingWorldClientAction.DISASSEMBLE.sendToServer(ship);
            }
            kbDisassemblePrevState = config.kbDisassemble.isKeyDown();

            if (config.kbAlign.isKeyDown() && !kbAlignPrevState) {
                MovingWorldClientAction.ALIGN.sendToServer(ship);
            }
            kbAlignPrevState = config.kbAlign.isKeyDown();

            int c = getControlCode();
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
