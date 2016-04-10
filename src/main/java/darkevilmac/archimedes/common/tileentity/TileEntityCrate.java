package darkevilmac.archimedes.common.tileentity;

import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.movingworld.common.chunk.mobilechunk.MobileChunk;
import darkevilmac.movingworld.common.entity.EntityMovingWorld;
import darkevilmac.movingworld.common.tile.IMovingWorldTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ITickable;

public class TileEntityCrate extends TileEntity implements IMovingWorldTileEntity, ITickable {
    private EntityShip parentShip;
    private int containedEntityId;
    private Entity containedEntity;
    private int refreshTime;

    public TileEntityCrate() {
        parentShip = null;
        containedEntityId = 0;
        containedEntity = null;
        refreshTime = 0;
    }

    @Override
    public void setParentMovingWorld(BlockPos pos, EntityMovingWorld entityMovingWorld) {
        parentShip = (EntityShip) entityMovingWorld;
    }

    @Override
    public EntityShip getParentMovingWorld() {
        return parentShip;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(new BlockPos(BlockPos.ORIGIN), entityMovingWorld);
    }

    @Override
    public void tick(MobileChunk mobileChunk) {
        // We'll try using this experimental tick function (sort of experimental) to keep the entity on the ship...?
        if (containedEntity == null) {
            if (refreshTime > 0) {
                refreshTime--;
            }
        } else if (containedEntity.isDead) {
            setContainedEntity(null);
        } else {
            containedEntity.motionX = containedEntity.motionY = containedEntity.motionZ = 0d;
            if (parentShip == null) {
                containedEntity.setPosition(pos.getX() + 0.5d, pos.getY() + 0.15f + containedEntity.getYOffset(), pos.getZ() + 0.5d);
            } else {
                parentShip.updatePassengerPosition(containedEntity, pos, 2);
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
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new SPacketUpdateTileEntity(pos, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("contained")) {
            if (worldObj == null) {
                containedEntityId = compound.getInteger("contained");
            } else {
                setContainedEntity(worldObj.getEntityByID(compound.getInteger("contained")));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (containedEntity != null) {
            compound.setInteger("contained", containedEntity.getEntityId());
        }
    }

    @Override
    public void update() {
        if (worldObj.isRemote) {
            if (parentShip != null && parentShip.isDead) {
                parentShip = null;
            }
            if (containedEntity == null) {
                if (containedEntityId != 0) {
                    setContainedEntity(worldObj.getEntityByID(containedEntityId));
                }
            }
        }

        if (containedEntity == null) {
            if (refreshTime > 0) {
                refreshTime--;
            }
        } else if (containedEntity.isDead) {
            setContainedEntity(null);
        } else {
            containedEntity.motionX = containedEntity.motionY = containedEntity.motionZ = 0d;
            if (parentShip == null) {
                containedEntity.setPosition(pos.getX() + 0.5d, pos.getY() + 0.15f + containedEntity.getYOffset(), pos.getZ() + 0.5d);
            } else {
                parentShip.updatePassengerPosition(containedEntity, pos, 2);
            }

            if (containedEntity.hurtResistantTime > 0 || containedEntity.isSneaking()) {
                containedEntity.posY += 1d;
                releaseEntity();
            }
        }
    }
}
