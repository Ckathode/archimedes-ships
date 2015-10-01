package darkevilmac.archimedes.common.tileentity;

import darkevilmac.archimedes.common.handler.ConnectionHandler;
import darkevilmac.archimedes.common.util.NBTTagUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

import java.util.UUID;

public class TileEntitySecuredBed extends TileEntity {

    public UUID playerID;
    public boolean doMove;

    public BlockPos previousPosition;

    public TileEntitySecuredBed() {
    }

    public void setPlayer(EntityPlayer player) {
        if (player != null) {
            this.playerID = player.getGameProfile().getId();
            addToConnectionMap(playerID);
        } else
            this.playerID = null;

        doMove = false;
    }

    private void addToConnectionMap(UUID playerID) {
        if (!ConnectionHandler.playerBedMap.containsKey(playerID)) {
            if ((ConnectionHandler.playerBedMap.containsKey(playerID) && ConnectionHandler.playerBedMap.get(playerID) != this)) {
                EntityPlayer player = worldObj.getPlayerEntityByUUID(playerID);
                TileEntitySecuredBed prevBed = ConnectionHandler.playerBedMap.get(playerID);

                prevBed.setPlayer(null);
                ConnectionHandler.playerBedMap.remove(playerID);
            }

            ConnectionHandler.playerBedMap.put(playerID, this);
        }
    }

    public void moveBed(BlockPos newPos) {
        if (playerID != null && doMove) {
            if (worldObj.getPlayerEntityByUUID(playerID) != null) {
                worldObj.getPlayerEntityByUUID(playerID).setSpawnPoint(newPos, true);
                doMove = false;
            } else {
                doMove = true;
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if (playerID != null)
            tag.setString("uuid", playerID.toString());

        tag.setBoolean("doMove", doMove);
        NBTTagUtils.writeVec3iToNBT(tag, "previousPosition", previousPosition);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("uuid"))
            playerID = UUID.fromString(tag.getString("uuid"));

        doMove = tag.getBoolean("doMove");

        if (playerID != null && !ConnectionHandler.playerBedMap.containsKey(playerID) && doMove) {
            ConnectionHandler.playerBedMap.put(playerID, this);
        }
        previousPosition = new BlockPos(NBTTagUtils.readVec3iFromNBT(tag, "previousPosition"));
    }
}
