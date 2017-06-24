package com.elytradev.davincisvessels.common.tileentity;

import com.elytradev.davincisvessels.common.handler.ConnectionHandler;
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
                if (!prevBed.getPos().equals(getPos()) && !(prevBed.getWorld().provider.getDimension() == getWorld().provider.getDimension())) {
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
                player.setSpawnChunk(newPos, true, world.provider.getDimension());
                player.setSpawnPoint(newPos, true);
                doMove = false;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        if (playerID != null)
            tag.setUniqueId("uuid", playerID);

        tag.setBoolean("doMove", doMove);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("uuidMost") && tag.hasKey("uuidLeast"))
            playerID = tag.getUniqueId("uuid");

        doMove = tag.getBoolean("doMove");

        if (playerID != null && !ConnectionHandler.playerBedMap.containsKey(playerID) && doMove) {
            ConnectionHandler.playerBedMap.put(playerID, this);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound writtenTag = new NBTTagCompound();
        writtenTag = this.writeToNBT(writtenTag);
        return writtenTag;
    }

}
