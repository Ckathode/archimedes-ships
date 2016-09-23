package io.github.elytra.davincisvessels.common.tileentity;

import io.github.elytra.movingworld.api.IMovingWorldTileEntity;
import io.github.elytra.movingworld.common.chunk.mobilechunk.MobileChunk;
import io.github.elytra.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityGauge extends TileEntity implements IMovingWorldTileEntity {
    public EntityMovingWorld parentShip;
    private BlockPos chunkPos;

    public TileEntityGauge() {
        parentShip = null;
    }

    @Override
    public void setParentMovingWorld(BlockPos pos, EntityMovingWorld entityMovingWorld) {
        this.chunkPos = pos;
        parentShip = entityMovingWorld;
    }

    @Override
    public EntityMovingWorld getParentMovingWorld() {
        return parentShip;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(new BlockPos(BlockPos.ORIGIN), entityMovingWorld);
    }

    @Override
    public BlockPos getChunkPos() {
        return chunkPos;
    }

    @Override
    public void tick(MobileChunk mobileChunk) {
        // No implementation
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("vehicle") && worldObj != null) {
            int id = tag.getInteger("vehicle");
            Entity entity = worldObj.getEntityByID(id);
            if (entity instanceof EntityMovingWorld) {
                parentShip = (EntityMovingWorld) entity;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return super.writeToNBT(tag);
    }

}
