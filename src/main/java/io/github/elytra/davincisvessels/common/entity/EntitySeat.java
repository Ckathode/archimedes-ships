package io.github.elytra.davincisvessels.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.util.List;

public class EntitySeat extends Entity implements IEntityAdditionalSpawnData {

    public static final DataParameter<Integer> SHIPID = EntityDataManager.<Integer>createKey(EntitySeat.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> POSX = EntityDataManager.<Integer>createKey(EntitySeat.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> POSY = EntityDataManager.<Integer>createKey(EntitySeat.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> POSZ = EntityDataManager.<Integer>createKey(EntitySeat.class, DataSerializers.VARINT);

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
     */
    @Override
    public boolean processInitialInteract(EntityPlayer entityplayer, ItemStack stack, EnumHand hand) {
        checkShipOpinion();

        if (getControllingPassenger() == null) {
            entityplayer.startRiding(this, true);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the parent ship as well as position.
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
                if (!this.dataManager.isEmpty()) {
                    this.dataManager.set(SHIPID, entityship.getEntityId());
                    this.dataManager.set(POSX, x);
                    this.dataManager.set(POSY, y);
                    this.dataManager.set(POSZ, z);
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
            if (getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) getControllingPassenger();
                EntitySeat seat = ((ShipCapabilities) ship.getCapabilities()).getAvailableSeat();
                if (seat != null) {
                    player.dismountRidingEntity();
                    player.startRiding(seat);
                    EntityParachute parachute = new EntityParachute(worldObj, ship, pos);
                    if (worldObj.spawnEntityInWorld(parachute)) {
                        player.startRiding(parachute);
                        player.setSneaking(false);
                    }
                }
                setDead();
            }
        }
    }

    public void killedBy() {
        if (getControllingPassenger() != null && ship != null && ship.isFlying()) {
            EntityParachute parachute = new EntityParachute(worldObj, ship, pos);
            if (prevRiddenByEntity != null && worldObj.spawnEntityInWorld(parachute)) {
                prevRiddenByEntity.startRiding(parachute);
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
            if (!this.dataManager.isEmpty() && this.dataManager.isDirty()) {
                if (this.dataManager.get(SHIPID) != -1) {
                    ship = (EntityShip) worldObj.getEntityByID(this.dataManager.get(SHIPID));
                    pos = new BlockPos(this.dataManager.get(POSX),
                            this.dataManager.get(POSY),
                            this.dataManager.get(POSZ));
                }
            }
            if (this.dataManager.isDirty() && this.dataManager.get(SHIPID) != -1) {
                ship = (EntityShip) worldObj.getEntityByID(this.dataManager.get(SHIPID));
                pos = new BlockPos(this.dataManager.get(POSX),
                        this.dataManager.get(POSY),
                        this.dataManager.get(POSZ));
            }
        }

        if (ship != null) {
            setPosition(ship.posX, ship.posY, ship.posZ);
        }

        if (!worldObj.isRemote) {
            if (getControllingPassenger() == null) {
                if (prevRiddenByEntity != null) {
                    if (ship != null && ship.isFlying()) {
                        EntityParachute parachute = new EntityParachute(worldObj, ship, pos);
                        if (worldObj.spawnEntityInWorld(parachute)) {
                            prevRiddenByEntity.startRiding(parachute);
                            prevRiddenByEntity.setSneaking(false);
                        }
                    }
                    prevRiddenByEntity = null;
                }
            } else {
                prevRiddenByEntity = getControllingPassenger();
            }
            ticksTillShipCheck++;
            if (ticksTillShipCheck >= 40) {
                if (ship != null)
                    ticksTillShipCheck = 0;
            }
        }

        if (getControllingPassenger() != null && getControllingPassenger().getRidingEntity() != this) {
            Entity rider = getControllingPassenger();
            rider.startRiding(this);
        }
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(SHIPID, -1);
        this.dataManager.register(POSX, 0);
        this.dataManager.register(POSY, 0);
        this.dataManager.register(POSZ, 0);
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (ship != null) {
            ship.updatePassengerPosition(passenger, new BlockPos(pos.getX(), pos.getY(), pos.getZ()), 0);
        }
    }

    @Override
    public void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (ship != null) {
            ship.updatePassengerPosition(passenger, new BlockPos(pos.getX(), pos.getY() + ship.getMobileChunk().maxY() / 2, pos.getZ()), 0);
        }
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : (Entity) list.get(0);
    }

    @Override
    public double getMountedYOffset() {
        return 0.5D;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    @Override
    public AxisAlignedBB getEntityBoundingBox() {
        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        checkShipOpinion();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
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