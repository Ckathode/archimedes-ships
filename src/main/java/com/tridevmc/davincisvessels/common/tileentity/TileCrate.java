package com.tridevmc.davincisvessels.common.tileentity;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import com.tridevmc.movingworld.api.IMovingTile;
import com.tridevmc.movingworld.common.chunk.mobilechunk.MobileChunk;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileCrate extends TileEntity implements IMovingTile, ITickable {
    private EntityVessel parentVessel;
    private int containedEntityId;
    private Entity containedEntity;
    private int refreshTime;
    private BlockPos chunkPos;

    public TileCrate() {
        super(DavincisVesselsMod.CONTENT.tileTypes.get(TileCrate.class));
        parentVessel = null;
        containedEntityId = 0;
        containedEntity = null;
        refreshTime = 0;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld movingWorld, BlockPos pos) {
        chunkPos = pos;
        parentVessel = (EntityVessel) movingWorld;
    }

    @Override
    public EntityVessel getParentMovingWorld() {
        return parentVessel;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(entityMovingWorld, new BlockPos(BlockPos.ZERO));
    }

    @Override
    public BlockPos getChunkPos() {
        return chunkPos;
    }

    @Override
    public void setChunkPos(BlockPos chunkPos) {
        this.chunkPos = chunkPos;
    }

    @Override
    public void tick(MobileChunk mobileChunk) {
        // We'll try using this experimental tick function (sort of experimental) to keep the entity on the vessel...?
        if (containedEntity == null) {
            if (refreshTime > 0) {
                refreshTime--;
            }
        } else if (!containedEntity.isAlive()) {
            setContainedEntity(null);
        } else {
            containedEntity.setMotion(0, 0, 0);
            if (parentVessel == null) {
                containedEntity.setPosition(pos.getX() + 0.5d, pos.getY() + 0.15f + containedEntity.getYOffset(), pos.getZ() + 0.5d);
            } else {
                parentVessel.updatePassengerPosition(containedEntity, pos, 2);
            }

            if (containedEntity.hurtResistantTime > 0 || containedEntity.isSneaking()) {
                containedEntity.posY += 1d;
                releaseEntity();
            }
        }
    }

    public boolean canCatchEntity() {
        return refreshTime == 0;
    }

    public void releaseEntity() {
        setContainedEntity(null);
        refreshTime = 60;
    }

    public Entity getContainedEntity() {
        return containedEntity;
    }

    public void setContainedEntity(Entity entity) {
        containedEntity = entity;
        containedEntityId = containedEntity == null ? 0 : containedEntity.getEntityId();
        refreshTime = 0;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT compound = new CompoundNBT();
        write(compound);
        return new SUpdateTileEntityPacket(pos, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        read(packet.getNbtCompound());
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (tag.contains("contained")) {
            if (world == null) {
                containedEntityId = tag.getInt("contained");
            } else {
                setContainedEntity(world.getEntityByID(tag.getInt("contained")));
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);
        if (containedEntity != null) {
            tag.putInt("contained", containedEntity.getEntityId());
        }
        return tag;
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            if (parentVessel != null && !parentVessel.isAlive()) {
                parentVessel = null;
            }
            if (containedEntity == null) {
                if (containedEntityId != 0) {
                    setContainedEntity(world.getEntityByID(containedEntityId));
                }
            }
        }

        if (containedEntity == null) {
            if (refreshTime > 0) {
                refreshTime--;
            }
        } else if (!containedEntity.isAlive()) {
            setContainedEntity(null);
        } else {
            containedEntity.setMotion(0, 0, 0);
            if (parentVessel == null) {
                containedEntity.setPosition(pos.getX() + 0.5d, pos.getY() + 0.15f + containedEntity.getYOffset(), pos.getZ() + 0.5d);
            } else {
                parentVessel.updatePassengerPosition(containedEntity, pos, 2);
            }

            if (containedEntity.hurtResistantTime > 0 || containedEntity.isSneaking()) {
                containedEntity.posY += 1d;
                releaseEntity();
            }
        }
    }
}
