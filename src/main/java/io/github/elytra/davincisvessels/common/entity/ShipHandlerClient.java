package io.github.elytra.davincisvessels.common.entity;

import io.github.elytra.movingworld.common.entity.EntityMovingWorld;
import io.github.elytra.movingworld.common.entity.MovingWorldHandlerClient;

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
