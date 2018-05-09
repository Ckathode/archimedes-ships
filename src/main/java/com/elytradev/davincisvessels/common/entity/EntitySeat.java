package com.elytradev.davincisvessels.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

public class EntitySeat extends Entity {

    private static final DataParameter<BlockPos> CHUNK_POS = EntityDataManager.createKey(EntitySeat.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Integer> SHIP_ID = EntityDataManager.createKey(EntitySeat.class, DataSerializers.VARINT);

    public EntitySeat(World worldIn) {
        super(worldIn);
    }

    public void setupShip(EntityShip ship, BlockPos chunkPos) {
        setPosition(ship.posX, ship.posY, ship.posZ);
        setShip(ship);
        setChunkPos(chunkPos);
    }

    @Override
    public void onUpdate() {
        EntityShip ship = getShip();
        if (ship != null) {
            ship.updatePassengerPosition(this, getChunkPos(), 0);
        }
        super.onUpdate();
    }


    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            return false;
        } else if (this.isBeingRidden()) {
            return true;
        } else {
            if (!this.world.isRemote) {
                player.startRiding(this);
            }

            return true;
        }
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(CHUNK_POS, BlockPos.ORIGIN);
        this.dataManager.register(SHIP_ID, 0);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    public boolean setChunkPos(BlockPos chunkPos) {
        if (!getChunkPos().equals(chunkPos)) {
            dataManager.set(CHUNK_POS, chunkPos);
            return true;
        }
        return false;
    }

    public BlockPos getChunkPos() {
        return dataManager.get(CHUNK_POS);
    }

    public EntityShip getShip() {
        Entity foundEntity = world.getEntityByID(dataManager.get(SHIP_ID));
        EntityShip ship = null;

        if (foundEntity instanceof EntityShip)
            ship = (EntityShip) foundEntity;

        return ship;
    }

    public int getShipId() {
        return dataManager.get(SHIP_ID);
    }

    // Passenger code below.

    @Override
    public double getMountedYOffset() {
        return -0.3D;
    }

    @Override
    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
    }

    @Override
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    public boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < 1;
    }

    public boolean setShip(EntityShip ship) {
        if (ship != null && !Objects.equals(getShipId(), ship.getEntityId())) {
            dataManager.set(SHIP_ID, ship.getEntityId());
            return true;
        }
        return false;
    }
}