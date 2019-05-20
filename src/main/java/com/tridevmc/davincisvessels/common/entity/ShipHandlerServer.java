package com.tridevmc.davincisvessels.common.entity;

import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import com.tridevmc.movingworld.common.entity.MovingWorldHandlerServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

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
        return movingWorld.getMovingWorldCapabilities().mountEntity(player);
    }

    @Override
    public void onChunkUpdate() {
        super.onChunkUpdate();
        if (firstChunkUpdate) {
            ((ShipCapabilities) movingWorld.getMovingWorldCapabilities()).spawnSeatEntities();
            movingWorld.getDataManager().set(EntityShip.CAN_SUBMERGE, ((ShipCapabilities) movingWorld.getMovingWorldCapabilities()).canSubmerge());
        }
    }
}