package darkevilmac.archimedes.common.handler;

import darkevilmac.archimedes.common.entity.EntityParachute;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonPlayerTicker {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.player.ridingEntity instanceof EntityParachute && e.player.ridingEntity.ticksExisted < 40) {
            if (e.player.isSneaking()) {
                e.player.setSneaking(false);
            }
        }
    }
}
