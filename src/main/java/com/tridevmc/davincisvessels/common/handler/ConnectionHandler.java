package com.tridevmc.davincisvessels.common.handler;

import com.tridevmc.davincisvessels.common.entity.EntityParachute;
import com.tridevmc.davincisvessels.common.entity.EntitySeat;
import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import com.tridevmc.davincisvessels.common.tileentity.TileEntitySecuredBed;
import com.tridevmc.movingworld.common.util.Vec3dMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
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

            if (event.getPlayer().getRidingEntity() != null && event.getPlayer().getRidingEntity() instanceof EntityVessel
                    && !event.getPlayer().world.getServer().isSinglePlayer()) {
                ((EntityVessel) event.getPlayer().getRidingEntity()).disassemble(true);
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
        PlayerEntity player = event.getPlayer();
        World worldObj = player.world;
        if (player.getEntityData().getBoolean("reqParachute")) {
            CompoundNBT nbt = player.getEntityData().getCompound("parachuteInfo");

            double vecX = nbt.getDouble("vecX");
            double vecY = nbt.getDouble("vecY");
            double vecZ = nbt.getDouble("vecZ");
            double vesselX = nbt.getDouble("vesselX");
            double vesselY = nbt.getDouble("vesselY");
            double vesselZ = nbt.getDouble("vesselZ");
            double motionX = nbt.getDouble("motionX");
            double motionY = nbt.getDouble("motionY");
            double motionZ = nbt.getDouble("motionZ");
            Vec3dMod vec = new Vec3dMod(vecX, vecY, vecZ);
            Vec3dMod vesselVec = new Vec3dMod(vesselX, vesselY, vesselZ);
            Vec3dMod motionVec = new Vec3dMod(motionX, motionY, motionZ);

            EntityParachute parachute = new EntityParachute(worldObj, player, vec, vesselVec, motionVec);
            worldObj.addEntity(parachute);

            player.getEntityData().remove("parachuteInfo");
            player.getEntityData().putBoolean("reqParachute", false);
        }
    }

    private void handleParachuteLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer().getRidingEntity() != null && event.getPlayer().getRidingEntity() instanceof EntitySeat) {
            PlayerEntity player = event.getPlayer();
            EntitySeat seat = (EntitySeat) player.getRidingEntity();
            EntityVessel vessel = seat.getVessel();

            player.stopRiding();
            if (vessel != null && seat.getChunkPos() != null) {
                CompoundNBT nbt = new CompoundNBT();

                Vec3dMod vec = new Vec3dMod(seat.getChunkPos().getX() - vessel.getMobileChunk().getCenterX(),
                        seat.getChunkPos().getY() - vessel.getMobileChunk().minY(),
                        seat.getChunkPos().getZ() - vessel.getMobileChunk().getCenterZ());
                vec = vec.rotateAroundY((float) Math.toRadians(vessel.rotationYaw));

                nbt.putDouble("vecX", vec.x);
                nbt.putDouble("vecY", vec.y);
                nbt.putDouble("vecZ", vec.z);
                nbt.putDouble("vesselX", vessel.posX);
                nbt.putDouble("vesselY", vessel.posY);
                nbt.putDouble("vesselZ", vessel.posZ);
                nbt.putDouble("motionX", vessel.getMotion().x);
                nbt.putDouble("motionY", vessel.getMotion().y);
                nbt.putDouble("motionZ", vessel.getMotion().z);
                player.getEntityData().put("parachuteInfo", nbt);
                player.getEntityData().putBoolean("reqParachute", true);
            }
        }
    }

}
