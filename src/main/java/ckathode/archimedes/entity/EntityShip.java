package ckathode.archimedes.entity;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import ckathode.archimedes.ArchimedesConfig;
import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.MaterialDensity;
import ckathode.archimedes.blockitem.TileEntityEngine;
import ckathode.archimedes.blockitem.TileEntityHelm;
import ckathode.archimedes.chunk.AssembleResult;
import ckathode.archimedes.chunk.ChunkDisassembler;
import ckathode.archimedes.chunk.ChunkIO;
import ckathode.archimedes.chunk.MobileChunk;
import ckathode.archimedes.chunk.MobileChunkClient;
import ckathode.archimedes.chunk.MobileChunkServer;
import ckathode.archimedes.chunk.ShipSizeOverflowException;
import ckathode.archimedes.control.ShipControllerClient;
import ckathode.archimedes.control.ShipControllerCommon;
import ckathode.archimedes.util.AABBRotator;
import ckathode.archimedes.util.MathHelperMod;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityShip extends EntityBoat implements IEntityAdditionalSpawnData
{
	public static final float	BASE_FORWARD_SPEED	= 0.005F, BASE_TURN_SPEED = 0.5F, BASE_LIFT_SPEED = 0.004F;
	
	public static boolean isAABBInLiquidNotFall(World world, AxisAlignedBB aabb)
	{
		int i = MathHelper.floor_double(aabb.minX);
		int j = MathHelper.floor_double(aabb.maxX + 1D);
		int k = MathHelper.floor_double(aabb.minY);
		int l = MathHelper.floor_double(aabb.maxY + 1D);
		int i1 = MathHelper.floor_double(aabb.minZ);
		int j1 = MathHelper.floor_double(aabb.maxZ + 1D);
		
		for (int x = i; x < j; ++x)
		{
			for (int y = k; y < l; ++y)
			{
				for (int z = i1; z < j1; ++z)
				{
					Block block = world.getBlock(x, y, z);
					
					if (block != null && (block.getMaterial() == Material.water || block.getMaterial() == Material.lava))
					{
						int j2 = world.getBlockMetadata(x, y, z);
						double d0 = y + 1;
						
						if (j2 < 8)
						{
							d0 = y + 1 - j2 / 8.0D;
							
							if (d0 >= aabb.minY)
							{
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	private MobileChunk				shipChunk;
	private ShipCapabilities		capabilities;
	private ShipControllerCommon	controller;
	private ShipHandlerCommon		handler;
	private ShipInfo				info;
	
	private ChunkDisassembler		disassembler;
	
	public float					motionYaw;
	
	public int						frontDirection;
	public int						seatX, seatY, seatZ;
	private Entity					prevRiddenByEntity;
	
	boolean							isFlying;
	protected float					groundFriction, horFriction, vertFriction;
	
	int[]							layeredBlockVolumeCount;
	
	//START ENTITYBOAT MOD VARS	
	private boolean					boatIsEmpty;
	private boolean					syncPosWithServer;
	@SideOnly(Side.CLIENT)
	private int						boatPosRotationIncrements;
	@SideOnly(Side.CLIENT)
	private double					boatX, boatY, boatZ;
	@SideOnly(Side.CLIENT)
	private double					boatPitch, boatYaw;
	@SideOnly(Side.CLIENT)
	private double					boatVelX, boalVelY, boatVelZ;
	
	//END ENTITYBOAT MOD VARS
	
	public EntityShip(World world)
	{
		super(world);
		info = new ShipInfo();
		capabilities = new ShipCapabilities(this);
		if (world.isRemote)
		{
			initClient();
		} else
		{
			initCommon();
		}
		
		motionYaw = 0F;
		
		layeredBlockVolumeCount = null;
		frontDirection = 0;
		yOffset = 0F;
		
		groundFriction = 0.9F;
		horFriction = 0.994F;
		vertFriction = 0.95F;
		
		prevRiddenByEntity = null;
		
		isFlying = false;
		boatIsEmpty = false;
		syncPosWithServer = true;
		if (world.isRemote)
		{
			boatPosRotationIncrements = 0;
			boatX = boatY = boatZ = 0D;
			boatPitch = boatYaw = 0D;
			boatVelX = boalVelY = boatVelZ = 0D;
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void initClient()
	{
		shipChunk = new MobileChunkClient(worldObj, this);
		handler = new ShipHandlerClient(this);
		controller = new ShipControllerClient();
	}
	
	private void initCommon()
	{
		shipChunk = new MobileChunkServer(worldObj, this);
		handler = new ShipHandlerServer(this);
		controller = new ShipControllerCommon();
	}
	
	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(30, Byte.valueOf((byte) 0));
	}
	
	public MobileChunk getShipChunk()
	{
		return shipChunk;
	}
	
	public ShipCapabilities getCapabilities()
	{
		return capabilities;
	}
	
	public ShipControllerCommon getController()
	{
		return controller;
	}
	
	public ChunkDisassembler getDisassembler()
	{
		if (disassembler == null)
		{
			disassembler = new ChunkDisassembler(this);
		}
		return disassembler;
	}
	
	public void setInfo(ShipInfo shipinfo)
	{
		if (shipinfo == null) throw new NullPointerException("Cannot set null ship info");
		info = shipinfo;
	}
	
	public ShipInfo getInfo()
	{
		return info;
	}
	
	public void setPilotSeat(int dir, int seatx, int seaty, int seatz)
	{
		frontDirection = dir;
		seatX = seatx;
		seatY = seaty;
		seatZ = seatz;
	}
	
	@Override
	public void setDead()
	{
		super.setDead();
		shipChunk.onChunkUnload();
		capabilities.clear();
	}
	
	@Override
	public void onEntityUpdate()
	{
		super.onEntityUpdate();
		if (shipChunk.isModified)
		{
			shipChunk.isModified = false;
			handler.onChunkUpdate();
		}
	}
	
	public void setRotatedBoundingBox()
	{
		if (shipChunk == null)
		{
			float hw = width / 2F;
			boundingBox.setBounds(posX - hw, posY, posZ - hw, posX + hw, posY + height, posZ + hw);
		} else
		{
			boundingBox.setBounds(posX - shipChunk.getCenterX(), posY, posZ - shipChunk.getCenterZ(), posX + shipChunk.getCenterX(), posY + height, posZ + shipChunk.getCenterZ());
			AABBRotator.rotateAABBAroundY(boundingBox, posX, posZ, (float) Math.toRadians(rotationYaw));
		}
	}
	
	@Override
	public void setSize(float w, float h)
	{
		if (w != width || h != height)
		{
			width = w;
			height = h;
			float hw = w / 2F;
			boundingBox.setBounds(posX - hw, posY, posZ - hw, posX + hw, posY + height, posZ + hw);
		}
		
		float f = w % 2.0F;
		if (f < 0.375D)
		{
			myEntitySize = EnumEntitySize.SIZE_1;
		} else if (f < 0.75D)
		{
			myEntitySize = EnumEntitySize.SIZE_2;
		} else if (f < 1.0D)
		{
			myEntitySize = EnumEntitySize.SIZE_3;
		} else if (f < 1.375D)
		{
			myEntitySize = EnumEntitySize.SIZE_4;
		} else if (f < 1.75D)
		{
			myEntitySize = EnumEntitySize.SIZE_5;
		} else
		{
			myEntitySize = EnumEntitySize.SIZE_6;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int incr)
	{
		if (boatIsEmpty)
		{
			boatPosRotationIncrements = incr + 5;
		} else
		{
			double dx = x - posX;
			double dy = y - posY;
			double dz = z - posZ;
			double d = dx * dx + dy * dy + dz * dz;
			
			if (d < 0.3D)
			{
				return;
			}
			
			syncPosWithServer = true;
			boatPosRotationIncrements = incr;
		}
		
		boatX = x;
		boatY = y;
		boatZ = z;
		boatYaw = yaw;
		boatPitch = pitch;
		motionX = boatVelX;
		motionY = boalVelY;
		motionZ = boatVelZ;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z)
	{
		boatVelX = motionX = x;
		boalVelY = motionY = y;
		boatVelZ = motionZ = z;
	}
	
	@Override
	public void onUpdate()
	{
		onEntityUpdate();
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		double horvel = Math.sqrt(motionX * motionX + motionZ * motionZ);
		if (worldObj.isRemote)
		{
			if (riddenByEntity == null)
			{
				setIsBoatEmpty(true);
			}
			spawnParticles(horvel);
		}
		
		if (worldObj.isRemote && (boatIsEmpty || syncPosWithServer))
		{
			handleClientUpdate();
			if (boatPosRotationIncrements == 0)
			{
				syncPosWithServer = false;
			}
		} else
		{
			handleServerUpdate(horvel);
		}
	}
	
	@SideOnly(Side.CLIENT)
	protected void handleClientUpdate()
	{
		if (boatPosRotationIncrements > 0)
		{
			double dx = posX + (boatX - posX) / boatPosRotationIncrements;
			double dy = posY + (boatY - posY) / boatPosRotationIncrements;
			double dz = posZ + (boatZ - posZ) / boatPosRotationIncrements;
			double ang = MathHelper.wrapAngleTo180_double(boatYaw - rotationYaw);
			rotationYaw = (float) (rotationYaw + ang / boatPosRotationIncrements);
			rotationPitch = (float) (rotationPitch + (boatPitch - rotationPitch) / boatPosRotationIncrements);
			boatPosRotationIncrements--;
			setPosition(dx, dy, dz);
			setRotation(rotationYaw, rotationPitch);
		} else
		{
			setPosition(posX + motionX, posY + motionY, posZ + motionZ);
			
			if (onGround)
			{
				motionX *= groundFriction;
				motionY *= groundFriction;
				motionZ *= groundFriction;
			}
			
			motionX *= horFriction;
			motionY *= vertFriction;
			motionZ *= horFriction;
		}
		setRotatedBoundingBox();
	}
	
	protected void handleServerUpdate(double horvel)
	{
		//START outer forces
		byte b0 = 5;
		int bpermeter = (int) (b0 * (boundingBox.maxY - boundingBox.minY));
		float watervolume = 0F;
		AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(0D, 0D, 0D, 0D, 0D, 0D);
		int belowwater = 0;
		for (; belowwater < bpermeter; belowwater++)
		{
			double d1 = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) * belowwater / bpermeter;
			double d2 = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) * (belowwater + 1) / bpermeter;
			axisalignedbb.setBounds(boundingBox.minX, d1, boundingBox.minZ, boundingBox.maxX, d2, boundingBox.maxZ);
			
			if (!isAABBInLiquidNotFall(worldObj, axisalignedbb))
			{
				break;
			}
		}
		if (belowwater > 0 && layeredBlockVolumeCount != null)
		{
			int k = belowwater / b0;
			for (int y = 0; y <= k && y < layeredBlockVolumeCount.length; y++)
			{
				if (y == k)
				{
					watervolume += layeredBlockVolumeCount[y] * (belowwater % b0) * MaterialDensity.WATER_DENSITY / b0;
				} else
				{
					watervolume += layeredBlockVolumeCount[y] * MaterialDensity.WATER_DENSITY;
				}
			}
		}
		
		if (onGround)
		{
			isFlying = false;
		}
		
		float gravity = 0.05F;
		if (watervolume > 0F)
		{
			isFlying = false;
			float buoyancyforce = MaterialDensity.WATER_DENSITY * watervolume * gravity; //F = rho * V * g (Archimedes' law)
			float mass = capabilities.getMass();
			motionY += buoyancyforce / mass;
		}
		if (!isFlying())
		{
			motionY -= gravity;
		}
		capabilities.updateEngines();
		//END outer forces
		
		//START player input
		if (riddenByEntity == null)
		{
			if (prevRiddenByEntity != null)
			{
				if (ArchimedesShipMod.instance.modConfig.disassembleOnDismount)
				{
					alignToGrid();
					updateRiderPosition(prevRiddenByEntity, seatX, seatY, seatZ, 1);
					disassemble(false);
				} else
				{
					if (!worldObj.isRemote && isFlying())
					{
						EntityParachute parachute = new EntityParachute(worldObj, this, seatX, seatY, seatZ);
						if (worldObj.spawnEntityInWorld(parachute))
						{
							prevRiddenByEntity.mountEntity(parachute);
							prevRiddenByEntity.setSneaking(false);
						}
					}
				}
				prevRiddenByEntity = null;
			}
		}
		
		if (riddenByEntity == null)
		{
			if (isFlying())
			{
				motionY -= BASE_LIFT_SPEED * 0.2F;
			}
		} else
		{
			handlePlayerControl();
			prevRiddenByEntity = riddenByEntity;
		}
		//END player input
		
		//START limit motion
		double newhorvel = Math.sqrt(motionX * motionX + motionZ * motionZ);
		double maxvel = ArchimedesShipMod.instance.modConfig.speedLimit;
		if (newhorvel > maxvel)
		{
			double d = maxvel / newhorvel;
			motionX *= d;
			motionZ *= d;
			newhorvel = maxvel;
		}
		motionY = MathHelperMod.clamp_double(motionY, -maxvel, maxvel);
		//END limit motion
		
		if (onGround)
		{
			motionX *= groundFriction;
			motionY *= groundFriction;
			motionZ *= groundFriction;
		}
		rotationPitch = rotationPitch + (motionYaw * ArchimedesShipMod.instance.modConfig.bankingMultiplier - rotationPitch) * 0.15f;
		motionYaw *= 0.7F;
		//motionYaw = MathHelper.clamp_float(motionYaw, -BASE_TURN_SPEED * ShipMod.instance.modConfig.turnSpeed, BASE_TURN_SPEED * ShipMod.instance.modConfig.turnSpeed);
		rotationYaw += motionYaw;
		setRotatedBoundingBox();
		moveEntity(motionX, motionY, motionZ);
		posY = Math.min(posY, worldObj.getHeight());
		motionX *= horFriction;
		motionY *= vertFriction;
		motionZ *= horFriction;
		
		if (ArchimedesShipMod.instance.modConfig.shipControlType == ArchimedesConfig.CONTROL_TYPE_VANILLA)
		{
			double newyaw = rotationYaw;
			double dx = prevPosX - posX;
			double dz = prevPosZ - posZ;
			
			if (riddenByEntity != null && !isBraking() && dx * dx + dz * dz > 0.01D)
			{
				newyaw = 270F - Math.toDegrees(Math.atan2(dz, dx)) + frontDirection * 90F;
			}
			
			double deltayaw = MathHelper.wrapAngleTo180_double(newyaw - rotationYaw);
			double maxyawspeed = 2D;
			if (deltayaw > maxyawspeed)
			{
				deltayaw = maxyawspeed;
			}
			if (deltayaw < -maxyawspeed)
			{
				deltayaw = -maxyawspeed;
			}
			
			rotationYaw = (float) (rotationYaw + deltayaw);
		}
		setRotation(rotationYaw, rotationPitch);
		
		//START Collision
		if (!worldObj.isRemote)
		{
			@SuppressWarnings("unchecked")
			List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.2D, 0.0D, 0.2D));
			if (list != null && !list.isEmpty())
			{
				for (Entity entity : list)
				{
					if (entity != riddenByEntity && entity.canBePushed())
					{
						if (entity instanceof EntityShip)
						{
							entity.applyEntityCollision(this);
						} else if (entity instanceof EntityBoat)
						{
							double d0 = this.posX - entity.posX;
							double d1 = this.posZ - entity.posZ;
							double d2 = MathHelper.abs_max(d0, d1);
							
							if (d2 >= 0.01D)
							{
								d2 = MathHelper.sqrt_double(d2);
								d0 /= d2;
								d1 /= d2;
								double d3 = 1.0D / d2;
								
								if (d3 > 1.0D)
								{
									d3 = 1.0D;
								}
								
								d0 *= d3;
								d1 *= d3;
								d0 *= 0.05D;
								d1 *= 0.05D;
								d0 *= 1.0F - entity.entityCollisionReduction;
								d1 *= 1.0F - entity.entityCollisionReduction;
								entity.addVelocity(-d0, 0.0D, -d1);
							}
						}
					}
				}
			}
			
			for (int l = 0; l < 4; ++l)
			{
				int i1 = MathHelper.floor_double(posX + ((l % 2) - 0.5D) * 0.8D);
				int j1 = MathHelper.floor_double(posZ + ((l / 2) - 0.5D) * 0.8D);
				
				for (int k1 = 0; k1 < 2; ++k1)
				{
					int l1 = MathHelper.floor_double(posY) + k1;
					Block block = worldObj.getBlock(i1, l1, j1);
					
					if (block == Blocks.snow)
					{
						worldObj.setBlockToAir(i1, l1, j1);
						isCollidedHorizontally = false;
					} else if (block == Blocks.waterlily)
					{
						worldObj.func_147480_a(i1, l1, j1, true);
						isCollidedHorizontally = false;
					}
				}
			}
		}
		//END Collision
	}
	
	private void handlePlayerControl()
	{
		if (riddenByEntity instanceof EntityLivingBase)
		{
			double throttle = ((EntityLivingBase) riddenByEntity).moveForward;
			if (isFlying())
			{
				throttle *= 0.5D;
			}
			
			if (ArchimedesShipMod.instance.modConfig.shipControlType == ArchimedesConfig.CONTROL_TYPE_ARCHIMEDES)
			{
				Vec3 vec = Vec3.createVectorHelper(riddenByEntity.motionX, 0D, riddenByEntity.motionZ);
				vec.rotateAroundY((float) Math.toRadians(riddenByEntity.rotationYaw));
				
				double steer = ((EntityLivingBase) riddenByEntity).moveStrafing;
				
				motionYaw += steer * BASE_TURN_SPEED * capabilities.getPoweredRotationMult() * ArchimedesShipMod.instance.modConfig.turnSpeed;
				
				float yaw = (float) Math.toRadians(180F - rotationYaw + frontDirection * 90F);
				vec.xCoord = motionX;
				vec.zCoord = motionZ;
				vec.rotateAroundY(yaw);
				vec.xCoord *= 0.9D;
				vec.zCoord -= throttle * BASE_FORWARD_SPEED * capabilities.getPoweredSpeedMult();
				vec.rotateAroundY(-yaw);
				
				motionX = vec.xCoord;
				motionZ = vec.zCoord;
				
			} else if (ArchimedesShipMod.instance.modConfig.shipControlType == ArchimedesConfig.CONTROL_TYPE_VANILLA)
			{
				if (throttle > 0.0D)
				{
					double dsin = -Math.sin(Math.toRadians(riddenByEntity.rotationYaw));
					double dcos = Math.cos(Math.toRadians(riddenByEntity.rotationYaw));
					motionX += dsin * BASE_FORWARD_SPEED * capabilities.speedMultiplier;
					motionZ += dcos * BASE_FORWARD_SPEED * capabilities.speedMultiplier;
				}
			}
		}
		
		if (controller.getShipControl() != 0)
		{
			if (controller.getShipControl() == 4)
			{
				alignToGrid();
			} else if (isBraking())
			{
				motionX *= capabilities.brakeMult;
				motionZ *= capabilities.brakeMult;
				if (isFlying())
				{
					motionY *= capabilities.brakeMult;
				}
			} else if (controller.getShipControl() < 3 && capabilities.canFly())
			{
				int i;
				if (controller.getShipControl() == 2)
				{
					isFlying = true;
					i = 1;
				} else
				{
					i = -1;
				}
				motionY += i * BASE_LIFT_SPEED * capabilities.getPoweredLiftMult();
			}
		}
	}
	
	@Override
	public boolean handleWaterMovement()
	{
		float f = width;
		width = 0F;
		boolean ret = super.handleWaterMovement();
		width = f;
		return ret;
	}
	
	public boolean isFlying()
	{
		return capabilities.canFly() && (isFlying || controller.getShipControl() == 2);
	}
	
	public boolean isBraking()
	{
		return controller.getShipControl() == 3;
	}
	
	/**
	 * Determines whether the entity should be pushed by fluids
	 */
	@Override
	public boolean isPushedByWater()
	{
		return ticksExisted > 60;
	}
	
	@SideOnly(Side.CLIENT)
	protected void spawnParticles(double horvel)
	{
		/*if (isInWater() && horvel > 0.1625D)
		{
			/*double yaw = Math.toRadians(rotationYaw);
			double cosyaw = Math.cos(yaw);
			double sinyaw = Math.sin(yaw);*//*
											
											for (int j = 0; j < 1D + horvel * 60D; j++)
											{
											worldObj.spawnParticle("splash", posX + (rand.nextFloat() - 0.5F) * width, posY, posZ + (rand.nextFloat() - 0.5F) * width, motionX, motionY + 1F, motionZ);
											}
											for (int j = 0; j < 1D + horvel * 20D; j++)
											{
											worldObj.spawnParticle("bubble", posX + rand.nextFloat() - 0.5F, posY - 0.2D, posZ + rand.nextFloat() - 0.5F, 0D, 0D, 0D);
											}
											}*/
		if (capabilities.getEngines() != null)
		{
			Vec3 vec = Vec3.createVectorHelper(0d, 0d, 0d);
			float yaw = (float) Math.toRadians(rotationYaw);
			for (TileEntityEngine engine : capabilities.getEngines())
			{
				if (engine.isRunning())
				{
					vec.xCoord = engine.xCoord - shipChunk.getCenterX() + 0.5f;
					vec.yCoord = engine.yCoord;
					vec.zCoord = engine.zCoord - shipChunk.getCenterZ() + 0.5f;
					vec.rotateAroundY(yaw);
					worldObj.spawnParticle("smoke", posX + vec.xCoord, posY + vec.yCoord + 1d, posZ + vec.zCoord, 0d, 0d, 0d);
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setIsBoatEmpty(boolean flag)
	{
		boatIsEmpty = flag;
	}
	
	@Override
	public boolean shouldRiderSit()
	{
		return true;
	}
	
	@Override
	public void updateRiderPosition()
	{
		updateRiderPosition(riddenByEntity, seatX, seatY, seatZ, 1);
	}
	
	public void updateRiderPosition(Entity entity, int seatx, int seaty, int seatz, int flags)
	{
		if (entity != null)
		{
			float yaw = (float) Math.toRadians(rotationYaw);
			float pitch = (float) Math.toRadians(rotationPitch);
			
			int x1 = seatx, y1 = seaty, z1 = seatz;
			if ((flags & 1) == 1)
			{
				if (frontDirection == 0)
				{
					z1 -= 1;
				} else if (frontDirection == 1)
				{
					x1 += 1;
				} else if (frontDirection == 2)
				{
					z1 += 1;
				} else if (frontDirection == 3)
				{
					x1 -= 1;
				}
				
				Block block = shipChunk.getBlock(x1, MathHelper.floor_double(y1 + getMountedYOffset() + entity.getYOffset()), z1);
				if (block.isOpaqueCube())
				{
					x1 = seatx;
					y1 = seaty;
					z1 = seatz;
				}
			}
			
			double yoff = (flags & 2) == 2 ? 0d : getMountedYOffset();
			Vec3 vec = Vec3.createVectorHelper(x1 - shipChunk.getCenterX() + 0.5d, y1 - shipChunk.minY() + yoff, z1 - shipChunk.getCenterZ() + 0.5d);
			switch (frontDirection)
			{
			case 0:
				vec.rotateAroundZ(-pitch);
				break;
			case 1:
				vec.rotateAroundX(pitch);
				break;
			case 2:
				vec.rotateAroundZ(pitch);
				break;
			case 3:
				vec.rotateAroundX(-pitch);
				break;
			}
			vec.rotateAroundY(yaw);
			
			entity.setPosition(posX + vec.xCoord, posY + vec.yCoord + entity.getYOffset(), posZ + vec.zCoord);
		}
	}
	
	@Override
	public double getMountedYOffset()
	{
		return yOffset + 0.5D;
	}
	
	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		return entity instanceof EntitySeat || entity.ridingEntity instanceof EntitySeat || entity instanceof EntityLiving ? null : entity.boundingBox;
		//return null;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox()
	{
		return boundingBox;
	}
	
	@Override
	public boolean canBePushed()
	{
		return onGround && !isInWater() && riddenByEntity == null;
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return !isDead;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		/*if (source.isExplosion())
		{
			if (source.getEntity() != null && source.getEntity().getClass().getName().equals("ckathode.weaponmod.entity.projectile.EntityCannonBall"))
			{
				double dx = source.getEntity().posX - posX;
				double dy = source.getEntity().posY - posY;
				double dz = source.getEntity().posZ - posZ;
				
				Vec3 vec = worldObj.getWorldVec3Pool().getVecFromPool(dx, dy, dz);
				vec.rotateAroundY((float) Math.toRadians(-rotationYaw));
				
				worldObj.createExplosion(source.getEntity(), source.getEntity().posX, source.getEntity().posY, source.getEntity().posZ, 4F, false);
				source.getEntity().setDead();
			}
		}*/
		return false;
	}
	
	@Override
	protected void updateFallState(double distancefallen, boolean onground)
	{
		if (!isFlying())
		{	
			
		}
	}
	
	@Override
	protected void fall(float distance)
	{
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.5F;
	}
	
	public float getHorizontalVelocity()
	{
		return (float) Math.sqrt(motionX * motionX + motionZ * motionZ);
	}
	
	@Override
	public boolean interactFirst(EntityPlayer entityplayer)
	{
		return handler.interact(entityplayer);
	}
	
	public void alignToGrid()
	{
		rotationYaw = Math.round(rotationYaw / 90F) * 90F;
		rotationPitch = 0F;
		
		Vec3 vec = Vec3.createVectorHelper(-shipChunk.getCenterX(), -shipChunk.minY(), -shipChunk.getCenterZ());
		vec.rotateAroundY((float) Math.toRadians(rotationYaw));
		
		int ix = MathHelperMod.round_double(vec.xCoord + posX);
		int iy = MathHelperMod.round_double(vec.yCoord + posY);
		int iz = MathHelperMod.round_double(vec.zCoord + posZ);
		
		posX = ix - vec.xCoord;
		posY = iy - vec.yCoord;
		posZ = iz - vec.zCoord;
		
		motionX = motionY = motionZ = 0D;
	}
	
	public boolean disassemble(boolean overwrite)
	{
		if (worldObj.isRemote) return true;
		
		updateRiderPosition();
		
		ChunkDisassembler disassembler = getDisassembler();
		disassembler.overwrite = overwrite;
		
		if (!disassembler.canDisassemble())
		{
			if (prevRiddenByEntity instanceof EntityPlayer)
			{
				ChatComponentText c = new ChatComponentText("Cannot disassemble ship here");
				((EntityPlayer) prevRiddenByEntity).addChatMessage(c);
			}
			return false;
		}
		
		AssembleResult result = disassembler.doDisassemble();
		if (result.getShipMarker() != null)
		{
			TileEntity te = result.getShipMarker().tileEntity;
			if (te instanceof TileEntityHelm)
			{
				((TileEntityHelm) te).setAssembleResult(result);
				((TileEntityHelm) te).setShipInfo(info);
			}
		}
		
		return true;
	}
	
	public void dropAsItems()
	{
		TileEntity tileentity;
		Block block;
		for (int i = shipChunk.minX(); i < shipChunk.maxX(); i++)
		{
			for (int j = shipChunk.minY(); j < shipChunk.maxY(); j++)
			{
				for (int k = shipChunk.minZ(); k < shipChunk.maxZ(); k++)
				{
					tileentity = shipChunk.getTileEntity(i, j, k);
					if (tileentity instanceof IInventory)
					{
						IInventory inv = (IInventory) tileentity;
						for (int it = 0; it < inv.getSizeInventory(); it++)
						{
							ItemStack is = inv.getStackInSlot(it);
							if (is != null)
							{
								entityDropItem(is, 0F);
							}
						}
					}
					block = shipChunk.getBlock(i, j, k);
					
					if (block != Blocks.air)
					{
						int meta = shipChunk.getBlockMetadata(i, j, k);
						block.dropBlockAsItem(worldObj, MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ), meta, 0);
					}
				}
			}
		}
	}
	
	void fillAirBlocks(Set<ChunkPosition> set, int x, int y, int z)
	{
		if (x < shipChunk.minX() - 1 || x > shipChunk.maxX() || y < shipChunk.minY() - 1 || y > shipChunk.maxY() || z < shipChunk.minZ() - 1 || z > shipChunk.maxZ()) return;
		ChunkPosition pos = new ChunkPosition(x, y, z);
		if (set.contains(pos)) return;
		
		set.add(pos);
		if (shipChunk.setBlockAsFilledAir(x, y, z))
		{
			fillAirBlocks(set, x, y + 1, z);
			//fillAirBlocks(set, x, y - 1, z);
			fillAirBlocks(set, x - 1, y, z);
			fillAirBlocks(set, x, y, z - 1);
			fillAirBlocks(set, x + 1, y, z);
			fillAirBlocks(set, x, y, z + 1);
		}
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(shipChunk.getMemoryUsage());
		DataOutputStream out = new DataOutputStream(baos);
		try
		{
			ChunkIO.writeAll(out, shipChunk);
			out.flush();
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		compound.setByteArray("chunk", baos.toByteArray());
		compound.setByte("seatX", (byte) seatX);
		compound.setByte("seatY", (byte) seatY);
		compound.setByte("seatZ", (byte) seatZ);
		compound.setByte("front", (byte) frontDirection);
		
		if (!shipChunk.chunkTileEntityMap.isEmpty())
		{
			NBTTagList tileentities = new NBTTagList();
			for (TileEntity tileentity : shipChunk.chunkTileEntityMap.values())
			{
				NBTTagCompound comp = new NBTTagCompound();
				tileentity.writeToNBT(comp);
				tileentities.appendTag(comp);
			}
			compound.setTag("tileent", tileentities);
		}
		
		compound.setString("name", info.shipName);
		if (info.owner != null)
		{
			compound.setString("owner", info.owner);
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		byte[] ab = compound.getByteArray("chunk");
		ByteArrayInputStream bais = new ByteArrayInputStream(ab);
		DataInputStream in = new DataInputStream(bais);
		try
		{
			ChunkIO.read(in, shipChunk);
			in.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		if (compound.hasKey("seat"))
		{
			short s = compound.getShort("seat");
			seatX = s & 0xF;
			seatY = s >>> 4 & 0xF;
			seatZ = s >>> 8 & 0xF;
			frontDirection = s >>> 12 & 3;
		} else
		{
			seatX = compound.getByte("seatX") & 0xFF;
			seatY = compound.getByte("seatY") & 0xFF;
			seatZ = compound.getByte("seatZ") & 0xFF;
			frontDirection = compound.getByte("front") & 3;
		}
		
		NBTTagList tileentities = compound.getTagList("tileent", 10);
		if (tileentities != null)
		{
			for (int i = 0; i < tileentities.tagCount(); i++)
			{
				NBTTagCompound comp = tileentities.getCompoundTagAt(i);
				TileEntity tileentity = TileEntity.createAndLoadEntity(comp);
				shipChunk.setTileEntity(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord, tileentity);
			}
		}
		
		info = new ShipInfo();
		info.shipName = compound.getString("name");
		if (compound.hasKey("owner"))
		{
			info.shipName = compound.getString("owner");
		}
	}
	
	@Override
	public void writeSpawnData(ByteBuf data)
	{
		data.writeByte(seatX);
		data.writeByte(seatY);
		data.writeByte(seatZ);
		data.writeByte(frontDirection);
		
		data.writeShort(info.shipName.length());
		data.writeBytes(info.shipName.getBytes());
		
		try
		{
			ChunkIO.writeAllCompressed(data, shipChunk);
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ShipSizeOverflowException ssoe)
		{
			disassemble(false);
			ArchimedesShipMod.modLog.warn("Ship is too large to be sent");
		}
	}
	
	@Override
	public void readSpawnData(ByteBuf data)
	{
		seatX = data.readUnsignedByte();
		seatY = data.readUnsignedByte();
		seatZ = data.readUnsignedByte();
		frontDirection = data.readUnsignedByte();
		
		byte[] ab = new byte[data.readShort()];
		data.readBytes(ab);
		info.shipName = new String(ab);
		try
		{
			ChunkIO.readCompressed(data, shipChunk);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		shipChunk.onChunkLoad();
	}
}
