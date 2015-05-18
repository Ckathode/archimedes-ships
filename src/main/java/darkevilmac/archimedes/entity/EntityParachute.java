package darkevilmac.archimedes.entity;

import darkevilmac.movingworld.util.Vec3Mod;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityParachute extends Entity implements IEntityAdditionalSpawnData {
    public EntityParachute(World world) {
        super(world);
        setSize(1F, 1F);
    }

    public EntityParachute(World world, EntityShip ship, BlockPos pos) {
        this(world);
        Vec3Mod vec = new Vec3Mod(pos.getX() - ship.getMovingWorldChunk().getCenterX(), pos.getY() - ship.getMovingWorldChunk().minY(), pos.getZ() - ship.getMovingWorldChunk().getCenterZ());
        vec = new Vec3Mod(vec.rotateYaw((float) Math.toRadians(ship.rotationYaw)));

        setLocationAndAngles(ship.posX + vec.xCoord, ship.posY + vec.yCoord - 2D, ship.posZ + vec.zCoord, 0F, 0F);
        motionX = ship.motionX;
        motionY = ship.motionY;
        motionZ = ship.motionZ;
    }

    public EntityParachute(World world, Entity mounter, Vec3 vec, double shipPosX, double shipPosY, double shipPosZ, double motionX, double motionY, double motionZ) {
        this(world);

        setLocationAndAngles(shipPosX + vec.xCoord, shipPosY + vec.yCoord - 2D, shipPosZ + vec.zCoord, 0F, 0F);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        mounter.mountEntity(null);
        mounter.mountEntity(this);
    }


    @Override
    protected void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (!worldObj.isRemote && (riddenByEntity == null || onGround || isInWater())) {
            setDead();
            return;
        }

        motionX *= 0.98D;
        motionY *= 0.8D;
        motionZ *= 0.98D;

        if (!worldObj.isRemote && riddenByEntity != null) {
            motionX += riddenByEntity.motionX;
            motionZ += riddenByEntity.motionZ;
        }
        motionY -= 0.05D;

        moveEntity(motionX, motionY, motionZ);
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
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
    public void func_180433_a(double y, boolean grounded, Block blockIn, BlockPos pos) {
        // Do nothing
    }


    @Override
    public void fall(float fallDistance, float damageMult) {
        // Do nothing
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeBoolean(riddenByEntity != null);
        if (riddenByEntity != null) {
            buffer.writeInt(riddenByEntity.getEntityId());
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        if (additionalData.readBoolean() && worldObj != null) {
            int entityID = additionalData.readInt();
            if (worldObj.getEntityByID(entityID) != null) {
                worldObj.getEntityByID(entityID).mountEntity(this);
            }
        }
    }
}
