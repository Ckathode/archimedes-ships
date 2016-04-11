package darkevilmac.archimedes.common.handler;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.entity.EntityParachute;
import darkevilmac.archimedes.common.entity.EntitySeat;
import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.network.ConfigMessage;
import darkevilmac.archimedes.common.tileentity.TileEntitySecuredBed;
import darkevilmac.movingworld.common.util.Vec3dMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;

public class ConnectionHandler {

    public static HashMap<UUID, TileEntitySecuredBed> playerBedMap = new HashMap<UUID, TileEntitySecuredBed>();

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.isCanceled())
            return;
        if (event.player != null && event.player.worldObj != null && !event.player.worldObj.isRemote) {
            handleParachuteLogout(event);
            handleConfigDesync(event);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.isCanceled())
            return;
        if (event.player != null && event.player.worldObj != null && !event.player.worldObj.isRemote) {
            handleParachuteLogin(event);
            handleBedLogin(event);
            handlerConfigSync(event);
        }
    }

    private void handlerConfigSync(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP)
            ArchimedesShipMod.instance.network.sendTo(new ConfigMessage(ArchimedesShipMod.instance.getNetworkConfig().getShared()), (EntityPlayerMP) event.player);
    }

    private void handleConfigDesync(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP)
            ArchimedesShipMod.instance.network.sendTo(new ConfigMessage(), (EntityPlayerMP) event.player);
    }

    private void handleBedLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (playerBedMap.containsKey(event.player.getGameProfile().getId())) {
            TileEntitySecuredBed bed = playerBedMap.get(event.player.getGameProfile().getId());
            bed.playerID = event.player.getGameProfile().getId();
            bed.moveBed(bed.getPos());
        }
    }

    private void handleParachuteLogin(PlayerEvent.PlayerLoggedInEvent event) {
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
            Vec3dMod vec = new Vec3dMod(vecX, vecY, vecZ);
            Vec3dMod shipVec = new Vec3dMod(shipX, shipY, shipZ);
            Vec3dMod motionVec = new Vec3dMod(motionX, motionY, motionZ);

            EntityParachute parachute = new EntityParachute(worldObj, player, vec, shipVec, motionVec);
            worldObj.spawnEntityInWorld(parachute);

            player.getEntityData().removeTag("parachuteInfo");
            player.getEntityData().setBoolean("reqParachute", false);
        }
    }

    private void handleParachuteLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player.getRidingEntity() != null && event.player.getRidingEntity() instanceof EntitySeat) {
            EntityPlayer player = event.player;
            EntitySeat seat = (EntitySeat) player.getRidingEntity();
            EntityShip ship = seat.getParentShip();

            player.startRiding(null);
            if (ship != null && seat.getPos() != null) {
                NBTTagCompound nbt = new NBTTagCompound();

                Vec3dMod vec = new Vec3dMod(seat.getPos().getX() - ship.getMobileChunk().getCenterX(), seat.getPos().getY() - ship.getMobileChunk().minY(), seat.getPos().getZ() - ship.getMobileChunk().getCenterZ());
                vec = vec.rotateAroundY((float) Math.toRadians(ship.rotationYaw));

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
