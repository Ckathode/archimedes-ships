package darkevilmac.archimedes.entity;

import darkevilmac.archimedes.ArchimedesConfig;
import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.blockitem.TileEntityAnchorPoint;
import darkevilmac.archimedes.blockitem.TileEntityEngine;
import darkevilmac.archimedes.blockitem.TileEntityHelm;
import darkevilmac.archimedes.control.ShipControllerClient;
import darkevilmac.archimedes.control.ShipControllerCommon;
import darkevilmac.movingworld.chunk.AssembleResult;
import darkevilmac.movingworld.chunk.ChunkDisassembler;
import darkevilmac.movingworld.chunk.MovingWorldAssemblyInteractor;
import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.entity.MovingWorldCapabilities;
import darkevilmac.movingworld.entity.MovingWorldHandlerCommon;
import darkevilmac.movingworld.util.Vec3Mod;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

public class EntityShip extends EntityMovingWorld {

    public static final float BASE_FORWARD_SPEED = 0.005F, BASE_TURN_SPEED = 0.5F, BASE_LIFT_SPEED = 0.004F;
    public ShipCapabilities capabilities;
    private ShipControllerCommon controller;
    private MovingWorldHandlerCommon handler;
    private ShipAssemblyInteractor shipAssemblyInteractor;

    public EntityShip(World world) {
        super(world);
        capabilities = new ShipCapabilities(this, true);
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        if (worldObj != null && !worldObj.isRemote) {
            boolean hasEngines = false;
            if (capabilities.getEngines() != null) {
                if (capabilities.getEngines().isEmpty())
                    hasEngines = false;
                else {
                    hasEngines = capabilities.getEnginePower() > 0;
                }
            }
            if (ArchimedesShipMod.instance.modConfig.enginesMandatory)
                getDataWatcher().updateObject(28, new Byte(hasEngines ? (byte) 1 : (byte) 0));
            else
                getDataWatcher().updateObject(28, new Byte((byte) 1));
        }
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return entity instanceof EntitySeat || entity.ridingEntity instanceof EntitySeat || entity instanceof EntityLiving ? null : entity.getBoundingBox();
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
        getCapabilities();
        dataWatcher.addObject(29, 0F); // Engine power
        dataWatcher.addObject(28, new Byte((byte) 0)); // Do we have any engines
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
    public MovingWorldCapabilities getCapabilities() {
        return this.capabilities == null ? new ShipCapabilities(this, true) : this.capabilities;
    }

    @Override
    public void setCapabilities(MovingWorldCapabilities capabilities) {
        if (capabilities != null && capabilities instanceof ShipCapabilities) {
            this.capabilities = (ShipCapabilities) capabilities;
        }
    }

    /**
     * Aligns to the closest anchor within 16 blocks.
     *
     * @return
     */
    public boolean alignToAnchor() {
        for (int amountToIgnore = 0; amountToIgnore < 100; amountToIgnore++) {
            if (capabilities.findClosestValidAnchor(16) != null) {
                TileEntityAnchorPoint anchorPoint = capabilities.findClosestValidAnchor(16);
                setPosition(anchorPoint.getPos().getX() - 0, anchorPoint.getPos().getY() + 2, anchorPoint.getPos().getZ() - 0);
            } else {
                alignToGrid();
                return false;
            }
        }

        alignToGrid();

        return false;
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
    public void writeMovingWorldNBT(NBTTagCompound compound) {
    }

    @Override
    public void readMovingWorldNBT(NBTTagCompound compound) {
    }

    @Override
    public void writeMovingWorldSpawnData(ByteBuf data) {
    }

    @Override
    public void handleControl(double horvel) {
        capabilities.updateEngines();

        if (riddenByEntity == null) {
            if (prevRiddenByEntity != null) {
                if (ArchimedesShipMod.instance.modConfig.disassembleOnDismount) {
                    alignToAnchor();
                    updateRiderPosition(prevRiddenByEntity, riderDestination, 1);
                    disassemble(false);
                } else {
                    if (!worldObj.isRemote && isFlying()) {
                        EntityParachute parachute = new EntityParachute(worldObj, this, riderDestination);
                        if (worldObj.spawnEntityInWorld(parachute)) {
                            prevRiddenByEntity.mountEntity(parachute);
                            prevRiddenByEntity.setSneaking(false);
                        }
                    }
                }
                prevRiddenByEntity = null;
            }
        }

        if (riddenByEntity == null || !capabilities.canMove()) {
            if (isFlying()) {
                motionY -= BASE_LIFT_SPEED * 0.2F;
            }
        } else {
            handlePlayerControl();
            prevRiddenByEntity = riddenByEntity;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void spawnParticles(double horvel) {
        if (capabilities.getEngines() != null) {
            Vec3Mod vec = Vec3Mod.getOrigin();
            float yaw = (float) Math.toRadians(rotationYaw);
            for (TileEntityEngine engine : capabilities.getEngines()) {
                if (engine.isRunning()) {
                    vec = vec.setX(engine.getPos().getX() - getMovingWorldChunk().getCenterX() + 0.5f);
                    vec = vec.setY(engine.getPos().getY());
                    vec = vec.setZ(engine.getPos().getZ() - getMovingWorldChunk().getCenterZ() + 0.5f);
                    vec = vec.rotateAroundY(yaw);
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX + vec.xCoord, posY + vec.yCoord + 1d, posZ + vec.zCoord, 0d, 0d, 0d);
                }
            }
        }
    }

    @Override
    public void handleServerUpdatePreRotation() {
        if (ArchimedesShipMod.instance.modConfig.shipControlType == ArchimedesConfig.CONTROL_TYPE_VANILLA) {
            double newyaw = rotationYaw;
            double dx = prevPosX - posX;
            double dz = prevPosZ - posZ;

            if (riddenByEntity != null && !isBraking() && dx * dx + dz * dz > 0.01D) {
                newyaw = 270F - Math.toDegrees(Math.atan2(dz, dx)) + frontDirection.getOpposite().getHorizontalIndex() * 90F;
            }

            double deltayaw = MathHelper.wrapAngleTo180_double(newyaw - rotationYaw);
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
        if (worldObj.isRemote) return true;

        updateRiderPosition();

        ChunkDisassembler disassembler = getDisassembler();
        disassembler.overwrite = overwrite;

        if (!disassembler.canDisassemble(getNewAssemblyInteractor())) {
            if (prevRiddenByEntity instanceof EntityPlayer) {
                ChatComponentText c = new ChatComponentText("Cannot disassemble ship here");
                ((EntityPlayer) prevRiddenByEntity).addChatMessage(c);
            }
            return false;
        }

        AssembleResult result = disassembler.doDisassemble(getNewAssemblyInteractor());
        if (result.getShipMarker() != null) {
            TileEntity te = result.getShipMarker().tileEntity;
            if (te instanceof TileEntityHelm) {
                ((TileEntityHelm) te).setAssembleResult(result);
                ((TileEntityHelm) te).setInfo(getInfo());
            }
        }

        return true;
    }

    private void handlePlayerControl() {
        if (riddenByEntity instanceof EntityLivingBase && ((ShipCapabilities) getCapabilities()).canMove()) {
            double throttle = ((EntityLivingBase) riddenByEntity).moveForward;
            if (isFlying()) {
                throttle *= 0.5D;
            }

            if (ArchimedesShipMod.instance.modConfig.shipControlType == ArchimedesConfig.CONTROL_TYPE_ARCHIMEDES) {
                Vec3Mod vec = new Vec3Mod(riddenByEntity.motionX, 0D, riddenByEntity.motionZ);
                vec = vec.rotateAroundY((float) Math.toRadians(riddenByEntity.rotationYaw));

                double steer = ((EntityLivingBase) riddenByEntity).moveStrafing;

                motionYaw += steer * BASE_TURN_SPEED * capabilities.getRotationMult() * ArchimedesShipMod.instance.modConfig.turnSpeed;

                float yaw = (float) Math.toRadians(180F - rotationYaw + frontDirection.getOpposite().getHorizontalIndex() * 90F);
                vec = vec.setX(motionX);
                vec = vec.setZ(motionZ);
                vec = vec.rotateAroundY(yaw);
                vec = vec.setX(vec.xCoord * 0.9D);
                vec = vec.setZ(vec.zCoord - throttle * BASE_FORWARD_SPEED * capabilities.getSpeedMult());
                vec = vec.rotateAroundY(-yaw);

                motionX = vec.xCoord;
                motionZ = vec.zCoord;

            } else if (ArchimedesShipMod.instance.modConfig.shipControlType == ArchimedesConfig.CONTROL_TYPE_VANILLA) {
                if (throttle > 0.0D) {
                    double dsin = -Math.sin(Math.toRadians(riddenByEntity.rotationYaw));
                    double dcos = Math.cos(Math.toRadians(riddenByEntity.rotationYaw));
                    motionX += dsin * BASE_FORWARD_SPEED * capabilities.speedMultiplier;
                    motionZ += dcos * BASE_FORWARD_SPEED * capabilities.speedMultiplier;
                }
            }
        }

        if (controller.getShipControl() != 0) {
            if (controller.getShipControl() == 4) {
                alignToAnchor();
            } else if (isBraking()) {
                motionX *= capabilities.brakeMult;
                motionZ *= capabilities.brakeMult;
                if (isFlying()) {
                    motionY *= capabilities.brakeMult;
                }
            } else if (controller.getShipControl() < 3 && capabilities.canFly()) {
                int i;
                if (controller.getShipControl() == 2) {
                    isFlying = true;
                    i = 1;
                } else {
                    i = -1;
                }
                motionY += i * BASE_LIFT_SPEED * capabilities.getLiftMult();
            }
        }
    }

    @Override
    public boolean isFlying() {
        return capabilities.canFly() && (isFlying || controller.getShipControl() == 2);
    }

    @Override
    public void readMovingWorldSpawnData(ByteBuf data) {
    }

    @Override
    public float getXRenderScale() {
        return 1.000001F;
    }

    @Override
    public float getYRenderScale() {
        return 1.000001F;
    }

    @Override
    public float getZRenderScale() {
        return 1.000001F;
    }

    @Override
    public MovingWorldAssemblyInteractor getAssemblyInteractor() {
        return shipAssemblyInteractor;
    }

    @Override
    public void setAssemblyInteractor(MovingWorldAssemblyInteractor interactor) {
        //shipAssemblyInteractor = (ShipAssemblyInteractor) interactor;
        //interactor.transferToCapabilities(getCapabilities());
    }

    public void fillAirBlocks(Set<BlockPos> set, BlockPos pos) {
        super.fillAirBlocks(set, pos);
    }

    public ShipControllerCommon getController() {
        return controller;
    }
}