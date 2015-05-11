package darkevilmac.archimedes.entity;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.blockitem.TileEntityAnchorPoint;
import darkevilmac.archimedes.blockitem.TileEntityEngine;
import darkevilmac.movingworld.MaterialDensity;
import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.entity.MovingWorldCapabilities;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class ShipCapabilities extends MovingWorldCapabilities {

    private final EntityShip ship;
    public float speedMultiplier, rotationMultiplier, liftMultiplier;
    public float brakeMult;
    private int balloonCount;
    private int floaters;
    private int blockCount;
    private float mass;
    public List<TileEntityAnchorPoint> anchorPoints;
    public List<EntitySeat> seats;
    public List<TileEntityEngine> engines;

    public ShipCapabilities(EntityMovingWorld movingWorld, boolean autoCalcMass) {
        super(movingWorld, autoCalcMass);
        ship = (EntityShip) movingWorld;
    }

    public float getSpeedMult() {
        return speedMultiplier + getEnginePower() * 0.5f;
    }

    public float getRotationMult() {
        return rotationMultiplier + getEnginePower() * 0.25f;
    }

    public float getLiftMult() {
        return liftMultiplier + getEnginePower() * 0.5f;
    }

    public float getEnginePower() {
        return ship.getDataWatcher().getWatchableObjectFloat(29);
    }

    public void updateEngines() {
        float ePower = 0;
        if (engines != null) {
            for (TileEntityEngine te : engines) {
                te.updateRunning();
                if (te.isRunning()) {
                    ePower += te.enginePower;
                }
            }
        }
        if (!ship.worldObj.isRemote)
            ship.getDataWatcher().updateObject(29, ePower);
    }

    @Override
    public boolean canFly() {
        return ArchimedesShipMod.instance.modConfig.enableAirShips && getBalloonCount() >= blockCount * ArchimedesShipMod.instance.modConfig.flyBalloonRatio;
    }

    @Override
    public int getBlockCount() {
        return blockCount;
    }

    public int getBalloonCount() {
        return balloonCount;
    }

    public void setBalloonCount(int balloonCount) {
        this.balloonCount = balloonCount;
    }

    public int getFloaterCount() {
        return floaters;
    }

    @Override
    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public void addAttachments(EntitySeat entity) {
        if (seats == null) seats = new ArrayList<EntitySeat>();
        if (entity != null && entity instanceof EntitySeat) seats.add(entity);
    }

    public boolean canMove() {
        return ship.getDataWatcher().getWatchableObjectByte(28) == 1;
    }

    public List<EntitySeat> getAttachments() {
        return seats;
    }

    public List<TileEntityEngine> getEngines() {
        return engines;
    }

    @Override
    public void onChunkBlockAdded(Block block, int metadata, int x, int y, int z) {
        blockCount++;
        mass += MaterialDensity.getDensity(block);

        if (block == ArchimedesShipMod.blockBalloon) {
            balloonCount++;
        } else if (block == ArchimedesShipMod.blockFloater) {
            floaters++;
        } else if (block == ArchimedesShipMod.blockEngine) {
            TileEntity te = ship.getMovingWorldChunk().getTileEntity(x, y, z);
            if (te instanceof TileEntityEngine) {
                if (engines == null) {
                    engines = new ArrayList<TileEntityEngine>(4);
                }
                engines.add((TileEntityEngine) te);
            }
        } else if (block == ArchimedesShipMod.blockSeat && !ship.worldObj.isRemote) {
            int x1 = ship.riderDestinationX, y1 = ship.riderDestinationY, z1 = ship.riderDestinationZ;
            if (ship.frontDirection == 0) {
                z1 -= 1;
            } else if (ship.frontDirection == 1) {
                x1 += 1;
            } else if (ship.frontDirection == 2) {
                z1 += 1;
            } else if (ship.frontDirection == 3) {
                x1 -= 1;
            }
            if (x != x1 || y != y1 || z != z1) {
                EntitySeat seat = new EntitySeat(ship.worldObj);
                seat.setParentShip(ship, x, y, z);
                addAttachments(seat);
            }
        }
    }

    public boolean hasSeat(EntitySeat seat) {
        if (seats != null && !seats.isEmpty()) {
            return seats.contains(seat);
        } else {
            return true;
        }
    }

    public EntitySeat getAvailableSeat() {
        for (EntitySeat seat : seats) {
            if (seat.riddenByEntity == null || (seat.riddenByEntity != null && (seat.riddenByEntity.ridingEntity == null
                    || (seat.riddenByEntity.ridingEntity != null && seat.riddenByEntity.ridingEntity != seat)))) {
                seat.mountEntity(null);
                return seat;
            }
        }
        return null;
    }

    @Override
    public boolean mountEntity(Entity entity) {
        if (seats == null) {
            return false;
        }

        for (EntitySeat seat : seats) {
            if (seat.interactFirst((EntityPlayer) entity)) {
                return true;
            }
        }
        return false;
    }

    public void spawnSeatEntities() {
        if (seats != null) {
            for (EntitySeat seat : seats) {
                ship.worldObj.spawnEntityInWorld(seat);
            }
        }
    }

    @Override
    public void clearBlockCount() {
        speedMultiplier = rotationMultiplier = liftMultiplier = 1F;
        brakeMult = 0.9F;
        floaters = 0;
        blockCount = 0;
        mass = 0F;
        if (engines != null) {
            engines.clear();
            engines = null;
        }
    }

    @Override
    public void clear() {
        if (seats != null) {
            for (EntitySeat seat : seats) {
                seat.setDead();
            }
            seats = null;
        }
        if (engines != null) {
            engines.clear();
            engines = null;
        }
        clearBlockCount();
    }

    @Override
    public float getSpeedLimit() {
        return ArchimedesShipMod.instance.modConfig.speedLimit;
    }

    @Override
    public float getBankingMultiplier() {
        return ArchimedesShipMod.instance.modConfig.bankingMultiplier;
    }


}
