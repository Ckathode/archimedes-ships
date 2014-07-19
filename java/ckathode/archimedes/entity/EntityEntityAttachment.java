package ckathode.archimedes.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityEntityAttachment extends Entity implements IEntityAdditionalSpawnData
{
	private EntityShip		ship;
	private ChunkPosition	pos;
	private Entity			prevRiddenByEntity;
	
	public EntityEntityAttachment(World world)
	{
		super(world);
		ship = null;
		pos = null;
		prevRiddenByEntity = null;
		yOffset = 0f;
		setSize(0F, 0F);
	}
	
	public void setParentShip(EntityShip entityship, int x, int y, int z)
	{
		ship = entityship;
		if (entityship != null)
		{
			pos = new ChunkPosition(x, y, z);
			setLocationAndAngles(entityship.posX, entityship.posY, entityship.posZ, 0F, 0F);
		}
	}
	
	public EntityShip getParentShip()
	{
		return ship;
	}
	
	public ChunkPosition getChunkPosition()
	{
		return pos;
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (ship != null)
		{
			setPosition(ship.posX, ship.posY, ship.posZ);
		}
		
		if (!worldObj.isRemote)
		{
			if (riddenByEntity == null)
			{
				if (prevRiddenByEntity != null)
				{
					if (ship != null && ship.isFlying())
					{
						EntityParachute parachute = new EntityParachute(worldObj, ship, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
						if (worldObj.spawnEntityInWorld(parachute))
						{
							prevRiddenByEntity.mountEntity(parachute);
							prevRiddenByEntity.setSneaking(false);
						}
					}
					prevRiddenByEntity = null;
				}
			}
			
			if (riddenByEntity != null)
			{
				prevRiddenByEntity = riddenByEntity;
			}
		}
	}
	
	@Override
	protected void entityInit()
	{
	}
	
	@Override
	public void updateRiderPosition()
	{
		if (ship != null)
		{
			ship.updateRiderPosition(riddenByEntity, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, 0);
		}
	}
	
	@Override
	public double getMountedYOffset()
	{
		return yOffset + 0.5d;
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
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		setDead();
	}
	
	@Override
	public void writeSpawnData(ByteBuf data)
	{
		if (ship == null)
		{
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
	public void readSpawnData(ByteBuf data)
	{
		Entity entity = worldObj.getEntityByID(data.readInt());
		if (entity instanceof EntityShip)
		{
			setParentShip((EntityShip) entity, data.readUnsignedByte(), data.readUnsignedByte(), data.readUnsignedByte());
		}
	}
}
