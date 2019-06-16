package com.tridevmc.davincisvessels.client.control;

import com.tridevmc.davincisvessels.common.entity.EntityVessel;
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
public class VesselKeyHandler {
    private DavincisKeybinds keybinds;
    private boolean kbVesselGuiPrevState, kbDisassemblePrevState, kbAlignPrevState;

    public VesselKeyHandler(DavincisKeybinds cfg) {
        keybinds = cfg;
        kbVesselGuiPrevState = kbDisassemblePrevState = false;

    }

    @SubscribeEvent
    public void keyPress(InputEvent.KeyInputEvent e) {
    }

    @SubscribeEvent
    public void updateControl(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.START && e.side == LogicalSide.CLIENT
                && e.player == Minecraft.getInstance().player
                && e.player.getRidingEntity() instanceof EntityVessel) {
            EntityVessel vessel = (EntityVessel) e.player.getRidingEntity();
            if (keybinds.kbVesselInv.isKeyDown() && !kbVesselGuiPrevState) {
                new OpenGuiMessage(vessel.getEntityId()).sendToServer();
            }
            kbVesselGuiPrevState = keybinds.kbVesselInv.isKeyDown();

            if (keybinds.kbDisassemble.isKeyDown() && !kbDisassemblePrevState) {
                MovingWorldClientAction.DISASSEMBLE.sendToServer(vessel);
            }
            kbDisassemblePrevState = keybinds.kbDisassemble.isKeyDown();

            if (keybinds.kbAlign.isKeyDown() && !kbAlignPrevState) {
                MovingWorldClientAction.ALIGN.sendToServer(vessel);
            }
            kbAlignPrevState = keybinds.kbAlign.isKeyDown();

            int c = getControlCode();
            if (c != vessel.getController().getVesselControl()) {
                vessel.getController().updateControl(vessel, e.player, c);
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
