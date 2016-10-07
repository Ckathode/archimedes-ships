package io.github.elytra.davincisvessels.common.entity;

import io.github.elytra.movingworld.common.entity.EntityMovingWorld;
import io.github.elytra.movingworld.common.entity.MovingWorldHandlerServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    public boolean interact(EntityPlayer player, ItemStack stack, EnumHand hand) {
        if (!movingWorld.isBeingRidden()) {
            return player.startRiding(movingWorld);
        } else {
            if (player.getRidingEntity() != null) {
                player.dismountRidingEntity();
            }
            if (!movingWorld.isPassenger(player))
                return movingWorld.getCapabilities().mountEntity(player);
        }
        return false;
    }

    @Override
    public void onChunkUpdate() {
        super.onChunkUpdate();
        if (firstChunkUpdate) {
            ((ShipCapabilities) movingWorld.getCapabilities()).spawnSeatEntities();
            movingWorld.getDataManager().set(EntityShip.CAN_SUBMERGE, ((ShipCapabilities) movingWorld.getCapabilities()).canSubmerge() ? new Byte((byte) 1) : new Byte((byte) 0));
        }
    }
}