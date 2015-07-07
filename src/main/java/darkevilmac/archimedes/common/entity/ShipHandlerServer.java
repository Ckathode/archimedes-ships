package darkevilmac.archimedes.common.entity;

import darkevilmac.movingworld.common.entity.EntityMovingWorld;
import darkevilmac.movingworld.common.entity.MovingWorldHandlerServer;
import net.minecraft.entity.player.EntityPlayer;

public class ShipHandlerServer extends MovingWorldHandlerServer {

    private EntityMovingWorld movingWorld;
    private boolean firstChunkUpdate;

    public ShipHandlerServer(EntityShip entityship) {
        super(entityship);
        firstChunkUpdate = true;
    }

    @Override
    public EntityMovingWorld getMovingWorld() {
        return movingWorld;
    }

    @Override
    public void setMovingWorld(EntityMovingWorld movingWorld) {
        this.movingWorld = movingWorld;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (movingWorld.riddenByEntity == null) {
            player.mountEntity(movingWorld);
            return true;
        } else {
            if (player.ridingEntity != null)
                player.mountEntity(null);
            return movingWorld.getCapabilities().mountEntity(player);
        }
    }

    @Override
    public void onChunkUpdate() {
        super.onChunkUpdate();
        if (firstChunkUpdate) {
            ((ShipCapabilities) movingWorld.getCapabilities()).spawnSeatEntities();
            movingWorld.getDataWatcher().updateObject(27, ((ShipCapabilities) movingWorld.getCapabilities()).canSubmerge() ? new Byte((byte) 1) : new Byte((byte) 0));
        }
    }
}