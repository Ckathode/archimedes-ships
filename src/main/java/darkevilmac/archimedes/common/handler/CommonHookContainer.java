package darkevilmac.archimedes.common.handler;


import darkevilmac.archimedes.common.tileentity.TileEntityCrate;
import darkevilmac.archimedes.common.tileentity.TileEntitySecuredBed;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonHookContainer {
    @SubscribeEvent
    public void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntityPlayer() != null) {
            int x = MathHelper.floor_double(event.getTarget().posX);
            int y = MathHelper.floor_double(event.getTarget().posY);
            int z = MathHelper.floor_double(event.getTarget().posZ);

            TileEntity te = event.getEntity().worldObj.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityCrate && ((TileEntityCrate) te).getContainedEntity() == event.getTarget()) {
                ((TileEntityCrate) te).releaseEntity();
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerSpawnSet(PlayerSetSpawnEvent e) {
        if (e.isCanceled())
            return;

        if (e.getEntityPlayer().getGameProfile() != null && e.getEntityPlayer().getGameProfile().getId() != null &&
                ConnectionHandler.playerBedMap.containsKey(e.getEntityPlayer().getGameProfile().getId())) {
            //Spawn for the player is changing and they use a secured bed, clear the map of the player.

            TileEntitySecuredBed bed = ConnectionHandler.playerBedMap.get(e.getEntityPlayer().getGameProfile().getId());

            if (bed.getPos().equals(e.getNewSpawn()))
                return;

            ConnectionHandler.playerBedMap.remove(e.getEntityPlayer().getGameProfile().getId());
        }
    }

}
