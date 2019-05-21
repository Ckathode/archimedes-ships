package com.tridevmc.davincisvessels.common.tileentity;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.handler.ConnectionHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class TileEntitySecuredBed extends TileEntity {

    public boolean occupied;
    public boolean doMove;
    private UUID playerID;

    public TileEntitySecuredBed() {
        super(DavincisVesselsMod.CONTENT.tileTypes.get(TileEntitySecuredBed.class));
    }

    public void setPlayer(EntityPlayer player) {
        if (!world.isRemote) {
            if (player != null) {
                this.playerID = player.getGameProfile().getId();
                addToConnectionMap(playerID);
            } else {
                this.playerID = null;
            }
            doMove = false;
        }
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public void addToConnectionMap(UUID idForMap) {
        if (!world.isRemote && idForMap != null) {
            if (ConnectionHandler.playerBedMap.containsKey(idForMap)) {
                TileEntitySecuredBed prevBed = ConnectionHandler.playerBedMap.get(idForMap);
                if (!prevBed.getPos().equals(getPos()) && !(prevBed.getWorld().getDimension().getType() == getWorld().getDimension().getType())) {
                    prevBed.setPlayer(null);
                    ConnectionHandler.playerBedMap.remove(idForMap);
                }
            } else {
                ConnectionHandler.playerBedMap.put(idForMap, this);
            }
        }
    }

    public void moveBed(BlockPos newPos) {
        if (world != null && world.isRemote)
            return;

        if (playerID != null) {
            addToConnectionMap(playerID);

            if (!doMove)
                return;

            EntityPlayer player = world.getPlayerEntityByUUID(playerID);
            if (player != null) {
                player.bedLocation = pos;
                player.setSpawnPoint(newPos, true, world.getDimension().getType());
                player.setSpawnPoint(newPos, true);
                doMove = false;
            }
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        tag = super.write(tag);
        if (playerID != null)
            tag.putUniqueId("uuid", playerID);

        tag.putBoolean("doMove", doMove);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.contains("uuidMost") && tag.contains("uuidLeast"))
            playerID = tag.getUniqueId("uuid");

        doMove = tag.getBoolean("doMove");

        if (playerID != null && !ConnectionHandler.playerBedMap.containsKey(playerID) && doMove) {
            ConnectionHandler.playerBedMap.put(playerID, this);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound writtenTag = new NBTTagCompound();
        writtenTag = this.write(writtenTag);
        return writtenTag;
    }

}
