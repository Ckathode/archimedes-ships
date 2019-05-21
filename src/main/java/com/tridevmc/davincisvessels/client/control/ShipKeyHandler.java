package com.tridevmc.davincisvessels.client.control;

import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.network.message.OpenGuiMessage;
import com.tridevmc.movingworld.common.network.MovingWorldClientAction;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@OnlyIn(Dist.CLIENT)
public class ShipKeyHandler {
    private DavincisKeybinds keybinds;
    private boolean kbShipGuiPrevState, kbDisassemblePrevState, kbAlignPrevState;

    public ShipKeyHandler(DavincisKeybinds cfg) {
        keybinds = cfg;
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
            if (keybinds.kbShipInv.isKeyDown() && !kbShipGuiPrevState) {
                new OpenGuiMessage(ship.getEntityId()).sendToServer();
            }
            kbShipGuiPrevState = keybinds.kbShipInv.isKeyDown();

            if (keybinds.kbDisassemble.isKeyDown() && !kbDisassemblePrevState) {
                MovingWorldClientAction.DISASSEMBLE.sendToServer(ship);
            }
            kbDisassemblePrevState = keybinds.kbDisassemble.isKeyDown();

            if (keybinds.kbAlign.isKeyDown() && !kbAlignPrevState) {
                MovingWorldClientAction.ALIGN.sendToServer(ship);
            }
            kbAlignPrevState = keybinds.kbAlign.isKeyDown();

            int c = getControlCode();
            if (c != ship.getController().getShipControl()) {
                ship.getController().updateControl(ship, e.player, c);
            }
        }
    }


    public int getControlCode() {
        if (keybinds.kbAlign.isKeyDown()) return 4;
        if (keybinds.kbBrake.isKeyDown()) return 3;
        int vert = 0;
        if (keybinds.kbUp.isKeyDown()) vert++;
        if (keybinds.kbDown.isKeyDown()) vert--;
        return vert == 0 ? 0 : vert < 0 ? 1 : vert > 0 ? 2 : 0;
    }
}
