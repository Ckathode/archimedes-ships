package com.tridevmc.davincisvessels.common.handler;

import com.tridevmc.davincisvessels.common.entity.EntityParachute;
import com.tridevmc.davincisvessels.common.entity.EntitySeat;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.tileentity.TileEntitySecuredBed;
import com.tridevmc.movingworld.common.util.Vec3dMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;

public class ConnectionHandler {

    public static HashMap<UUID, TileEntitySecuredBed> playerBedMap = new HashMap<>();

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.isCanceled())
            return;
        if (event.getPlayer() != null && event.getPlayer().world != null && !event.getPlayer().world.isRemote) {
            handleParachuteLogout(event);

            if (event.getPlayer().getRidingEntity() != null && event.getPlayer().getRidingEntity() instanceof EntityShip
                    && !event.getPlayer().world.getServer().isSinglePlayer()) {
                ((EntityShip) event.getPlayer().getRidingEntity()).disassemble(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.isCanceled())
            return;
        if (event.getPlayer() != null && event.getPlayer().world != null && !event.getPlayer().world.isRemote) {
            handleParachuteLogin(event);
            handleBedLogin(event);
        }
    }

    private void handleBedLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (playerBedMap.containsKey(event.getPlayer().getGameProfile().getId())) {
            TileEntitySecuredBed bed = playerBedMap.get(event.getPlayer().getGameProfile().getId());
            bed.setPlayer(event.getPlayer());
            bed.moveBed(bed.getPos());
        }
    }

    private void handleParachuteLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.getPlayer();
        World worldObj = player.world;
        if (player.getEntityData().getBoolean("reqParachute") == true) {
            NBTTagCompound nbt = player.getEntityData().getCompound("parachuteInfo");

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
            worldObj.spawnEntity(parachute);

            player.getEntityData().remove("parachuteInfo");
            player.getEntityData().putBoolean("reqParachute", false);
        }
    }

    private void handleParachuteLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer().getRidingEntity() != null && event.getPlayer().getRidingEntity() instanceof EntitySeat) {
            EntityPlayer player = event.getPlayer();
            EntitySeat seat = (EntitySeat) player.getRidingEntity();
            EntityShip ship = seat.getShip();

            player.stopRiding();
            if (ship != null && seat.getChunkPos() != null) {
                NBTTagCompound nbt = new NBTTagCompound();

                Vec3dMod vec = new Vec3dMod(seat.getChunkPos().getX() - ship.getMobileChunk().getCenterX(),
                        seat.getChunkPos().getY() - ship.getMobileChunk().minY(),
                        seat.getChunkPos().getZ() - ship.getMobileChunk().getCenterZ());
                vec = vec.rotateAroundY((float) Math.toRadians(ship.rotationYaw));

                nbt.putDouble("vecX", vec.x);
                nbt.putDouble("vecY", vec.y);
                nbt.putDouble("vecZ", vec.z);
                nbt.putDouble("shipX", ship.posX);
                nbt.putDouble("shipY", ship.posY);
                nbt.putDouble("shipZ", ship.posZ);
                nbt.putDouble("motionX", ship.motionX);
                nbt.putDouble("motionY", ship.motionY);
                nbt.putDouble("motionZ", ship.motionZ);
                player.getEntityData().put("parachuteInfo", nbt);
                player.getEntityData().putBoolean("reqParachute", true);
            }
        }
    }

}
