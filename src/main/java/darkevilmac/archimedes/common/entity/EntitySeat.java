package darkevilmac.archimedes.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntitySeat extends Entity implements IEntityAdditionalSpawnData {

    private EntityShip ship;
    private BlockPos pos;
    private Entity prevRiddenByEntity;
    private int ticksTillShipCheck;

    public EntitySeat(World world) {
        super(world);
        ticksTillShipCheck = 0;
        ship = null;
        pos = null;
        prevRiddenByEntity = null;
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
     * Sets the parent ship as well as position.
     *
     * @param entityship
     */
    public void setParentShip(EntityShip entityship, BlockPos bPos) {
        int x = bPos.getX();
        int y = bPos.getY();
        int z = bPos.getZ();
        ship = entityship;
        if (entityship != null) {
            pos = new BlockPos(x, y, z);
            setLocationAndAngles(entityship.posX, entityship.posY, entityship.posZ, 0F, 0F);
            if (worldObj != null && !worldObj.isRemote) {
                if (!this.dataWatcher.getIsBlank() && this.dataWatcher.getWatchableObjectByte(10) == new Byte((byte) 1)) {
                    this.dataWatcher.updateObject(6, entityship.getEntityId());
                    this.dataWatcher.updateObject(7, x);
                    this.dataWatcher.updateObject(8, y);
                    this.dataWatcher.updateObject(9, z);
                } else {
                    this.dataWatcher.addObject(6, entityship.getEntityId());
                    this.dataWatcher.addObject(7, x);
                    this.dataWatcher.addObject(8, y);
                    this.dataWatcher.addObject(9, z);
                    this.dataWatcher.addObject(10, new Byte((byte) 1));
                }
            }
        }
    }

    public EntityShip getParentShip() {
        return ship;
    }

    public BlockPos getPos() {
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
                    EntityParachute parachute = new EntityParachute(worldObj, ship, pos);
                    if (worldObj.spawnEntityInWorld(parachute)) {
                        player.mountEntity(parachute);
                        player.setSneaking(false);
                    }
                }
                setDead();
            }
        }
    }

    public void killedBy(ShipCapabilities capabilities) {
        if (riddenByEntity != null && ship != null && ship.isFlying()) {
            EntityParachute parachute = new EntityParachute(worldObj, ship, pos);
            if (prevRiddenByEntity != null && worldObj.spawnEntityInWorld(parachute)) {
                prevRiddenByEntity.mountEntity(parachute);
                prevRiddenByEntity.setSneaking(false);
            }
        }
    }

    @Override
    public void onUpdate() {
        if (worldObj == null)
            return;

        super.onUpdate();

        if (worldObj.isRemote) {
            if (!this.dataWatcher.getIsBlank() && this.dataWatcher.getWatchableObjectByte(10) == new Byte((byte) 1)) {
                if (this.dataWatcher.getWatchableObjectInt(6) != 0) {
                    ship = (EntityShip) worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(6));
                    pos = new BlockPos(this.dataWatcher.getWatchableObjectInt(7),
                            this.dataWatcher.getWatchableObjectInt(8),
                            this.dataWatcher.getWatchableObjectInt(9));
                }
            }
            if (this.dataWatcher.hasObjectChanged() && this.dataWatcher.getWatchableObjectInt(6) != 0) {
                ship = (EntityShip) worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(6));
                pos = new BlockPos(this.dataWatcher.getWatchableObjectInt(7),
                        this.dataWatcher.getWatchableObjectInt(8),
                        this.dataWatcher.getWatchableObjectInt(9));
            }
        }

        if (ship != null) {
            setPosition(ship.posX, ship.posY, ship.posZ);
        }

        if (!worldObj.isRemote) {
            if (riddenByEntity == null) {
                if (prevRiddenByEntity != null) {
                    if (ship != null && ship.isFlying()) {
                        EntityParachute parachute = new EntityParachute(worldObj, ship, pos);
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
        this.dataWatcher.addObject(7, 0);
        this.dataWatcher.addObject(8, 0);
        this.dataWatcher.addObject(9, 0);
        this.dataWatcher.addObject(10, new Byte((byte) 1));
    }

    @Override
    public void updateRiderPosition() {
        if (ship != null) {
            ship.updatePassengerPosition(riddenByEntity, new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1), 1);
        }
    }

    @Override
    public double getMountedYOffset() {
        return 0.5D;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return new AxisAlignedBB(0,0,0,0,0,0);
    }

    @Override
    public AxisAlignedBB getEntityBoundingBox() {
        return new AxisAlignedBB(0,0,0,0,0,0);
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
            data.writeInt(0);
            data.writeInt(0);
            data.writeInt(0);
            return;
        }
        data.writeInt(ship.getEntityId());
        data.writeInt(pos.getX());
        data.writeInt(pos.getY());
        data.writeInt(pos.getZ());
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        int entityID = data.readInt();
        int posChunkX = data.readInt();
        int posChunkY = data.readInt();
        int posChunkZ = data.readInt();
        if (entityID != 0) {
            Entity entity = worldObj.getEntityByID(entityID);
            if (entity instanceof EntityShip) {
                setParentShip((EntityShip) entity, new BlockPos(posChunkX, posChunkY, posChunkZ));
            }
        }
    }
}