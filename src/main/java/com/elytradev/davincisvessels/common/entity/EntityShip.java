package com.elytradev.davincisvessels.common.entity;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.client.control.ShipControllerClient;
import com.elytradev.davincisvessels.common.DavincisVesselsConfig;
import com.elytradev.davincisvessels.common.api.tileentity.ITileEngineModifier;
import com.elytradev.davincisvessels.common.control.ShipControllerCommon;
import com.elytradev.davincisvessels.common.object.DavincisVesselsObjects;
import com.elytradev.davincisvessels.common.tileentity.TileHelm;
import com.elytradev.movingworld.common.chunk.LocatedBlock;
import com.elytradev.movingworld.common.chunk.MovingWorldAssemblyInteractor;
import com.elytradev.movingworld.common.chunk.assembly.AssembleResult;
import com.elytradev.movingworld.common.chunk.assembly.ChunkDisassembler;
import com.elytradev.movingworld.common.entity.EntityMovingWorld;
import com.elytradev.movingworld.common.entity.MovingWorldCapabilities;
import com.elytradev.movingworld.common.entity.MovingWorldHandlerCommon;
import com.elytradev.movingworld.common.util.MathHelperMod;
import com.elytradev.movingworld.common.util.Vec3dMod;
import io.netty.buffer.ByteBuf;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class EntityShip extends EntityMovingWorld {

    public static final DataParameter<Float> ENGINE_POWER = EntityDataManager.createKey(EntityShip.class, DataSerializers.FLOAT);
    public static final DataParameter<Boolean> CAN_MOVE = EntityDataManager.createKey(EntityShip.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> CAN_SUBMERGE = EntityDataManager.createKey(EntityShip.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Byte> IS_SUBMERGED = EntityDataManager.createKey(EntityShip.class, DataSerializers.BYTE);

    public static final float BASE_FORWARD_SPEED = 0.005F, BASE_TURN_SPEED = 0.5F, BASE_LIFT_SPEED = 0.004F;
    public ShipCapabilities capabilities;
    private ShipControllerCommon controller;
    private MovingWorldHandlerCommon handler;
    private ShipAssemblyInteractor shipAssemblyInteractor;
    private int driftCooldown = 0;
    private boolean submerge;

    public EntityShip(World world) {
        super(world);
        capabilities = new ShipCapabilities(this, true);
    }

    @Override
    public void assembleResultEntity() {
        super.assembleResultEntity();
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        if (world != null) {
            if (!world.isRemote) {
                driftCooldown -= 1;
                boolean hasEngines = false;
                if (capabilities.getEngines() != null) {
                    if (capabilities.getEngines().isEmpty())
                        hasEngines = false;
                    else {
                        hasEngines = capabilities.getEnginePower() > 0;
                    }
                }
                if (DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().enginesMandatory)
                    getDataManager().set(CAN_MOVE, hasEngines);
                else
                    getDataManager().set(CAN_MOVE, true);
            }
            if (world.isRemote) {
                if (dataManager != null && !dataManager.isEmpty() && dataManager.isDirty()) {
                    submerge = dataManager.get(IS_SUBMERGED) == new Byte((byte) 1);
                }
            }
        }
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(DavincisVesselsObjects.blockMarkShip);
    }

    public boolean getSubmerge() {
        return !getDataManager().isEmpty() ? (getDataManager().get(IS_SUBMERGED) == (byte) 1) : false;
    }

    public void setSubmerge(boolean submerge) {
        this.submerge = submerge;
        if (world != null && !world.isRemote) {
            getDataManager().set(IS_SUBMERGED, submerge ? new Byte((byte) 1) : new Byte((byte) 0));
            if (getMobileChunk().marker != null && getMobileChunk().marker.tileEntity instanceof TileHelm) {
                TileHelm helm = (TileHelm) getMobileChunk().marker.tileEntity;

                helm.submerge = submerge;
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        if (entity != null) {
            if (entity instanceof EntityMovingWorld) {
                EntityMovingWorld entityMovingWorld = (EntityMovingWorld) entity;
                return entityMovingWorld.getEntityBoundingBox();
            }
            if (entity instanceof EntitySeat || entity.getRidingEntity() instanceof EntitySeat
                || entity instanceof EntityLiving)
                return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        }
        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    @Override
    public MovingWorldHandlerCommon getHandler() {
        if (handler == null) {
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                handler = new ShipHandlerClient(this);
                handler.setMovingWorld(this);
            } else {
                handler = new ShipHandlerServer(this);
                handler.setMovingWorld(this);
            }
        }
        return handler;
    }

    @Override
    public void initMovingWorld() {
        dataManager.register(ENGINE_POWER, 0F);
        dataManager.register(CAN_MOVE, false);
        dataManager.register(CAN_SUBMERGE, false);
        dataManager.register(IS_SUBMERGED, (byte) 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initMovingWorldClient() {
        handler = new ShipHandlerClient(this);
        controller = new ShipControllerClient();
    }

    @Override
    public void initMovingWorldCommon() {
        handler = new ShipHandlerServer(this);
        controller = new ShipControllerCommon();
    }

    @Override
    public MovingWorldCapabilities getMovingWorldCapabilities() {
        return this.capabilities == null ? new ShipCapabilities(this, true) : this.capabilities;
    }

    @Override
    public void setCapabilities(MovingWorldCapabilities capabilities) {
        if (capabilities instanceof ShipCapabilities) {
            this.capabilities = (ShipCapabilities) capabilities;
        }
    }

    /**
     * Aligns to the closest anchor within the radius specified in the configuration.
     */
    public boolean alignToAnchor() {
        ImmutablePair<LocatedBlock, LocatedBlock> closestRelation = capabilities.findClosestValidAnchor(DavincisVesselsMod.INSTANCE.getNetworkConfig().anchorRadius);
        if (!closestRelation.getLeft().equals(LocatedBlock.AIR)
            && !closestRelation.getRight().equals(LocatedBlock.AIR)) {
            BlockPos chunkAnchor = closestRelation.getLeft().blockPos;
            BlockPos worldAnchor = closestRelation.getRight().blockPos;
            super.alignToGrid(true);

            float yaw = Math.round(rotationYaw / 90F) * 90F;
            yaw = (float) Math.toRadians(yaw);
            float ox = -getMobileChunk().getCenterX();
            float oz = -getMobileChunk().getCenterZ();
            Vec3dMod vecB = new Vec3dMod(chunkAnchor.getX() + ox, 0, chunkAnchor.getZ() + oz);
            Vec3dMod vec = vecB;
            vec = vec.rotateAroundY(yaw);

            BlockPos pos = new BlockPos(MathHelperMod.round_double(vec.x), 0, MathHelperMod.round_double(vec.z));
            setPositionAndUpdate(
                worldAnchor.getX() + -pos.getX(), worldAnchor.getY() + 2, worldAnchor.getZ() + -pos.getZ());

            super.alignToGrid(false);
            updatePassenger(getControllingPassenger());

            return true;
        }
        return false;
    }

    @Override
    public void alignToGrid(boolean doPosAdjustment) {
        if (!alignToAnchor())
            super.alignToGrid(true);
    }

    @Override
    public boolean isBraking() {
        return controller.getShipControl() == 3;
    }

    @Override
    public MovingWorldAssemblyInteractor getNewAssemblyInteractor() {
        return new ShipAssemblyInteractor();
    }

    @Override
    public void writeMovingWorldNBT(NBTTagCompound tag) {
        tag.setBoolean("submerge", submerge);
    }

    @Override
    public void readMovingWorldNBT(NBTTagCompound tag) {
        setSubmerge(tag.getBoolean("submerge"));
    }

    @Override
    public void writeMovingWorldSpawnData(ByteBuf data) {
    }

    @Override
    public void handleControl(double horizontalVelocity) {
        capabilities.updateEngines();

        if (getControllingPassenger() == null) {
            if (prevRiddenByEntity != null) {
                if (DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().disassembleOnDismount) {
                    alignToGrid(true);
                    updatePassengerPosition(prevRiddenByEntity, riderDestination, 1);
                    disassemble(false);
                } else {
                    if (!world.isRemote && isFlying()) {
                        driftCooldown = 20 * 6;
                        EntityParachute parachute = new EntityParachute(world, this, riderDestination);
                        if (world.spawnEntity(parachute)) {
                            prevRiddenByEntity.startRiding(parachute);
                            prevRiddenByEntity.setSneaking(false);
                        }
                    }
                }
                prevRiddenByEntity = null;
            }
        }

        if (getControllingPassenger() == null || !capabilities.canMove()) {
            if (isFlying()) {
                motionY -= BASE_LIFT_SPEED * 0.2F;
            }
        } else {
            handlePlayerControl();
            prevRiddenByEntity = getControllingPassenger();
        }
    }

    @Override
    public void updatePassengerPosition(Entity passenger, BlockPos riderDestination, int flags) {
        super.updatePassengerPosition(passenger, riderDestination, flags);

        if (submerge && passenger instanceof EntityLivingBase && world != null && !world.isRemote) {
            //Apply water breathing so we don't die and apply night vision so we're not blind.

            Potion waterBreathing = Potion.REGISTRY.getObject(new ResourceLocation("water_breathing"));
            if (((EntityLivingBase) passenger).getActivePotionEffect(waterBreathing) == null ||
                ((EntityLivingBase) passenger).getActivePotionEffect(waterBreathing).getDuration() <= 20 * 11)
                ((EntityLivingBase) passenger).addPotionEffect(new PotionEffect(waterBreathing, 20 * 12, 1));
            Potion nightVision = Potion.REGISTRY.getObject(new ResourceLocation("night_vision"));
            if (((EntityLivingBase) passenger).getActivePotionEffect(nightVision) == null ||
                ((EntityLivingBase) passenger).getActivePotionEffect(nightVision).getDuration() <= 20 * 11)
                ((EntityLivingBase) passenger).addPotionEffect(new PotionEffect(nightVision, 20 * 12, 1));
        }
    }

    /**
     * Currently overridden for future combat system.
     */
    protected String getHurtSound() {
        return "mob.irongolem.hit";
    }

    /**
     * Currently overridden for future combat system.
     */
    protected String getDeathSound() {
        return "mob.irongolem.death";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void spawnParticles(double horvel) {
        if (capabilities.getEngines() != null && !capabilities.getEngines().isEmpty()) {
            Vec3dMod vec = Vec3dMod.getOrigin();
            float yaw = (float) Math.toRadians(rotationYaw);
            for (ITileEngineModifier engine : capabilities.getEngines()) {
                if (engine.getPowerIncrement(capabilities) != 0F) {
                    vec = vec.setX(((TileEntity) engine).getPos().getX() - getMobileChunk().getCenterX() + 0.5f);
                    vec = vec.setY(((TileEntity) engine).getPos().getY());
                    vec = vec.setZ(((TileEntity) engine).getPos().getZ() - getMobileChunk().getCenterZ() + 0.5f);
                    vec = vec.rotateAroundY(yaw);
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                        posX + vec.x, posY + vec.y + 1d, posZ + vec.z, 0d, 0d, 0d);
                }
            }
        }
    }

    public int getBelowWater() {
        byte b0 = 5;
        int blocksPerMeter = (int) (b0 * (getEntityBoundingBox().maxY - getEntityBoundingBox().minY));
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(0D, 0D, 0D, 0D, 0D, 0D);
        int belowWater = 0;
        for (; belowWater < blocksPerMeter; belowWater++) {
            double d1 = getEntityBoundingBox().minY
                + (getEntityBoundingBox().maxY - getEntityBoundingBox().minY) * belowWater / blocksPerMeter;
            double d2 = getEntityBoundingBox().minY
                + (getEntityBoundingBox().maxY - getEntityBoundingBox().minY) * (belowWater + 1) / blocksPerMeter;
            axisalignedbb = new AxisAlignedBB(getEntityBoundingBox().minX, d1, getEntityBoundingBox().minZ, getEntityBoundingBox().maxX, d2, getEntityBoundingBox().maxZ);

            if (!isAABBInLiquidNotFall(world, axisalignedbb)) {
                break;
            }
        }

        return belowWater;
    }

    @Override
    public void handleServerUpdate(double horizontalVelocity) {
        boolean submergeMode = getSubmerge();

        byte b0 = 5;
        int blocksPerMeter = (int) (b0 * (getEntityBoundingBox().maxY - getEntityBoundingBox().minY));
        float waterVolume = 0F;
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(0D, 0D, 0D, 0D, 0D, 0D);
        int belowWater = 0;
        for (; belowWater < blocksPerMeter; belowWater++) {
            double d1 = getEntityBoundingBox().minY
                + (getEntityBoundingBox().maxY - getEntityBoundingBox().minY) * belowWater / blocksPerMeter;
            double d2 = getEntityBoundingBox().minY
                + (getEntityBoundingBox().maxY - getEntityBoundingBox().minY) * (belowWater + 1) / blocksPerMeter;
            axisalignedbb = new AxisAlignedBB(getEntityBoundingBox().minX, d1, getEntityBoundingBox().minZ, getEntityBoundingBox().maxX, d2, getEntityBoundingBox().maxZ);

            if (!isAABBInLiquidNotFall(world, axisalignedbb)) {
                break;
            }
        }
        if (belowWater > 0 && layeredBlockVolumeCount != null) {
            int k = belowWater / b0;
            for (int y = 0; y <= k && y < layeredBlockVolumeCount.length; y++) {
                if (y == k) {
                    waterVolume += layeredBlockVolumeCount[y] * (belowWater % b0) * 1F / b0;
                } else {
                    waterVolume += layeredBlockVolumeCount[y] * 1F;
                }
            }
        }

        if (onGround) {
            setFlying(false);
        }

        float gravity = 0.05F;
        if (waterVolume > 0F && !submergeMode) {
            setFlying(false);
            float buoyancyforce = 1F * waterVolume * gravity; //F = rho * V * g (Archimedes' principle)
            float mass = getMovingWorldCapabilities().getMass();
            motionY += buoyancyforce / mass;
        }

        if (DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().enableShipDownfall) {
            if (!isFlying() || (submergeMode && belowWater <= (getMobileChunk().maxY() * 5 / 3 * 2)))
                motionY -= gravity;
        } else {
            if (!capabilities.canFly() && !capabilities.canSubmerge())
                motionY -= gravity;
        }

        super.handleServerUpdate(horizontalVelocity);
    }

    @Override
    public void handleServerUpdatePreRotation() {
        if (DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().shipControlType
            == DavincisVesselsConfig.CONTROL_TYPE_VANILLA) {
            double newYaw = rotationYaw;
            double dx = prevPosX - posX;
            double dz = prevPosZ - posZ;

            if (getControllingPassenger() != null && !isBraking() && dx * dx + dz * dz > 0.01D) {
                newYaw = 270F - Math.toDegrees(Math.atan2(dz, dx)) + frontDirection.getHorizontalIndex() * 90F;
            }

            double deltayaw = MathHelper.wrapDegrees(newYaw - rotationYaw);
            double maxyawspeed = 2D;
            if (deltayaw > maxyawspeed) {
                deltayaw = maxyawspeed;
            }
            if (deltayaw < -maxyawspeed) {
                deltayaw = -maxyawspeed;
            }

            rotationYaw = (float) (rotationYaw + deltayaw);
        }
    }

    @Override
    public boolean disassemble(boolean overwrite) {
        if (world.isRemote)
            return true;

        updatePassenger(getControllingPassenger());

        ChunkDisassembler disassembler = getDisassembler();
        disassembler.overwrite = overwrite;

        if (!disassembler.canDisassemble(getNewAssemblyInteractor())) {
            if (prevRiddenByEntity instanceof EntityPlayer) {
                TextComponentString testMessage = new TextComponentString("Cannot disassemble ship here");
                ((EntityPlayer) prevRiddenByEntity).sendStatusMessage(testMessage, true);
            }
            return false;
        }

        AssembleResult result = disassembler.doDisassemble(getNewAssemblyInteractor());
        if (result.getShipMarker() != null) {
            TileEntity te = result.getShipMarker().tileEntity;
            if (te instanceof TileHelm) {
                ((TileHelm) te).setAssembleResult(result);
                ((TileHelm) te).setInfo(getInfo());
            }
        }

        return true;
    }

    private void handlePlayerControl() {
        if (getControllingPassenger() instanceof EntityLivingBase && ((ShipCapabilities) getMovingWorldCapabilities()).canMove()) {
            double throttle = ((EntityLivingBase) getControllingPassenger()).moveForward;
            if (isFlying()) {
                throttle *= 0.5D;
            }

            if (DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().shipControlType
                == DavincisVesselsConfig.CONTROL_TYPE_DAVINCI) {
                Vec3dMod vec = new Vec3dMod(getControllingPassenger().motionX, 0D, getControllingPassenger().motionZ);
                vec.rotateAroundY((float) Math.toRadians(getControllingPassenger().rotationYaw));

                double steer = ((EntityLivingBase) getControllingPassenger()).moveStrafing;

                motionYaw += steer * BASE_TURN_SPEED * capabilities.getRotationMult()
                    * DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().turnSpeed;

                float yaw = (float) Math.toRadians(180F - rotationYaw + frontDirection.getHorizontalIndex() * 90F);
                vec = vec.setX(motionX);
                vec = vec.setZ(motionZ);
                vec = vec.rotateAroundY(yaw);
                vec = vec.setX(vec.x * 0.9D);
                vec = vec.setZ(vec.z - throttle * BASE_FORWARD_SPEED * capabilities.getSpeedMult());
                vec = vec.rotateAroundY(-yaw);

                motionX = vec.x;
                motionZ = vec.z;
            } else if (DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().shipControlType
                == DavincisVesselsConfig.CONTROL_TYPE_VANILLA) {
                if (throttle > 0.0D) {
                    double dsin = -Math.sin(Math.toRadians(getControllingPassenger().rotationYaw));
                    double dcos = Math.cos(Math.toRadians(getControllingPassenger().rotationYaw));
                    motionX += dsin * BASE_FORWARD_SPEED * capabilities.speedMultiplier;
                    motionZ += dcos * BASE_FORWARD_SPEED * capabilities.speedMultiplier;
                }
            }
        }

        if (controller.getShipControl() != 0) {
            if (controller.getShipControl() == 4) {
                alignToGrid(true);
            } else if (isBraking()) {
                motionX *= capabilities.brakeMult;
                motionZ *= capabilities.brakeMult;
                if (isFlying()) {
                    motionY *= capabilities.brakeMult;
                }
            } else if (controller.getShipControl() < 3 && capabilities.canFly()) {
                int i;
                if (controller.getShipControl() == 2) {
                    setFlying(true);
                    i = 1;
                } else {
                    i = -1;
                }
                motionY += i * BASE_LIFT_SPEED * capabilities.getLiftMult();
                // TODO: Achievements are gone.
                //if (getControllingPassenger() != null && getControllingPassenger() instanceof EntityPlayer
                //        && !((EntityPlayer) getControllingPassenger()).hasAchievement(DavincisVesselsObjects.achievementFlyShip))
                //    ((EntityPlayer) getControllingPassenger()).addStat(DavincisVesselsObjects.achievementFlyShip);
            }
        }
    }

    @Override
    public boolean canBePushed() {
        return !isDead && getControllingPassenger() == null && driftCooldown <= 0;
    }

    @Override
    public boolean isFlying() {
        return (capabilities.canFly() && (super.isFlying() || controller.getShipControl() == 2)) || getSubmerge();
    }

    public boolean areSubmerged() {
        int belowWater = getBelowWater();

        return getSubmerge() && belowWater > 0;
    }

    @Override
    public void readMovingWorldSpawnData(ByteBuf data) {
    }

    @Override
    public float getXRenderScale() {
        return 1F;
    }

    @Override
    public float getYRenderScale() {
        return 1F;
    }

    @Override
    public float getZRenderScale() {
        return 1F;
    }

    @Override
    public MovingWorldAssemblyInteractor getAssemblyInteractor() {
        if (shipAssemblyInteractor == null)
            shipAssemblyInteractor = (ShipAssemblyInteractor) getNewAssemblyInteractor();

        return shipAssemblyInteractor;
    }

    @Override
    public void setAssemblyInteractor(MovingWorldAssemblyInteractor interactor) {
        //shipAssemblyInteractor = (ShipAssemblyInteractor) interactor;
        //interactor.transferToCapabilities(getMovingWorldCapabilities());
    }

    @Override
    public void fillAirBlocks(Set<BlockPos> set, BlockPos pos) {
        super.fillAirBlocks(set, pos);
    }

    public ShipControllerCommon getController() {
        return controller;
    }

    public boolean canSubmerge() {
        return !dataManager.isEmpty() ? dataManager.get(CAN_SUBMERGE) : false;
    }
}