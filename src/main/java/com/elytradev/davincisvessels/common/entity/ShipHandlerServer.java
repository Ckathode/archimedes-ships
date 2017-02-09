package com.elytradev.davincisvessels.common.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

import com.elytradev.movingworld.common.entity.EntityMovingWorld;
import com.elytradev.movingworld.common.entity.MovingWorldHandlerServer;

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
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return movingWorld.getCapabilities().mountEntity(player);
    }

    @Override
    public void onChunkUpdate() {
        super.onChunkUpdate();
        if (firstChunkUpdate) {
            ((ShipCapabilities) movingWorld.getCapabilities()).spawnSeatEntities();
            movingWorld.getDataManager().set(EntityShip.CAN_SUBMERGE, ((ShipCapabilities) movingWorld.getCapabilities()).canSubmerge());
        }
    }
}