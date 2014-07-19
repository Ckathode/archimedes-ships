package ckathode.archimedes.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityParachute extends Entity
{
	public EntityParachute(World world)
	{
		super(world);
		setSize(1F, 1F);
	}
	
	public EntityParachute(World world, EntityShip ship, int x, int y, int z)
	{
		this(world);
		Vec3 vec = Vec3.createVectorHelper(x - ship.getShipChunk().getCenterX(), y - ship.getShipChunk().minY(), z - ship.getShipChunk().getCenterZ());
		vec.rotateAroundY((float) Math.toRadians(ship.rotationYaw));
		
		setLocationAndAngles(ship.posX + vec.xCoord, ship.posY + vec.yCoord - 2D, ship.posZ + vec.zCoord, 0F, 0F);
		motionX = ship.motionX;
		motionY = ship.motionY;
		motionZ = ship.motionZ;
	}
	
	@Override
	protected void entityInit()
	{
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		if (!worldObj.isRemote && (riddenByEntity == null || onGround || isInWater()))
		{
			setDead();
			return;
		}
		
		motionX *= 0.98D;
		motionY *= 0.8D;
		motionZ *= 0.98D;
		
		if (!worldObj.isRemote && riddenByEntity != null)
		{
			motionX += riddenByEntity.motionX;
			motionZ += riddenByEntity.motionZ;
		}
		motionY -= 0.05D;
		
		moveEntity(motionX, motionY, motionZ);
	}
	
	@Override
	public boolean shouldRiderSit()
	{
		return false;
	}
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		return null;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox()
	{
		return null;
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}
	
	@Override
	protected void updateFallState(double d, boolean ground)
	{
	}
	
	@Override
	protected void fall(float f)
	{
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
	}
	
}
