package ckathode.archimedes.entity;

import ckathode.archimedes.control.ShipControllerCommon;
import darkevilmac.movingworld.chunk.MovingWorldAssemblyInteractor;
import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.entity.MovingWorldCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityShip extends EntityMovingWorld {

    public static final float BASE_FORWARD_SPEED = 0.005F, BASE_TURN_SPEED = 0.5F, BASE_LIFT_SPEED = 0.004F;

    private ShipControllerCommon controller;


    public EntityShip(World world, MovingWorldCapabilities capabilities) {
        super(world, capabilities);
    }

    @Override
    public boolean isBraking() {
        return controller.getShipControl() == 3;
    }

    @Override
    public void writeMovingWorldNBT(NBTTagCompound compound) {

    }

    @Override
    public void readMovingWorldNBT(NBTTagCompound compound) {

    }

    @Override
    public void writeMovingWorldSpawnData(ByteBuf data) {

    }

    @Override
    public void readMovingWorldSpawnData(ByteBuf data) {

    }

    @Override
    public float getXRenderScale() {
        return 1;
    }

    @Override
    public float getYRenderScale() {
        return 1;
    }

    @Override
    public float getZRenderScale() {
        return 1;
    }

    @Override
    public MovingWorldAssemblyInteractor getAssemblyInteractor() {
        return null;
    }
}