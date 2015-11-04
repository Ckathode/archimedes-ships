package darkevilmac.archimedes.common.handler;


import darkevilmac.archimedes.common.tileentity.TileEntityCrate;
import darkevilmac.archimedes.common.tileentity.TileEntitySecuredBed;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonHookContainer {
    @SubscribeEvent
    public void onInteractWithEntity(EntityInteractEvent event) {
        if (event.entityPlayer != null) {
            int x = MathHelper.floor_double(event.target.posX);
            int y = MathHelper.floor_double(event.target.posY);
            int z = MathHelper.floor_double(event.target.posZ);

            TileEntity te = event.entity.worldObj.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityCrate && ((TileEntityCrate) te).getContainedEntity() == event.target) {
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

        if (e.entityPlayer.getGameProfile() != null && e.entityPlayer.getGameProfile().getId() != null &&
                ConnectionHandler.playerBedMap.containsKey(e.entityPlayer.getGameProfile().getId())) {
            //Spawn for the player is changing and they use a secured bed, clear the map of the player and set the bed player to null.

            TileEntitySecuredBed bed = ConnectionHandler.playerBedMap.get(e.entityPlayer.getGameProfile().getId());

            if (bed.getPos().equals(e.newSpawn))
                return;

            bed.setPlayer(null);
            ConnectionHandler.playerBedMap.remove(e.entityPlayer.getGameProfile().getId());
        }
    }

}
