package darkevilmac.archimedes.common.entity;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.api.block.IBlockBalloon;
import darkevilmac.archimedes.common.api.block.IBlockCustomMass;
import darkevilmac.archimedes.common.api.tileentity.ITileEngineModifier;
import darkevilmac.archimedes.common.object.ArchimedesObjects;
import darkevilmac.archimedes.common.tileentity.AnchorInstance;
import darkevilmac.archimedes.common.tileentity.BlockLocation;
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
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

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

    public ImmutablePair<LocatedBlock, LocatedBlock> findClosestValidAnchor(int radius) {
        LocatedBlock closest = LocatedBlock.AIR;
        LocatedBlock shipAnchor = LocatedBlock.AIR;
        int smallestOverallDistance = Integer.MAX_VALUE;

        if (anchorPoints != null) {
            for (LocatedBlock anchorLB : anchorPoints) {
                TileEntityAnchorPoint anchorTile = (TileEntityAnchorPoint) anchorLB.tileEntity;
                AnchorInstance anchor = anchorTile.getInstance();
                if (anchor.getRelatedAnchors().isEmpty())
                    continue;

                Iterator<Map.Entry<UUID, BlockLocation>> relationIterator = anchor.getRelatedAnchors().entrySet().iterator();
                while (relationIterator.hasNext()) {
                    Map.Entry<UUID, BlockLocation> relation = relationIterator.next();
                    if (relation.getValue().dimID == ship.worldObj.provider.getDimension()) {
                        TileEntity relatedTile = ship.worldObj.getTileEntity(relation.getValue().pos);
                        if (relatedTile != null && relatedTile instanceof TileEntityAnchorPoint) {
                            TileEntityAnchorPoint relatedAnchor = (TileEntityAnchorPoint) relatedTile;
                            if (relatedAnchor.getInstance().getRelatedAnchors().containsKey(anchor.getIdentifier())
                                    && relatedAnchor.getInstance().getType().equals(AnchorInstance.InstanceType.FORLAND)) {
                                int xDist = (int) Math.abs(Math.round(ship.posX) - relatedAnchor.getPos().getX());
                                int yDist = (int) Math.abs(Math.round(ship.posY) - relatedAnchor.getPos().getY());
                                int zDist = (int) Math.abs(Math.round(ship.posZ) - relatedAnchor.getPos().getZ());
                                if (!(xDist > radius || yDist > radius || zDist > radius)) {
                                    int collectiveDist = xDist + yDist + zDist;
                                    if (collectiveDist < smallestOverallDistance) {
                                        smallestOverallDistance = collectiveDist;
                                        closest = new LocatedBlock(ship.getEntityWorld().getBlockState(relatedTile.getPos()), relatedTile, relatedTile.getPos());
                                        shipAnchor = anchorLB;
                                    }
                                }
                                continue;
                            }
                            relatedAnchor.getInstance().setChanged(true);
                        }
                    }
                    relationIterator.remove();
                }
            }
        }
        return new ImmutablePair<>(shipAnchor, closest);
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
            if (te != null && te instanceof TileEntityAnchorPoint && ((TileEntityAnchorPoint) te).getInstance() != null
                    && ((TileEntityAnchorPoint) te).getInstance().getType().equals(AnchorInstance.InstanceType.FORSHIP)) {
                if (anchorPoints == null) {
                    anchorPoints = new ArrayList<>();
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
                seat.killedBy();
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
