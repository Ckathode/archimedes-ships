package com.tridevmc.davincisvessels.common.entity;

import com.google.common.collect.ImmutableList;
import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.api.block.IBlockBalloon;
import com.tridevmc.davincisvessels.common.api.block.IBlockCustomMass;
import com.tridevmc.davincisvessels.common.api.tileentity.ITileEngineModifier;
import com.tridevmc.davincisvessels.common.tileentity.AnchorInstance;
import com.tridevmc.davincisvessels.common.tileentity.BlockLocation;
import com.tridevmc.davincisvessels.common.tileentity.TileAnchorPoint;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import com.tridevmc.movingworld.common.chunk.LocatedBlock;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import com.tridevmc.movingworld.common.entity.MovingWorldCapabilities;
import com.tridevmc.movingworld.common.util.FloodFiller;
import com.tridevmc.movingworld.common.util.LocatedBlockList;
import com.tridevmc.movingworld.common.util.MaterialDensity;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

public class VesselCapabilities extends MovingWorldCapabilities {

    private final EntityVessel vessel;
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

    public VesselCapabilities(EntityMovingWorld movingWorld, boolean autoCalcMass) {
        super(movingWorld, autoCalcMass);
        vessel = (EntityVessel) movingWorld;
        seats = new ArrayList<>();
    }

    @Override
    public float getMass() {
        return mass;
    }

    @Override
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
        return vessel.getDataManager().get(EntityVessel.ENGINE_POWER);
    }

    public ImmutablePair<LocatedBlock, LocatedBlock> findClosestValidAnchor(int radius) {
        LocatedBlock closest = LocatedBlock.AIR;
        LocatedBlock vesselAnchor = LocatedBlock.AIR;
        int smallestOverallDistance = Integer.MAX_VALUE;

        if (anchorPoints != null) {
            for (LocatedBlock anchorLB : anchorPoints) {
                TileAnchorPoint anchorTile = (TileAnchorPoint) anchorLB.tile;
                AnchorInstance anchor = anchorTile.getInstance();
                if (anchor.getRelatedAnchors().isEmpty())
                    continue;

                Iterator<Map.Entry<UUID, BlockLocation>> relationIterator = anchor.getRelatedAnchors().entrySet().iterator();
                while (relationIterator.hasNext()) {
                    Map.Entry<UUID, BlockLocation> relation = relationIterator.next();
                    if (relation.getValue().getDim() == vessel.world.getDimension().getType()) {
                        TileEntity relatedTile = vessel.world.getTileEntity(relation.getValue().getPos());
                        if (relatedTile instanceof TileAnchorPoint) {
                            TileAnchorPoint relatedAnchor = (TileAnchorPoint) relatedTile;
                            if (relatedAnchor.getInstance().getRelatedAnchors().containsKey(anchor.getIdentifier())
                                    && relatedAnchor.getInstance().getType().equals(AnchorInstance.InstanceType.LAND)) {
                                int xDist = (int) Math.abs(Math.round(vessel.posX) - relatedAnchor.getPos().getX());
                                int yDist = (int) Math.abs(Math.round(vessel.posY) - relatedAnchor.getPos().getY());
                                int zDist = (int) Math.abs(Math.round(vessel.posZ) - relatedAnchor.getPos().getZ());
                                if (!(xDist > radius || yDist > radius || zDist > radius)) {
                                    int collectiveDist = xDist + yDist + zDist;
                                    if (collectiveDist < smallestOverallDistance) {
                                        smallestOverallDistance = collectiveDist;
                                        closest = new LocatedBlock(vessel.getEntityWorld().getBlockState(relatedTile.getPos()), relatedTile, relatedTile.getPos());
                                        vesselAnchor = anchorLB;
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
        return new ImmutablePair<>(vesselAnchor, closest);
    }

    public void updateEngines() {
        float ePower = 0;
        if (engines != null) {
            for (ITileEngineModifier te : engines) {
                ePower += te.getPowerIncrement(this);
            }
        }
        if (!vessel.world.isRemote)
            vessel.getDataManager().set(EntityVessel.ENGINE_POWER, ePower);
    }

    @Override
    public boolean canFly() {
        return (DavincisVesselsMod.CONFIG.enableAirVessels && getBalloonCount() >= blockCount * DavincisVesselsMod.CONFIG.flyBalloonRatio)
                || vessel.areSubmerged();
    }

    public boolean canSubmerge() {
        if (!submerseFound) {
            FloodFiller floodFiller = new FloodFiller();
            LocatedBlockList filledBlocks = floodFiller.floodFillMobileChunk(vessel.getMobileChunk());
            int filledBlockCount = filledBlocks.size();

            canSubmerge = false;
            if (DavincisVesselsMod.CONFIG.enableSubmersibles)
                canSubmerge =
                        filledBlockCount < (nonAirBlockCount * DavincisVesselsMod.CONFIG.submersibleFillRatio);
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

    public ImmutableList<EntitySeat> getSeats() {
        return ImmutableList.copyOf(seats);
    }

    public void addSeat(EntitySeat entity) {
        if (entity instanceof EntitySeat) seats.add(entity);
    }

    public boolean canMove() {
        return vessel.getDataManager().get(EntityVessel.CAN_MOVE);
    }

    public List<ITileEngineModifier> getEngines() {
        return engines;
    }

    public boolean hasSeat(EntitySeat seat) {
        if (seats != null && !seats.isEmpty()) {
            return seats.contains(seat);
        } else {
            return true;
        }
    }

    public EntitySeat getAvailableSeat() {
        if (seats.stream().anyMatch(seat -> seat.getControllingPassenger() == null))
            return seats.stream().filter(seat -> seat.getControllingPassenger() == null).findFirst().get();
        return null;
    }

    @Override
    public boolean mountEntity(Entity player) {
        if (player.isSneaking()) {
            return false;
        } else if (vessel.isBeingRidden()) {
            if (player instanceof PlayerEntity) {
                tryMountSeat((PlayerEntity) player);
            }
            return true;
        } else {
            if (!vessel.world.isRemote) {
                player.startRiding(vessel);
            }
            return true;
        }
    }

    private void tryMountSeat(PlayerEntity player) {
        EntitySeat seat = getAvailableSeat();
        if (seat != null) {
            player.startRiding(seat);
        }
    }

    public void spawnSeatEntities() {
        if (seats != null)
            seats.forEach(seat -> vessel.world.addEntity(seat));
    }

    @Override
    public void onChunkBlockAdded(BlockState state, BlockPos pos) {
        mass += MaterialDensity.getDensity(state);

        blockCount++;
        nonAirBlockCount++;
        TileEntity tile = null;
        if (vessel != null && vessel.getMobileChunk() != null)
            tile = vessel.getMobileChunk().getTileEntity(pos);

        Block block = state.getBlock();
        if (block == null) {
            nonAirBlockCount--;
            return;
        }

        if (block instanceof AirBlock)
            nonAirBlockCount--;

        if (block instanceof IBlockCustomMass) {
            mass -= MaterialDensity.getDensity(state); // Custom mass found, remove the mass assumed and substitute with custom mass.
            mass += ((IBlockCustomMass) block).getCustomMass();
        }

        if (block instanceof IBlockBalloon) {
            balloonCount += ((IBlockBalloon) block).getBalloonWorth(tile);
        } else if (DavincisVesselsMod.BLOCK_CONFIG.isBalloon(block)) {
            balloonCount++;
        } else if (block == DavincisVesselsMod.CONTENT.blockFloater) {
            floaters++;
        } else if (block == DavincisVesselsMod.CONTENT.blockAnchorPoint) {
            TileEntity te = vessel.getMobileChunk().getTileEntity(pos);
            if (te instanceof TileAnchorPoint && ((TileAnchorPoint) te).getInstance() != null
                    && ((TileAnchorPoint) te).getInstance().getType().equals(AnchorInstance.InstanceType.VESSEL)) {
                if (anchorPoints == null) {
                    anchorPoints = new ArrayList<>();
                }
                anchorPoints.add(new LocatedBlock(state, te, pos));
            }
        } else if (block == DavincisVesselsMod.CONTENT.blockEngine) {
            TileEntity te = vessel.getMobileChunk().getTileEntity(pos);
            if (te instanceof ITileEngineModifier) {
                if (engines == null) {
                    engines = new ArrayList<>(4);
                }
                engines.add((ITileEngineModifier) te);
            }
        } else if (block == DavincisVesselsMod.CONTENT.blockSeat || DavincisVesselsMod.BLOCK_CONFIG.isSeat(block)) {
            int x1 = vessel.riderDestination.getX(), y1 = vessel.riderDestination.getY(), z1 = vessel.riderDestination.getZ();
            switch (vessel.frontDirection) {
                case SOUTH: {
                    z1 -= 1;
                    break;
                }
                case WEST: {
                    x1 += 1;
                    break;
                }
                case NORTH: {
                    z1 += 1;
                    break;
                }
                case EAST: {
                    x1 -= 1;
                    break;
                }
            }

            if (pos.getX() != x1 || pos.getY() != y1 || pos.getZ() != z1) {
                EntitySeat seat = new EntitySeat(vessel.world);
                seat.setupVessel(vessel, pos);
                addSeat(seat);
            }
        }
    }

    @Override
    public void postBlockAdding() {
        if (vessel.getMobileChunk() != null && vessel.getMobileChunk().marker != null
                && vessel.getMobileChunk().marker.tile instanceof TileHelm) {
            if (((TileHelm) vessel.getMobileChunk().marker.tile).submerge && canSubmerge()) {
                vessel.setSubmerge(true);
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
            seats.forEach(Entity::remove);
            seats.clear();
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
        return (float) DavincisVesselsMod.CONFIG.speedLimit;
    }

    @Override
    public float getBankingMultiplier() {
        return (float) DavincisVesselsMod.CONFIG.bankingMultiplier;
    }

}
