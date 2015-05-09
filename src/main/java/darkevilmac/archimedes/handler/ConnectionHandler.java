package darkevilmac.archimedes.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import darkevilmac.archimedes.entity.EntityParachute;
import darkevilmac.archimedes.entity.EntitySeat;
import darkevilmac.archimedes.entity.EntityShip;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ConnectionHandler {

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.isCanceled())
            return;
        if (event.player != null && event.player.worldObj != null && !event.player.worldObj.isRemote) {
            if (event.player.ridingEntity != null && event.player.ridingEntity instanceof EntitySeat) {
                EntityPlayer player = event.player;
                EntitySeat seat = (EntitySeat) player.ridingEntity;
                EntityShip ship = seat.getParentShip();

                player.mountEntity(null);
                if (ship != null && seat.getPos() != null) {
                    NBTTagCompound nbt = new NBTTagCompound();

                    Vec3 vec = Vec3.createVectorHelper(seat.getPos().chunkPosX - ship.getMovingWorldChunk().getCenterX(), seat.getPos().chunkPosY - ship.getMovingWorldChunk().minY(), seat.getPos().chunkPosZ - ship.getMovingWorldChunk().getCenterZ());
                    vec.rotateAroundY((float) Math.toRadians(ship.rotationYaw));

                    nbt.setDouble("vecX", vec.xCoord);
                    nbt.setDouble("vecY", vec.yCoord);
                    nbt.setDouble("vecZ", vec.zCoord);
                    nbt.setDouble("shipX", ship.posX);
                    nbt.setDouble("shipY", ship.posY);
                    nbt.setDouble("shipZ", ship.posZ);
                    nbt.setDouble("motionX", ship.motionX);
                    nbt.setDouble("motionY", ship.motionY);
                    nbt.setDouble("motionZ", ship.motionZ);
                    player.getEntityData().setTag("parachuteInfo", nbt);
                    player.getEntityData().setBoolean("reqParachute", true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.isCanceled())
            return;
        if (event.player != null && event.player.worldObj != null && !event.player.worldObj.isRemote) {
            EntityPlayer player = event.player;
            World worldObj = player.worldObj;
            if (player.getEntityData().getBoolean("reqParachute") == true) {
                NBTTagCompound nbt = player.getEntityData().getCompoundTag("parachuteInfo");

                double vecX = nbt.getDouble("vecX");
                double vecY = nbt.getDouble("vecY");
                double vecZ = nbt.getDouble("vecZ");
                double shipX = nbt.getDouble("shipX");
                double shipY = nbt.getDouble("shipY");
                double shipZ = nbt.getDouble("shipZ");
                double motionX = nbt.getDouble("motionX");
                double motionY = nbt.getDouble("motionY");
                double motionZ = nbt.getDouble("motionZ");
                Vec3 vec = Vec3.createVectorHelper(vecX, vecY, vecZ);

                EntityParachute parachute = new EntityParachute(worldObj, player, vec, shipX, shipY, shipZ, motionX, motionY, motionZ);
                worldObj.spawnEntityInWorld(parachute);

                player.getEntityData().removeTag("parachuteInfo");
                player.getEntityData().setBoolean("reqParachute", false);
            }
        }
    }
}
