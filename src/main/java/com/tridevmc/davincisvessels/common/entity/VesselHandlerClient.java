package com.tridevmc.davincisvessels.common.entity;

import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import com.tridevmc.movingworld.common.entity.MovingWorldHandlerClient;

public class VesselHandlerClient extends MovingWorldHandlerClient {

    private EntityMovingWorld movingWorld;

    public VesselHandlerClient(EntityMovingWorld movingWorld) {
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
