package darkevilmac.archimedes.common.tileentity;

import darkevilmac.archimedes.common.handler.ConnectionHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

import java.util.UUID;

public class TileEntitySecuredBed extends TileEntity {

    public UUID player;
    public boolean doMove;

    public TileEntitySecuredBed() {
    }

    public void setPlayer(EntityPlayer player) {
        this.player = player.getGameProfile().getId();

        if (!ConnectionHandler.playerBedMap.containsKey(player))
            ConnectionHandler.playerBedMap.remove(player);
        doMove = false;
    }

    public void moveBed(BlockPos newPos) {
        if (player != null && doMove) {
            worldObj.getPlayerEntityByUUID(player).setSpawnPoint(newPos, true);
            doMove = false;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("uuid", player.toString());
        tag.setBoolean("doMove", doMove);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("uuid"))
            player = UUID.fromString(tag.getString("uuid"));
        doMove = tag.getBoolean("doMove");

        if (player != null && !ConnectionHandler.playerBedMap.containsKey(player) && doMove) {
            ConnectionHandler.playerBedMap.put(player, this);
        }
    }

}
