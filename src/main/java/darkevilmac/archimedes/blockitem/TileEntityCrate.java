package darkevilmac.archimedes.blockitem;

import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.entity.IMovingWorldTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCrate extends TileEntity implements IMovingWorldTileEntity {
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
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld, int x, int y, int z) {
        parentShip = (EntityShip) entityMovingWorld;
    }

    @Override
    public EntityShip getParentMovingWorld() {
        return parentShip;
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
    public boolean canUpdate() {
        return true;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
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
    public void updateEntity() {
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
                containedEntity.setPosition(xCoord + 0.5d, yCoord + 0.15f + containedEntity.getYOffset(), zCoord + 0.5d);
            } else {
                parentShip.updateRiderPosition(containedEntity, xCoord, yCoord, zCoord, 2);
            }

            if (containedEntity.hurtResistantTime > 0 || containedEntity.isSneaking()) {
                containedEntity.posY += 1d;
                releaseEntity();
            }
        }
    }
}
