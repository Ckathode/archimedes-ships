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
    private int ticksTillShipCheck;

    public EntitySeat(World world) {
        super(world);
        ticksTillShipCheck = 0;
        ship = null;
        pos = null;
        prevRiddenByEntity = null;
        yOffset = 0f;
        setSize(0F, 0F);
    }

    /**
     * Called from ShipCapabilities.
     *
     * @param player
     * @return
     * @ShipCapabilities
     */
    @Override
    public boolean interactFirst(EntityPlayer player) {
        checkShipOpinion();

        if (riddenByEntity == null) {
            player.mountEntity(null);
            player.setSneaking(false);
            player.mountEntity(this);
            return true;
        } else {
            return false;
        }
    }


    /**
     * Sets the parent ship as well as chunkposition.
     *
     * @param entityship
     * @param x
     * @param y
     * @param z
     */
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

    public ChunkPosition getPos() {
        return pos;
    }

    public void checkShipOpinion() {
        if (ship != null && ship.getCapabilities() != null && !((ShipCapabilities) ship.getCapabilities()).hasSeat(this)) {
            if (riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) riddenByEntity;
                EntitySeat seat = ((ShipCapabilities) ship.getCapabilities()).getAvailableSeat();
                if (seat != null) {
                    player.mountEntity(null);
                    player.mountEntity(seat);
                    EntityParachute parachute = new EntityParachute(worldObj, ship, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
                    if (worldObj.spawnEntityInWorld(parachute)) {
                        player.mountEntity(parachute);
                        player.setSneaking(false);
                    }
                }
                setDead();
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj == null)
            return;

        if (worldObj.isRemote) {
            if (!this.dataWatcher.getIsBlank() && this.dataWatcher.getWatchableObjectByte(10) == new Byte((byte) 1)) {
                if (this.dataWatcher.getWatchableObjectInt(6) != 0) {
                    ship = (EntityShip) worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(6));
                    pos = new ChunkPosition(this.dataWatcher.getWatchableObjectByte(7),
                            this.dataWatcher.getWatchableObjectByte(8),
                            this.dataWatcher.getWatchableObjectByte(9));
                }
            }
            if (this.dataWatcher.hasChanges() && this.dataWatcher.getWatchableObjectInt(6) != 0) {
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
            } else {
                prevRiddenByEntity = riddenByEntity;
            }
            ticksTillShipCheck++;
            if (ticksTillShipCheck >= 40) {
                if (ship != null)
                    ticksTillShipCheck = 0;
            }
        }

        if (riddenByEntity != null && riddenByEntity.ridingEntity != this) {
            Entity rider = riddenByEntity;
            rider.mountEntity(null);
            rider.mountEntity(this);
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
    protected void readEntityFromNBT(NBTTagCompound compound) {
        checkShipOpinion();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {

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