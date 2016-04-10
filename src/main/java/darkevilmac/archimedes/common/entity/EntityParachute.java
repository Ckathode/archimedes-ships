package darkevilmac.archimedes.common.entity;

import darkevilmac.movingworld.common.util.Vec3dMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityParachute extends Entity implements IEntityAdditionalSpawnData {

    public EntityParachute(World world) {
        super(world);
    }

    public EntityParachute(World world, EntityShip ship, BlockPos pos) {
        this(world);
        Vec3dMod vec = new Vec3dMod(pos.getX() - ship.getMobileChunk().getCenterX(), pos.getY() - ship.getMobileChunk().minY(), pos.getZ() - ship.getMobileChunk().getCenterZ());
        vec = vec.rotateAroundY((float) Math.toRadians(ship.rotationYaw));

        setLocationAndAngles(ship.posX + vec.xCoord, ship.posY + vec.yCoord - 2D, ship.posZ + vec.zCoord, 0F, 0F);
        motionX = ship.motionX;
        motionY = ship.motionY;
        motionZ = ship.motionZ;
    }

    public EntityParachute(World world, Entity mounter, Vec3dMod vec, Vec3dMod shipPos, Vec3dMod motion) {
        this(world);

        setLocationAndAngles(shipPos.xCoord + vec.xCoord, shipPos.yCoord + vec.yCoord - 2D, shipPos.zCoord + vec.zCoord, 0F, 0F);
        this.motionX = motion.xCoord;
        this.motionY = motion.yCoord;
        this.motionZ = motion.zCoord;

        mounter.startRiding(null);
        mounter.startRiding(this, true);
    }


    @Override
    protected void entityInit() {
        setSize(1F, 1F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (!worldObj.isRemote && (getControllingPassenger() == null || onGround || isInWater())) {
            setDead();
            return;
        }


        if (!worldObj.isRemote && getControllingPassenger() != null) {
            motionX += getControllingPassenger().motionX;
            motionZ += getControllingPassenger().motionZ;
        }
        if (motionY > -.5)
            motionY -= 0.025D;

        moveEntity(motionX, motionY, motionZ);
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
    }


    @Override
    public void fall(float fallDistance, float damageMult) {
        // We don't fall.
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeBoolean(getControllingPassenger() != null);
        if (getControllingPassenger() != null) {
            buffer.writeInt(getControllingPassenger().getEntityId());
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        if (additionalData.readBoolean() && worldObj != null) {
            int entityID = additionalData.readInt();
            if (worldObj.getEntityByID(entityID) != null) {
                worldObj.getEntityByID(entityID).startRiding(this);
            }
        }
    }
}
