package darkevilmac.archimedes;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import darkevilmac.archimedes.entity.EntityParachute;
import net.minecraft.client.Minecraft;

public class CommonPlayerTicker {
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent e) {
        if (e.phase == Phase.END && e.player.ridingEntity instanceof EntityParachute && e.player.ridingEntity.ticksExisted < 40) {
            if (e.player.isSneaking()) {
                e.player.setSneaking(false);
            }
        }
    }
}
