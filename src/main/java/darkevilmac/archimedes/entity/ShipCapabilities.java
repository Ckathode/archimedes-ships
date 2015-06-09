package darkevilmac.archimedes.entity;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.blockitem.TileEntityAnchorPoint;
import darkevilmac.archimedes.blockitem.TileEntityEngine;
import darkevilmac.movingworld.MaterialDensity;
import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.entity.MovingWorldCapabilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class ShipCapabilities extends MovingWorldCapabilities {

    private final EntityShip ship;
    public float speedMultiplier, rotationMultiplier, liftMultiplier;
    public float brakeMult;
    private List<TileEntityAnchorPoint.AnchorPointInfo> anchorPoints;
    private List<EntitySeat> seats;
    private List<TileEntityEngine> engines;
    private int balloonCount;
    private int floaters;
    private int blockCount;
    private float mass;

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

    public TileEntityAnchorPoint findClosestValidAnchor(int range) {
        if (ship != null && ship.worldObj != null && !ship.worldObj.isRemote) {
            if (anchorPoints != null) {

                List<TileEntityAnchorPoint> validAnchorPoints = new ArrayList<TileEntityAnchorPoint>();
                List<Integer> validAnchorPointsDistance = new ArrayList<Integer>();
                for (TileEntityAnchorPoint.AnchorPointInfo anchorPointInfo : anchorPoints) {
                    int differenceX = 0;
                    int differenceY = 0;
                    int differenceZ = 0;

                    boolean validXDistance = false;
                    boolean validYDistance = false;
                    boolean validZDistance = false;
                    boolean validDistance = false;

                    if (anchorPointInfo.linkPos.getX() > ship.posX) {
                        for (int i = 1; i < range; i++) {
                            if (ship.posX + i >= anchorPointInfo.linkPos.getX()) {
                                validXDistance = true;
                                differenceX = i;
                            }
                        }
                    } else {
                        for (int i = 1; i < range; i++) {
                            if (anchorPointInfo.linkPos.getX() + i >= ship.posX) {
                                validXDistance = true;
                                differenceX = i;
                            }
                        }
                    }

                    if (anchorPointInfo.linkPos.getY() > ship.posY) {
                        for (int i = 1; i < range; i++) {
                            if (ship.posY + i >= anchorPointInfo.linkPos.getY()) {
                                validYDistance = true;
                                differenceY = i;
                            }
                        }
                    } else {
                        for (int i = 1; i < range; i++) {
                            if (anchorPointInfo.linkPos.getY() + i >= ship.posY) {
                                validYDistance = true;
                                differenceY = i;
                            }
                        }
                    }

                    if (anchorPointInfo.linkPos.getZ() > ship.posZ) {
                        for (int i = 1; i < range; i++) {
                            if (ship.posZ + i >= anchorPointInfo.linkPos.getZ()) {
                                validZDistance = true;
                                differenceZ = i;
                            }
                        }
                    } else {
                        for (int i = 1; i < range; i++) {
                            if (anchorPointInfo.linkPos.getZ() + i >= ship.posZ) {
                                validZDistance = true;
                                differenceZ = i;
                            }
                        }
                    }

                    validDistance = validXDistance && validYDistance && validZDistance;

                    if (validDistance && ship.worldObj.getTileEntity(anchorPointInfo.linkPos) != null
                            && ship.worldObj.getTileEntity(anchorPointInfo.linkPos) instanceof TileEntityAnchorPoint) {
                        if (!((TileEntityAnchorPoint) ship.worldObj.getTileEntity(anchorPointInfo.linkPos)).anchorPointInfo.forShip) {
                            validAnchorPoints.add((TileEntityAnchorPoint) ship.worldObj.getTileEntity(anchorPointInfo.linkPos));
                            validAnchorPointsDistance.add(differenceX + differenceY + differenceZ);
                        }
                    }
                }
                TileEntityAnchorPoint shortestAnchor = null;

                if (validAnchorPoints != null && !validAnchorPoints.isEmpty()) {
                    int shortestIndex = 0;
                    for (int index = 0; index < validAnchorPoints.size(); index++) {
                        if (validAnchorPointsDistance.get(index) < validAnchorPointsDistance.get(shortestIndex)) {
                            shortestIndex = index;
                        }
                    }
                    shortestAnchor = validAnchorPoints.get(shortestIndex);
                }

                return shortestAnchor;
            }
        }
        return null;
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

    public List<TileEntityAnchorPoint.AnchorPointInfo> getAnchorPoints() {
        return anchorPoints;
    }

    @Override
    public void onChunkBlockAdded(IBlockState state, BlockPos pos) {
        blockCount++;
        mass += MaterialDensity.getDensity(state);

        Block block = state.getBlock();
        if (block == null)
            return;

        if (block == ArchimedesShipMod.blockBalloon) {
            balloonCount++;
        } else if (block == ArchimedesShipMod.blockFloater) {
            floaters++;
        } else if (block == ArchimedesShipMod.blockAnchorPoint) {
            TileEntity te = ship.getMovingWorldChunk().getTileEntity(pos);
            if (te != null && te instanceof TileEntityAnchorPoint && ((TileEntityAnchorPoint) te).anchorPointInfo != null && ((TileEntityAnchorPoint) te).anchorPointInfo.forShip) {
                if (anchorPoints == null) {
                    anchorPoints = new ArrayList<TileEntityAnchorPoint.AnchorPointInfo>();
                }
                anchorPoints.add(((TileEntityAnchorPoint) te).anchorPointInfo);
            }
        } else if (block == ArchimedesShipMod.blockEngine) {
            TileEntity te = ship.getMovingWorldChunk().getTileEntity(pos);
            if (te instanceof TileEntityEngine) {
                if (engines == null) {
                    engines = new ArrayList<TileEntityEngine>(4);
                }
                engines.add((TileEntityEngine) te);
            }
        } else if (block == ArchimedesShipMod.blockSeat && !ship.worldObj.isRemote) {
            int x1 = ship.riderDestination.getX(), y1 = ship.riderDestination.getY(), z1 = ship.riderDestination.getZ();
            int frontDir = ship.frontDirection.getHorizontalIndex();

            if (frontDir == 0) {
                z1 -= 1;
            } else if (frontDir == 1) {
                x1 += 1;
            } else if (frontDir == 2) {
                z1 += 1;
            } else if (frontDir == 3) {
                x1 -= 1;
            }

            if (pos.getX() != x1 || pos.getY() != y1 || pos.getZ() != z1) {
                EntitySeat seat = new EntitySeat(ship.worldObj);
                seat.setParentShip(ship, pos);
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
        if (seats != null && !seats.isEmpty()) {
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
                seat.killedBy(this);
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
