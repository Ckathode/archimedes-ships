package darkevilmac.archimedes.common.entity;

import darkevilmac.movingworld.common.entity.EntityMovingWorld;
import darkevilmac.movingworld.common.entity.MovingWorldHandlerClient;

public class ShipHandlerClient extends MovingWorldHandlerClient {

    private EntityMovingWorld movingWorld;

    public ShipHandlerClient(EntityMovingWorld movingWorld) {
        super(movingWorld);
    }

    @Override
    public EntityMovingWorld getMovingWorld() {
        return movingWorld;
    }

    @Override
    public void setMovingWorld(EntityMovingWorld movingWorld) {
        this.movingWorld = movingWorld;
    }
}
