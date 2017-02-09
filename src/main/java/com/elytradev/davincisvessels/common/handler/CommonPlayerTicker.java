package com.elytradev.davincisvessels.common.handler;

import com.elytradev.davincisvessels.common.entity.EntityParachute;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonPlayerTicker {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.player.getRidingEntity() instanceof EntityParachute && e.player.getRidingEntity().ticksExisted < 40) {
            if (e.player.isSneaking()) {
                e.player.setSneaking(false);
            }
        }
    }
}
