package io.github.elytra.davincisvessels.common.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.common.api.block.IBlockBalloon;
import io.github.elytra.davincisvessels.common.api.block.IBlockCustomMass;
import io.github.elytra.davincisvessels.common.api.tileentity.ITileEngineModifier;
import io.github.elytra.davincisvessels.common.object.DavincisVesselsObjects;
import io.github.elytra.davincisvessels.common.tileentity.AnchorInstance;
import io.github.elytra.davincisvessels.common.tileentity.BlockLocation;
import io.github.elytra.davincisvessels.common.tileentity.TileAnchorPoint;
import io.github.elytra.davincisvessels.common.tileentity.TileHelm;
import io.github.elytra.movingworld.common.chunk.LocatedBlock;
import io.github.elytra.movingworld.common.entity.EntityMovingWorld;
import io.github.elytra.movingworld.common.entity.MovingWorldCapabilities;
import io.github.elytra.movingworld.common.util.FloodFiller;
import io.github.elytra.movingworld.common.util.LocatedBlockList;
import io.github.elytra.movingworld.common.util.MaterialDensity;

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
        seats = new ArrayList<>();
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
                TileAnchorPoint anchorTile = (TileAnchorPoint) anchorLB.tileEntity;
                AnchorInstance anchor = anchorTile.getInstance();
                if (anchor.getRelatedAnchors().isEmpty())
                    continue;

                Iterator<Map.Entry<UUID, BlockLocation>> relationIterator = anchor.getRelatedAnchors().entrySet().iterator();
                while (relationIterator.hasNext()) {
                    Map.Entry<UUID, BlockLocation> relation = relationIterator.next();
                    if (relation.getValue().dimID == ship.worldObj.provider.getDimension()) {
                        TileEntity relatedTile = ship.worldObj.getTileEntity(relation.getValue().pos);
                        if (relatedTile != null && relatedTile instanceof TileAnchorPoint) {
                            TileAnchorPoint relatedAnchor = (TileAnchorPoint) relatedTile;
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
        return (DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().enableAirShips && getBalloonCount() >= blockCount * DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().flyBalloonRatio)
                || ship.areSubmerged();
    }

    public boolean canSubmerge() {
        if (!submerseFound) {
            FloodFiller floodFiller = new FloodFiller();
            LocatedBlockList filledBlocks = floodFiller.floodFillMobileChunk(ship.getMobileChunk());
            int filledBlockCount = filledBlocks.size();

            canSubmerge = false;
            if (DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().enableSubmersibles)
                canSubmerge =
                        filledBlockCount < (nonAirBlockCount * DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().submersibleFillRatio);
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

    public void addSeat(EntitySeat entity) {
        if (entity != null && entity instanceof EntitySeat) seats.add(entity);
    }

    public boolean canMove() {
        return ship.getDataManager().get(EntityShip.HAS_ENGINES) == 1;
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
        if (seats.stream().allMatch(seat -> seat.getControllingPassenger() == null))
            return seats.stream().filter(seat -> seat.getControllingPassenger() == null).findFirst().get();
        return null;
    }

    @Override
    public boolean mountEntity(Entity player) {
        if (player.isSneaking()) {
            return false;
        } else if (ship.isBeingRidden()) {
            if (player instanceof EntityPlayer) {
                tryMountSeat((EntityPlayer) player);
            }
            return true;
        } else {
            if (!ship.worldObj.isRemote) {
                player.startRiding(ship);
            }
            return true;
        }
    }

    private void tryMountSeat(EntityPlayer player) {
        EntitySeat seat = getAvailableSeat();
        if (seat != null) {
            player.interact(seat, player.getActiveItemStack(), player.getActiveHand());
        }
    }

    public void spawnSeatEntities() {
        if (seats != null)
            seats.forEach(seat -> ship.worldObj.spawnEntityInWorld(seat));
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
        } else if (block == DavincisVesselsObjects.blockBalloon) {
            balloonCount++;
        } else if (DavincisVesselsMod.INSTANCE.getNetworkConfig().isBalloon(block)) {
            balloonCount++;
        } else if (block == DavincisVesselsObjects.blockFloater) {
            floaters++;
        } else if (block == DavincisVesselsObjects.blockAnchorPoint) {
            TileEntity te = ship.getMobileChunk().getTileEntity(pos);
            if (te != null && te instanceof TileAnchorPoint && ((TileAnchorPoint) te).getInstance() != null
                    && ((TileAnchorPoint) te).getInstance().getType().equals(AnchorInstance.InstanceType.FORSHIP)) {
                if (anchorPoints == null) {
                    anchorPoints = new ArrayList<>();
                }
                anchorPoints.add(new LocatedBlock(state, te, pos));
            }
        } else if (block == DavincisVesselsObjects.blockEngine) {
            TileEntity te = ship.getMobileChunk().getTileEntity(pos);
            if (te instanceof ITileEngineModifier) {
                if (engines == null) {
                    engines = new ArrayList<>(4);
                }
                engines.add((ITileEngineModifier) te);
            }
        } else if (block == DavincisVesselsObjects.blockSeat || DavincisVesselsMod.INSTANCE.getNetworkConfig().isSeat(block)) {
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
                seat.setupShip(ship, pos);
                addSeat(seat);
            }
        }
    }

    @Override
    public void postBlockAdding() {
        if (ship.getMobileChunk() != null && ship.getMobileChunk().marker != null && ship.getMobileChunk().marker.tileEntity != null && ship.getMobileChunk().marker.tileEntity instanceof TileHelm) {
            if (((TileHelm) ship.getMobileChunk().marker.tileEntity).submerge && canSubmerge()) {
                ship.setSubmerge(true);
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
            seats.forEach(seat -> seat.setDead());
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
        return DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().speedLimit;
    }

    @Override
    public float getBankingMultiplier() {
        return DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().bankingMultiplier;
    }

}
