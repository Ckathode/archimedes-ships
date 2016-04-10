package darkevilmac.archimedes.common.tileentity;

import darkevilmac.archimedes.common.handler.ConnectionHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class TileEntitySecuredBed extends TileEntity {

    public UUID playerID;
    public boolean doMove;

    public TileEntitySecuredBed() {
    }

    public void setPlayer(EntityPlayer player) {
        if (worldObj != null && worldObj.isRemote)
            return;

        if (player != null) {
            this.playerID = player.getGameProfile().getId();
            addToConnectionMap(playerID);
        } else
            this.playerID = null;

        doMove = false;
    }

    public void addToConnectionMap(UUID playerID) {
        if (worldObj != null && worldObj.isRemote)
            return;

        if (!ConnectionHandler.playerBedMap.containsKey(playerID)) {
            if ((ConnectionHandler.playerBedMap.containsKey(playerID) && ConnectionHandler.playerBedMap.get(playerID) != this)) {
                TileEntitySecuredBed prevBed = ConnectionHandler.playerBedMap.get(playerID);

                prevBed.setPlayer(null);
                ConnectionHandler.playerBedMap.remove(playerID);
            }

            ConnectionHandler.playerBedMap.put(playerID, this);
        }
    }

    public void moveBed(BlockPos newPos) {
        if (worldObj != null && worldObj.isRemote)
            return;

        if (playerID != null) {
            addToConnectionMap(playerID);

            if (!doMove)
                return;

            if (worldObj.getPlayerEntityByUUID(playerID) != null) {
                worldObj.getPlayerEntityByUUID(playerID).setSpawnChunk(newPos, true, worldObj.provider.getDimension());
                worldObj.getPlayerEntityByUUID(playerID).setSpawnPoint(newPos, true);
                doMove = false;
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (worldObj != null && worldObj.isRemote)
            return;

        if (playerID != null)
            tag.setString("uuid", playerID.toString());

        tag.setBoolean("doMove", doMove);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (worldObj != null && worldObj.isRemote)
            return;

        if (tag.hasKey("uuid"))
            playerID = UUID.fromString(tag.getString("uuid"));

        doMove = tag.getBoolean("doMove");

        if (playerID != null && !ConnectionHandler.playerBedMap.containsKey(playerID) && doMove) {
            ConnectionHandler.playerBedMap.put(playerID, this);
        }
    }
}
