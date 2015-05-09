package darkevilmac.archimedes.entity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import darkevilmac.archimedes.ArchimedesConfig;
import darkevilmac.archimedes.ArchimedesShipMod;
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
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

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
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return entity instanceof EntitySeat || entity.ridingEntity instanceof EntitySeat || entity instanceof EntityLiving ? null : entity.boundingBox;
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
        dataWatcher.addObject(29, 0F); // Engine power
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
                    alignToGrid();
                    updateRiderPosition(prevRiddenByEntity, riderDestinationX, riderDestinationY, riderDestinationZ, 1);
                    disassemble(false);
                } else {
                    if (!worldObj.isRemote && isFlying()) {
                        EntityParachute parachute = new EntityParachute(worldObj, this, riderDestinationX, riderDestinationY, riderDestinationZ);
                        if (worldObj.spawnEntityInWorld(parachute)) {
                            prevRiddenByEntity.mountEntity(parachute);
                            prevRiddenByEntity.setSneaking(false);
                        }
                    }
                }
                prevRiddenByEntity = null;
            }
        }

        if (riddenByEntity == null) {
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
            Vec3 vec = Vec3.createVectorHelper(0d, 0d, 0d);
            float yaw = (float) Math.toRadians(rotationYaw);
            for (TileEntityEngine engine : capabilities.getEngines()) {
                if (engine.isRunning()) {
                    vec.xCoord = engine.xCoord - getMovingWorldChunk().getCenterX() + 0.5f;
                    vec.yCoord = engine.yCoord;
                    vec.zCoord = engine.zCoord - getMovingWorldChunk().getCenterZ() + 0.5f;
                    vec.rotateAroundY(yaw);
                    worldObj.spawnParticle("smoke", posX + vec.xCoord, posY + vec.yCoord + 1d, posZ + vec.zCoord, 0d, 0d, 0d);
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
                newyaw = 270F - Math.toDegrees(Math.atan2(dz, dx)) + frontDirection * 90F;
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
        if (riddenByEntity instanceof EntityLivingBase) {
            double throttle = ((EntityLivingBase) riddenByEntity).moveForward;
            if (isFlying()) {
                throttle *= 0.5D;
            }

            if (ArchimedesShipMod.instance.modConfig.shipControlType == ArchimedesConfig.CONTROL_TYPE_ARCHIMEDES) {
                Vec3 vec = Vec3.createVectorHelper(riddenByEntity.motionX, 0D, riddenByEntity.motionZ);
                vec.rotateAroundY((float) Math.toRadians(riddenByEntity.rotationYaw));

                double steer = ((EntityLivingBase) riddenByEntity).moveStrafing;

                motionYaw += steer * BASE_TURN_SPEED * capabilities.getRotationMult() * ArchimedesShipMod.instance.modConfig.turnSpeed;

                float yaw = (float) Math.toRadians(180F - rotationYaw + frontDirection * 90F);
                vec.xCoord = motionX;
                vec.zCoord = motionZ;
                vec.rotateAroundY(yaw);
                vec.xCoord *= 0.9D;
                vec.zCoord -= throttle * BASE_FORWARD_SPEED * capabilities.getSpeedMult();
                vec.rotateAroundY(-yaw);

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
                alignToGrid();
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

    public void fillAirBlocks(Set<ChunkPosition> set, int x, int y, int z) {
        super.fillAirBlocks(set, x, y, z);
    }

    public ShipControllerCommon getController() {
        return controller;
    }
}