package com.tridevmc.davincisvessels.common.entity;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.client.control.ShipControllerClient;
import com.tridevmc.davincisvessels.client.gui.ContainerShip;
import com.tridevmc.davincisvessels.client.gui.GuiShip;
import com.tridevmc.davincisvessels.common.IElementProvider;
import com.tridevmc.davincisvessels.common.api.tileentity.ITileEngineModifier;
import com.tridevmc.davincisvessels.common.control.EnumShipControlType;
import com.tridevmc.davincisvessels.common.control.ShipControllerCommon;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import com.tridevmc.movingworld.common.chunk.LocatedBlock;
import com.tridevmc.movingworld.common.chunk.MovingWorldAssemblyInteractor;
import com.tridevmc.movingworld.common.chunk.assembly.AssembleResult;
import com.tridevmc.movingworld.common.chunk.assembly.ChunkDisassembler;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import com.tridevmc.movingworld.common.entity.MovingWorldCapabilities;
import com.tridevmc.movingworld.common.entity.MovingWorldHandlerCommon;
import com.tridevmc.movingworld.common.util.MathHelperMod;
import com.tridevmc.movingworld.common.util.Vec3dMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Set;

public class EntityShip extends EntityMovingWorld implements IElementProvider<ContainerShip> {

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
        super((EntityType<? extends EntityMovingWorld>) DavincisVesselsMod.CONTENT.entityTypes.get(EntityShip.class), world);
        capabilities = new ShipCapabilities(this, true);
    }

    @Override
    public EntityType<?> getType() {
        return DavincisVesselsMod.CONTENT.entityTypes.get(EntityShip.class);
    }

    @Override
    public void assembleResultEntity() {
        super.assembleResultEntity();
    }

    @Override
    public void baseTick() {
        super.baseTick();

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
                if (DavincisVesselsMod.CONFIG.enginesMandatory)
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
        return new ItemStack(DavincisVesselsMod.CONTENT.blockHelm);
    }

    public boolean getSubmerge() {
        return !getDataManager().isEmpty() ? (getDataManager().get(IS_SUBMERGED) == (byte) 1) : false;
    }

    public void setSubmerge(boolean submerge) {
        this.submerge = submerge;
        if (world != null && !world.isRemote) {
            getDataManager().set(IS_SUBMERGED, submerge ? new Byte((byte) 1) : new Byte((byte) 0));
            if (getMobileChunk().marker != null && getMobileChunk().marker.tile instanceof TileHelm) {
                TileHelm helm = (TileHelm) getMobileChunk().marker.tile;

                helm.submerge = submerge;
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        if (entity != null) {
            if (entity instanceof EntityMovingWorld) {
                EntityMovingWorld entityMovingWorld = (EntityMovingWorld) entity;
                return entityMovingWorld.getBoundingBox();
            }
            if (entity instanceof EntitySeat || entity.getRidingEntity() instanceof EntitySeat
                    || entity instanceof MobEntity)
                return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        }
        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    @Override
    public MovingWorldHandlerCommon getHandler() {
        if (handler == null) {
            if (EffectiveSide.get() == LogicalSide.CLIENT) {
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
    @OnlyIn(Dist.CLIENT)
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
        ImmutablePair<LocatedBlock, LocatedBlock> closestRelation = capabilities.findClosestValidAnchor(DavincisVesselsMod.CONFIG.anchorRadius);
        if (!closestRelation.getLeft().equals(LocatedBlock.AIR)
                && !closestRelation.getRight().equals(LocatedBlock.AIR)) {
            BlockPos chunkAnchor = closestRelation.getLeft().pos;
            BlockPos worldAnchor = closestRelation.getRight().pos;
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
    public void writeMovingWorldNBT(CompoundNBT tag) {
        tag.putBoolean("submerge", submerge);
    }

    @Override
    public void readMovingWorldNBT(CompoundNBT tag) {
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
                if (DavincisVesselsMod.CONFIG.disassembleOnDismount) {
                    alignToGrid(true);
                    updatePassengerPosition(prevRiddenByEntity, riderDestination, 1);
                    disassemble(false);
                } else {
                    if (!world.isRemote && isFlying()) {
                        driftCooldown = 20 * 6;
                        EntityParachute parachute = new EntityParachute(world, this, riderDestination);
                        if (world.addEntity(parachute)) {
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
                this.setMotion(this.getMotion().subtract(0, BASE_LIFT_SPEED * 0.2F, 0));
            }
        } else {
            handlePlayerControl();
            prevRiddenByEntity = getControllingPassenger();
        }
    }

    @Override
    public void updatePassengerPosition(Entity passenger, BlockPos riderDestination, int flags) {
        super.updatePassengerPosition(passenger, riderDestination, flags);

        if (submerge && passenger instanceof LivingEntity && world != null && !world.isRemote) {
            //Apply water breathing so we don't die and apply night vision so we're not blind.

            LivingEntity livingPassenger = (LivingEntity) passenger;
            IForgeRegistry<Effect> potions = ForgeRegistries.POTIONS;
            Effect waterBreathing = potions.getValue(new ResourceLocation("water_breathing"));
            if (livingPassenger.getActivePotionEffect(waterBreathing) == null ||
                    livingPassenger.getActivePotionEffect(waterBreathing).getDuration() <= 20 * 11)
                livingPassenger.addPotionEffect(new EffectInstance(waterBreathing, 20 * 12, 1));
            Effect nightVision = potions.getValue(new ResourceLocation("night_vision"));
            if (livingPassenger.getActivePotionEffect(nightVision) == null ||
                    livingPassenger.getActivePotionEffect(nightVision).getDuration() <= 20 * 11)
                livingPassenger.addPotionEffect(new EffectInstance(nightVision, 20 * 12, 1));
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
    @OnlyIn(Dist.CLIENT)
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
                    world.addParticle(ParticleTypes.LARGE_SMOKE,
                            posX + vec.x, posY + vec.y + 1d, posZ + vec.z, 0d, 0d, 0d);
                }
            }
        }
    }

    public int getBelowWater() {
        byte b0 = 5;
        int blocksPerMeter = (int) (b0 * (getBoundingBox().maxY - getBoundingBox().minY));
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(0D, 0D, 0D, 0D, 0D, 0D);
        int belowWater = 0;
        for (; belowWater < blocksPerMeter; belowWater++) {
            double d1 = getBoundingBox().minY
                    + (getBoundingBox().maxY - getBoundingBox().minY) * belowWater / blocksPerMeter;
            double d2 = getBoundingBox().minY
                    + (getBoundingBox().maxY - getBoundingBox().minY) * (belowWater + 1) / blocksPerMeter;
            axisalignedbb = new AxisAlignedBB(getBoundingBox().minX, d1, getBoundingBox().minZ, getBoundingBox().maxX, d2, getBoundingBox().maxZ);

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
        int blocksPerMeter = (int) (b0 * (getBoundingBox().maxY - getBoundingBox().minY));
        float waterVolume = 0F;
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(0D, 0D, 0D, 0D, 0D, 0D);
        int belowWater = 0;
        for (; belowWater < blocksPerMeter; belowWater++) {
            double d1 = getBoundingBox().minY
                    + (getBoundingBox().maxY - getBoundingBox().minY) * belowWater / blocksPerMeter;
            double d2 = getBoundingBox().minY
                    + (getBoundingBox().maxY - getBoundingBox().minY) * (belowWater + 1) / blocksPerMeter;
            axisalignedbb = new AxisAlignedBB(getBoundingBox().minX, d1, getBoundingBox().minZ, getBoundingBox().maxX, d2, getBoundingBox().maxZ);

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
            setMotion(getMotion().add(0, buoyancyforce / mass, 0));
        }

        if (DavincisVesselsMod.CONFIG.enableShipDownfall) {
            if (!isFlying() || (submergeMode && belowWater <= (getMobileChunk().maxY() * 5 / 3 * 2)))
                setMotion(getMotion().subtract(0, gravity, 0));
        } else {
            if (!capabilities.canFly() && !capabilities.canSubmerge())
                setMotion(getMotion().subtract(0, gravity, 0));
        }

        super.handleServerUpdate(horizontalVelocity);
    }

    @Override
    public void handleServerUpdatePreRotation() {
        if (DavincisVesselsMod.CONFIG.shipControlType == EnumShipControlType.VANILLA) {
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
            if (prevRiddenByEntity instanceof PlayerEntity) {
                StringTextComponent testMessage = new StringTextComponent("Cannot disassemble ship here");
                ((PlayerEntity) prevRiddenByEntity).sendStatusMessage(testMessage, true);
            }
            return false;
        }

        AssembleResult result = disassembler.doDisassemble(getNewAssemblyInteractor());
        if (result.getShipMarker() != null) {
            TileEntity te = result.getShipMarker().tile;
            if (te instanceof TileHelm) {
                ((TileHelm) te).setAssembleResult(result);
                ((TileHelm) te).setInfo(getInfo());
            }
        }

        return true;
    }

    private void handlePlayerControl() {
        if (getControllingPassenger() instanceof LivingEntity && ((ShipCapabilities) getMovingWorldCapabilities()).canMove()) {
            double throttle = ((LivingEntity) getControllingPassenger()).moveForward;
            if (isFlying()) {
                throttle *= 0.5D;
            }

            if (DavincisVesselsMod.CONFIG.shipControlType == EnumShipControlType.DAVINCIS) {
                Vec3dMod vec = new Vec3dMod(getControllingPassenger().getMotion().x, 0D, getControllingPassenger().getMotion().z);
                vec.rotateAroundY((float) Math.toRadians(getControllingPassenger().rotationYaw));

                double steer = ((LivingEntity) getControllingPassenger()).moveStrafing;

                motionYaw += steer * BASE_TURN_SPEED * capabilities.getRotationMult()
                        * DavincisVesselsMod.CONFIG.turnSpeed;

                float yaw = (float) Math.toRadians(180F - rotationYaw + frontDirection.getHorizontalIndex() * 90F);
                vec = vec.setX(getMotion().x);
                vec = vec.setZ(getMotion().z);
                vec = vec.rotateAroundY(yaw);
                vec = vec.setX(vec.x * 0.9D);
                vec = vec.setZ(vec.z - throttle * BASE_FORWARD_SPEED * capabilities.getSpeedMult());
                vec = vec.rotateAroundY(-yaw);
                vec = vec.setY(getMotion().y);
                this.setMotion(vec);
            } else if (DavincisVesselsMod.CONFIG.shipControlType == EnumShipControlType.VANILLA) {
                if (throttle > 0.0D) {
                    double dsin = -Math.sin(Math.toRadians(getControllingPassenger().rotationYaw));
                    double dcos = Math.cos(Math.toRadians(getControllingPassenger().rotationYaw));

                    this.setMotion(this.getMotion().add(dsin * BASE_FORWARD_SPEED * capabilities.speedMultiplier, 0,
                            dcos * BASE_FORWARD_SPEED * capabilities.speedMultiplier));
                }
            }
        }

        if (controller.getShipControl() != 0) {
            if (controller.getShipControl() == 4) {
                alignToGrid(true);
            } else if (isBraking()) {
                float yMult = isFlying() ? capabilities.brakeMult : 1;
                this.setMotion(this.getMotion().mul(capabilities.brakeMult, yMult, capabilities.brakeMult));
            } else if (controller.getShipControl() < 3 && capabilities.canFly()) {
                int i;
                if (controller.getShipControl() == 2) {
                    setFlying(true);
                    i = 1;
                } else {
                    i = -1;
                }
                this.setMotion(this.getMotion().add(0, i * BASE_LIFT_SPEED * capabilities.getLiftMult(), 0));
                // TODO: Achievements are gone.
                //if (getControllingPassenger() != null && getControllingPassenger() instanceof EntityPlayer
                //        && !((EntityPlayer) getControllingPassenger()).hasAchievement(DavincisVesselsContent.achievementFlyShip))
                //    ((EntityPlayer) getControllingPassenger()).addStat(DavincisVesselsContent.achievementFlyShip);
            }
        }
    }

    @Override
    public boolean canBePushed() {
        return !removed && getControllingPassenger() == null && driftCooldown <= 0;
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public Screen createScreen(ContainerShip container, PlayerEntity player) {
        return new GuiShip(container);
    }

    @Override
    public Container createMenu(int window, PlayerInventory playerInventory, PlayerEntity playerIn) {
        return new ContainerShip(window, this, playerIn);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}