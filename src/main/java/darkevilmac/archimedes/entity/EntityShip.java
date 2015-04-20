package darkevilmac.archimedes.entity;

import darkevilmac.archimedes.control.ShipControllerCommon;
import darkevilmac.movingworld.chunk.MovingWorldAssemblyInteractor;
import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.entity.MovingWorldCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.util.Set;

public class EntityShip extends EntityMovingWorld {

    public static final float BASE_FORWARD_SPEED = 0.005F, BASE_TURN_SPEED = 0.5F, BASE_LIFT_SPEED = 0.004F;
    public ShipCapabilities capabilities;
    private ShipControllerCommon controller;
    private ShipAssemblyInteractor shipAssemblyInteractor;

    public void setShipAssemblyInteractor(ShipAssemblyInteractor shipAssemblyInteractor) {
        this.shipAssemblyInteractor = shipAssemblyInteractor;
    }

    public EntityShip(World world) {
        super(world);
        capabilities = new ShipCapabilities(this, true);
    }

    @Override
    public MovingWorldCapabilities getCapabilities() {
        return capabilities;
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

    public void fillAirBlocks(Set<ChunkPosition> set, int x, int y, int z) {
        super.fillAirBlocks(set, x, y, z);
    }

    public ShipControllerCommon getController() {
        return controller;
    }
}