package darkevilmac.archimedes.common.entity;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.api.block.IBlockBalloon;
import darkevilmac.archimedes.common.api.block.IBlockCustomMass;
import darkevilmac.archimedes.common.api.tileentity.ITileEngineModifier;
import darkevilmac.archimedes.common.object.ArchimedesObjects;
import darkevilmac.archimedes.common.object.block.AnchorPointLocation;
import darkevilmac.archimedes.common.tileentity.TileEntityAnchorPoint;
import darkevilmac.archimedes.common.tileentity.TileEntityHelm;
import darkevilmac.movingworld.common.chunk.LocatedBlock;
import darkevilmac.movingworld.common.entity.EntityMovingWorld;
import darkevilmac.movingworld.common.entity.MovingWorldCapabilities;
import darkevilmac.movingworld.common.util.FloodFiller;
import darkevilmac.movingworld.common.util.LocatedBlockList;
import darkevilmac.movingworld.common.util.MaterialDensity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ShipCapabilities extends MovingWorldCapabilities {

    private final EntityShip ship;
    public float speedMultiplier, rotationMultiplier, liftMultiplier;
    public float brakeMult;
    private float mass;
    private List<LocatedBlock> anchorPoints;
    private List<EntitySeat> seats;
    private List<ITileEngineModifier> engines;
    private int balloonCount;
    private int floaters;
    private int blockCount;
    private int nonAirBlockCount;

    private boolean canSubmerge = false;
    private boolean submerseFound = false;

    public ShipCapabilities(EntityMovingWorld movingWorld, boolean autoCalcMass) {
        super(movingWorld, autoCalcMass);
        ship = (EntityShip) movingWorld;
    }

    @Override
    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getSpeedMult() {
        return speedMultiplier + getEnginePower() * 0.5F;
    }

    public float getRotationMult() {
        return rotationMultiplier + getEnginePower() * 0.25f;
    }

    public float getLiftMult() {
        return liftMultiplier + getEnginePower() * 0.5f;
    }

    public float getEnginePower() {
        return ship.getDataManager().get(EntityShip.ENGINE_POWER);
    }

    public AnchorPointLocation findClosestValidAnchor(int range) {
        if (ship != null && ship.worldObj != null && !ship.worldObj.isRemote) {
            if (anchorPoints != null) {
                AnchorPointLocation apLoc = new AnchorPointLocation(null, null);
                List<AnchorPointLocation> validAnchorPoints = new ArrayList<AnchorPointLocation>();

                List<Integer> validAnchorPointsDistance = new ArrayList<Integer>();
                for (LocatedBlock anchorPointLB : anchorPoints) {
                    TileEntityAnchorPoint.AnchorPointInfo anchorPointInfo = ((TileEntityAnchorPoint) anchorPointLB.tileEntity).anchorPointInfo;

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
                        TileEntityAnchorPoint anchorPoint = (TileEntityAnchorPoint) ship.worldObj.getTileEntity(anchorPointInfo.linkPos);
                        if (anchorPoint.anchorPointInfo != null && !anchorPoint.anchorPointInfo.forShip) {
                            AnchorPointLocation anchorPointLocation = new AnchorPointLocation(null, null);
                            anchorPointLocation.worldAnchor = new LocatedBlock(anchorPoint.getWorld().getBlockState(anchorPoint.getPos()), anchorPoint, anchorPoint.getPos());
                            anchorPointLocation.shipAnchor = anchorPointLB;
                            validAnchorPoints.add(anchorPointLocation);
                            validAnchorPointsDistance.add(differenceX + differenceY + differenceZ);
                        }
                    }
                }

                AnchorPointLocation shortestAnchorLocation = null;

                if (validAnchorPoints != null && !validAnchorPoints.isEmpty()) {
                    int shortestIndex = 0;
                    for (int index = 0; index < validAnchorPoints.size(); index++) {
                        if (validAnchorPointsDistance.get(index) < validAnchorPointsDistance.get(shortestIndex)) {
                            shortestIndex = index;
                        }
                    }
                    shortestAnchorLocation = validAnchorPoints.get(shortestIndex);
                }

                return shortestAnchorLocation;
            }
        }
        return null;
    }

    public void updateEngines() {
        float ePower = 0;
        if (engines != null) {
            for (ITileEngineModifier te : engines) {
                ePower += te.getPowerIncrement(this);
            }
        }
        if (!ship.worldObj.isRemote)
            ship.getDataManager().set(EntityShip.ENGINE_POWER, ePower);
    }

    @Override
    public boolean canFly() {
        return (ArchimedesShipMod.instance.getNetworkConfig().getShared().enableAirShips && getBalloonCount() >= blockCount * ArchimedesShipMod.instance.getNetworkConfig().getShared().flyBalloonRatio)
                || ship.areSubmerged();
    }

    public boolean canSubmerge() {
        if (!submerseFound) {
            FloodFiller floodFiller = new FloodFiller();
            LocatedBlockList filledBlocks = floodFiller.floodFillMobileChunk(ship.getMobileChunk());
            int filledBlockCount = filledBlocks.size();

            canSubmerge = false;
            if (ArchimedesShipMod.instance.getNetworkConfig().getShared().enableSubmersibles)
                canSubmerge =
                        filledBlockCount < (nonAirBlockCount * ArchimedesShipMod.instance.getNetworkConfig().getShared().submersibleFillRatio);
            submerseFound = true;
        }

        return canSubmerge;
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

    public void addAttachments(EntitySeat entity) {
        if (seats == null) seats = new ArrayList<EntitySeat>();
        if (entity != null && entity instanceof EntitySeat) seats.add(entity);
    }

    public boolean canMove() {
        return ship.getDataManager().get(EntityShip.HAS_ENGINES) == 1;
    }

    public List<EntitySeat> getAttachments() {
        return seats;
    }

    public List<ITileEngineModifier> getEngines() {
        return engines;
    }

    public List<LocatedBlock> getAnchorPoints() {
        return anchorPoints;
    }

    @Override
    public void onChunkBlockAdded(IBlockState state, BlockPos pos) {
        mass += MaterialDensity.getDensity(state);

        blockCount++;
        nonAirBlockCount++;
        TileEntity tile = null;
        if (ship != null && ship.getMobileChunk() != null)
            tile = ship.getMobileChunk().getTileEntity(pos);

        Block block = state.getBlock();
        if (block == null) {
            nonAirBlockCount--;
            return;
        }

        if (block instanceof BlockAir)
            nonAirBlockCount--;

        if (block instanceof IBlockCustomMass) {
            mass -= MaterialDensity.getDensity(state); // Custom mass found, remove the mass assumed and substitute with custom mass.
            mass += ((IBlockCustomMass) block).getCustomMass();
        }

        if (block instanceof IBlockBalloon) {
            balloonCount += ((IBlockBalloon) block).getBalloonWorth(tile);
        } else if (block == ArchimedesObjects.blockBalloon) {
            balloonCount++;
        } else if (ArchimedesShipMod.instance.getNetworkConfig().isBalloon(block)) {
            balloonCount++;
        } else if (block == ArchimedesObjects.blockFloater) {
            floaters++;
        } else if (block == ArchimedesObjects.blockAnchorPoint) {
            TileEntity te = ship.getMobileChunk().getTileEntity(pos);
            if (te != null && te instanceof TileEntityAnchorPoint && ((TileEntityAnchorPoint) te).anchorPointInfo != null && ((TileEntityAnchorPoint) te).anchorPointInfo.forShip) {
                if (anchorPoints == null) {
                    anchorPoints = new ArrayList<LocatedBlock>();
                }
                anchorPoints.add(new LocatedBlock(state, te, pos));
            }
        } else if (block == ArchimedesObjects.blockEngine) {
            TileEntity te = ship.getMobileChunk().getTileEntity(pos);
            if (te instanceof ITileEngineModifier) {
                if (engines == null) {
                    engines = new ArrayList<ITileEngineModifier>(4);
                }
                engines.add((ITileEngineModifier) te);
            }
        } else if (block == ArchimedesObjects.blockSeat || ArchimedesShipMod.instance.getNetworkConfig().isSeat(block) && !ship.worldObj.isRemote) {
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

    @Override
    public void postBlockAdding() {
        if (ship.getMobileChunk() != null && ship.getMobileChunk().marker != null && ship.getMobileChunk().marker.tileEntity != null && ship.getMobileChunk().marker.tileEntity instanceof TileEntityHelm) {
            if (((TileEntityHelm) ship.getMobileChunk().marker.tileEntity).submerge && canSubmerge()) {
                ship.setSubmerge(true);
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
            if (seat.getControllingPassenger() == null || (seat.getControllingPassenger() != null &&
                    (seat.getControllingPassenger().getRidingEntity() == null ||
                            (seat.getControllingPassenger().getRidingEntity() != null &&
                                    seat.getControllingPassenger().getRidingEntity() != seat)))) {
                seat.startRiding(null);
                return seat;
            }
        }
        return null;
    }

    @Override
    public boolean mountEntity(Entity entity) {
        if (seats == null || entity == null || !(entity instanceof EntityPlayer)) {
            return false;
        }

        EntityPlayer player = (EntityPlayer) entity;

        for (EntitySeat seat : seats) {
            if (seat.processInitialInteract(player, player.getActiveItemStack(), player.getActiveHand()))
                return true;
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
        submerseFound = false;
        canSubmerge = false;
        clearBlockCount();
    }

    @Override
    public float getSpeedLimit() {
        return ArchimedesShipMod.instance.getNetworkConfig().getShared().speedLimit;
    }

    @Override
    public float getBankingMultiplier() {
        return ArchimedesShipMod.instance.getNetworkConfig().getShared().bankingMultiplier;
    }


}
