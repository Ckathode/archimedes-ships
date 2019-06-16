package com.tridevmc.davincisvessels.common.entity;

import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import com.tridevmc.movingworld.common.entity.MovingWorldHandlerServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class VesselHandlerServer extends MovingWorldHandlerServer {

    private EntityMovingWorld movingWorld;
    private boolean firstChunkUpdate;

    public VesselHandlerServer(EntityVessel entityvessel) {
        super(entityvessel);
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
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        return movingWorld.getMovingWorldCapabilities().mountEntity(player);
    }

    @Override
    public void onChunkUpdate() {
        super.onChunkUpdate();
        if (firstChunkUpdate) {
            ((VesselCapabilities) movingWorld.getMovingWorldCapabilities()).spawnSeatEntities();
            movingWorld.getDataManager().set(EntityVessel.CAN_SUBMERGE, ((VesselCapabilities) movingWorld.getMovingWorldCapabilities()).canSubmerge());
        }
    }
}