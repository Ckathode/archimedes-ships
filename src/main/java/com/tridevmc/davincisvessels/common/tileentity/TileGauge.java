package com.tridevmc.davincisvessels.common.tileentity;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.movingworld.api.IMovingTile;
import com.tridevmc.movingworld.common.chunk.mobilechunk.MobileChunk;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileGauge extends TileEntity implements IMovingTile {
    public EntityMovingWorld parentShip;
    private BlockPos chunkPos;

    public TileGauge() {
        super(DavincisVesselsMod.CONTENT.tileTypes.get(TileHelm.class));
        parentShip = null;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld movingWorld, BlockPos pos) {
        this.chunkPos = pos;
        parentShip = movingWorld;
    }

    @Override
    public EntityMovingWorld getParentMovingWorld() {
        return parentShip;
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
        // No implementation
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT compound = new CompoundNBT();
        write(compound);
        return new SUpdateTileEntityPacket(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        read(packet.getNbtCompound());
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (tag.contains("vehicle") && world != null) {
            int id = tag.getInt("vehicle");
            Entity entity = world.getEntityByID(id);
            if (entity instanceof EntityMovingWorld) {
                parentShip = (EntityMovingWorld) entity;
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        return super.write(tag);
    }

}
