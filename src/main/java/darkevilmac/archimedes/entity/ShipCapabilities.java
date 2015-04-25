package darkevilmac.archimedes.entity;

import darkevilmac.archimedes.blockitem.TileEntityEngine;
import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.entity.EntityMovingWorldAttachment;
import darkevilmac.movingworld.entity.MovingWorldCapabilities;
import net.minecraft.entity.Entity;

import java.util.List;

public class ShipCapabilities extends MovingWorldCapabilities {

    private final EntityShip ship;
    public float speedMultiplier, rotationMultiplier, liftMultiplier;
    public float brakeMult;
    private int balloonCount;
    private int floaters;
    private int blockCount;
    private float mass;
    private List<EntitySeat> seats;
    private List<TileEntityEngine> engines;
    private float enginePower;

    public ShipCapabilities(EntityMovingWorld movingWorld, boolean autoCalcMass) {
        super(movingWorld, autoCalcMass);
        ship = (EntityShip) movingWorld;
    }

    public float getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(float enginePower) {
        this.enginePower = enginePower;
    }

    public EntityShip getShip() {
        return ship;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public float getRotationMultiplier() {
        return rotationMultiplier;
    }

    public void setRotationMultiplier(float rotationMultiplier) {
        this.rotationMultiplier = rotationMultiplier;
    }

    public float getLiftMultiplier() {
        return liftMultiplier;
    }

    public void setLiftMultiplier(float liftMultiplier) {
        this.liftMultiplier = liftMultiplier;
    }

    public float getBrakeMult() {
        return brakeMult;
    }

    public void setBrakeMult(float brakeMult) {
        this.brakeMult = brakeMult;
    }

    public int getBalloonCount() {
        return balloonCount;
    }

    public void setBalloonCount(int balloonCount) {
        this.balloonCount = balloonCount;
    }

    public int getFloaters() {
        return floaters;
    }

    public void setFloaters(int floaters) {
        this.floaters = floaters;
    }

    @Override
    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

    @Override
    public boolean mountEntity(Entity entity) {
        if (seats == null)
        {
            return false;
        }

        for (EntityMovingWorldAttachment seat : seats)
        {
            if (seat.riddenByEntity == null)
            {
                entity.mountEntity(seat);
                return true;
            } else if (seat.riddenByEntity == entity)
            {
                seat.mountEntity(null);
                return true;
            }
        }
        return false;
    }

    @Override
    public float getMass() {
        return mass;
    }

    @Override
    public void setMass(float mass) {
        this.mass = mass;
    }

    public List<EntitySeat> getSeats() {
        return seats;
    }

    public void setSeats(List<EntitySeat> seats) {
        this.seats = seats;
    }

    public List<TileEntityEngine> getEngines() {
        return engines;
    }

    public void setEngines(List<TileEntityEngine> engines) {
        this.engines = engines;
    }

    public void spawnSeatEntities() {
        if (seats != null) {
            for (EntitySeat seat : seats) {
                ship.worldObj.spawnEntityInWorld(seat);
            }
        }
    }

}
