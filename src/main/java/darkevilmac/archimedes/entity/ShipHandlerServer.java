package darkevilmac.archimedes.entity;

import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.entity.MovingWorldHandlerServer;

public class ShipHandlerServer extends MovingWorldHandlerServer {

    private EntityMovingWorld movingWorld;

    public ShipHandlerServer(EntityMovingWorld entitymovingWorld) {
        super(entitymovingWorld);
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
    public void onChunkUpdate() {
        super.onChunkUpdate();
        if (firstChunkUpdate) {
            ((ShipCapabilities) movingWorld.getCapabilities()).spawnSeatEntities();
        }
    }
}
