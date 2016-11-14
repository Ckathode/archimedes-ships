package io.github.elytra.davincisvessels.common.tileentity;

import io.github.elytra.davincisvessels.common.handler.ConnectionHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class TileEntitySecuredBed extends TileEntity implements ITickable {

    public boolean occupied;
    private UUID playerID;
    public boolean doMove;

    public TileEntitySecuredBed() {
    }

    public void setPlayer(EntityPlayer player) {
        if (!worldObj.isRemote) {
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
        if (!worldObj.isRemote && idForMap != null) {
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
        if (worldObj != null && worldObj.isRemote)
            return;

        if (playerID != null) {
            addToConnectionMap(playerID);

            if (!doMove)
                return;

            EntityPlayer player = worldObj.getPlayerEntityByUUID(playerID);
            if (player != null) {
                player.bedLocation = pos;
                player.setSpawnChunk(newPos, true, worldObj.provider.getDimension());
                player.setSpawnPoint(newPos, true);
                doMove = false;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        if (playerID != null)
            tag.setString("uuidStr", playerID.toString());

        tag.setBoolean("doMove", doMove);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("uuid"))
            playerID = UUID.fromString(tag.getString("uuidStr"));

        doMove = tag.getBoolean("doMove");

        if (playerID != null && !ConnectionHandler.playerBedMap.containsKey(playerID) && doMove) {
            ConnectionHandler.playerBedMap.put(playerID, this);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }


    @Override
    public void update() {
        // TODO: Stub for debugging, remove for builds.
    }
}
