package darkevilmac.archimedes.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class EntitySeat extends Entity implements IEntityAdditionalSpawnData {

    private EntityShip ship;
    private ChunkPosition pos;
    private Entity prevRiddenByEntity;

    public EntitySeat(World world) {
        super(world);
        ship = null;
        pos = null;
        prevRiddenByEntity = null;
        yOffset = 0f;
        setSize(0F, 0F);
    }

    /**
     * Called from ShipCapabilities.
     * @ShipCapabilities
     *
     * @param player
     * @return
     */
    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (this.riddenByEntity != null && this.riddenByEntity.ridingEntity != this) {
            this.riddenByEntity = null;
        }

        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player) {
            return true;
        } else {
            player.mountEntity(this);
            return true;
        }
    }

    public void setParentShip(EntityShip entityship, int x, int y, int z) {
        ship = entityship;
        if (entityship != null) {
            pos = new ChunkPosition(x, y, z);
            setLocationAndAngles(entityship.posX, entityship.posY, entityship.posZ, 0F, 0F);
            if (worldObj != null && !worldObj.isRemote) {
                if (!this.dataWatcher.getIsBlank() && this.dataWatcher.getWatchableObjectByte(10) == new Byte((byte) 1)) {
                    this.dataWatcher.updateObject(6, entityship.getEntityId());
                    this.dataWatcher.updateObject(7, new Byte((byte) (x & 0xFF)));
                    this.dataWatcher.updateObject(8, new Byte((byte) (y & 0xFF)));
                    this.dataWatcher.updateObject(9, new Byte((byte) (z & 0xFF)));
                } else {
                    this.dataWatcher.addObject(6, entityship.getEntityId());
                    this.dataWatcher.addObject(7, new Byte((byte) (x & 0xFF)));
                    this.dataWatcher.addObject(8, new Byte((byte) (y & 0xFF)));
                    this.dataWatcher.addObject(9, new Byte((byte) (z & 0xFF)));
                    this.dataWatcher.addObject(10, 1);
                }
            }
        }
    }

    public EntityShip getParentShip() {
        return ship;
    }

    public ChunkPosition getChunkPosition() {
        return pos;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj == null)
            return;

        if (worldObj.isRemote && !this.dataWatcher.getIsBlank() && this.dataWatcher.getWatchableObjectByte(10) == new Byte((byte) 1)) {
            if (this.dataWatcher.getWatchableObjectInt(6) != 0) {
                ship = (EntityShip) worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(6));
                pos = new ChunkPosition(this.dataWatcher.getWatchableObjectByte(7),
                        this.dataWatcher.getWatchableObjectByte(8),
                        this.dataWatcher.getWatchableObjectByte(9));
            }
        }

        if (worldObj.isRemote && this.dataWatcher.hasChanges()) {
            if (this.dataWatcher.getWatchableObjectInt(6) != 0) {
                ship = (EntityShip) worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(6));
                pos = new ChunkPosition(this.dataWatcher.getWatchableObjectByte(7),
                        this.dataWatcher.getWatchableObjectByte(8),
                        this.dataWatcher.getWatchableObjectByte(9));
            }
        }

        if (ship != null) {
            setPosition(ship.posX, ship.posY, ship.posZ);
        }

        if (!worldObj.isRemote) {
            if (riddenByEntity == null) {
                if (prevRiddenByEntity != null) {
                    if (ship != null && ship.isFlying()) {
                        EntityParachute parachute = new EntityParachute(worldObj, ship, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
                        if (worldObj.spawnEntityInWorld(parachute)) {
                            prevRiddenByEntity.mountEntity(parachute);
                            prevRiddenByEntity.setSneaking(false);
                        }
                    }
                    prevRiddenByEntity = null;
                }
            }

            if (riddenByEntity != null) {
                prevRiddenByEntity = riddenByEntity;
            }
        }
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(6, 0);
        this.dataWatcher.addObject(7, new Byte((byte) (0 & 0xFF)));
        this.dataWatcher.addObject(8, new Byte((byte) (0 & 0xFF)));
        this.dataWatcher.addObject(9, new Byte((byte) (0 & 0xFF)));
        this.dataWatcher.addObject(10, new Byte((byte) 1));
    }

    @Override
    public void updateRiderPosition() {
        if (ship != null) {
            ship.updateRiderPosition(riddenByEntity, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, 0);
        }
    }

    @Override
    public double getMountedYOffset() {
        return yOffset + 0.5d;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return null;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (ship == null) {
            compound.setInteger("shipID", 0);
            compound.setByte("cPosX", (byte) 0);
            compound.setByte("cPosY", (byte) 0);
            compound.setByte("cPosZ", (byte) 0);
            return;
        }
        compound.setInteger("shipID", ship.getEntityId());
        compound.setByte("cPosX", (byte) (pos.chunkPosX & 0xFF));
        compound.setByte("cPosY", (byte) (pos.chunkPosY & 0xFF));
        compound.setByte("cPosZ", (byte) (pos.chunkPosZ & 0xFF));
    }


    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        int entityID = compound.getInteger("shipID");
        int posChunkX = compound.getByte("cPosX");
        int posChunkY = compound.getByte("cPosY");
        int posChunkZ = compound.getByte("cPosZ");
        if (entityID != 0) {
            Entity entity = worldObj.getEntityByID(entityID);
            if (entity instanceof EntityShip) {
                setParentShip((EntityShip) entity, posChunkX, posChunkY, posChunkZ);
            }
        }
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        if (ship == null) {
            data.writeInt(0);
            data.writeByte(0);
            data.writeByte(0);
            data.writeByte(0);
            return;
        }
        data.writeInt(ship.getEntityId());
        data.writeByte(pos.chunkPosX & 0xFF);
        data.writeByte(pos.chunkPosY & 0xFF);
        data.writeByte(pos.chunkPosZ & 0xFF);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        int entityID = data.readInt();
        int posChunkX = data.readUnsignedByte();
        int posChunkY = data.readUnsignedByte();
        int posChunkZ = data.readUnsignedByte();
        if (entityID != 0) {
            Entity entity = worldObj.getEntityByID(entityID);
            if (entity instanceof EntityShip) {
                setParentShip((EntityShip) entity, posChunkX, posChunkY, posChunkZ);
            }
        }
    }
}