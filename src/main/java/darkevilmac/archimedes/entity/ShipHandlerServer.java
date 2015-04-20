package darkevilmac.archimedes.entity;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.movingworld.network.advanced.MsgChunkBlockUpdate;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import darkevilmac.movingworld.chunk.MobileChunkServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkPosition;

import java.util.Collection;

public class ShipHandlerServer extends ShipHandlerCommon {
    private boolean firstChunkUpdate;

    public ShipHandlerServer(EntityShip entityship) {
        super(entityship);
        firstChunkUpdate = true;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (ship.riddenByEntity == null) {
            player.mountEntity(ship);
            return true;
        } else if (player.ridingEntity == null) {
            return ship.getCapabilities().mountEntity(player);
        }

        return false;
    }

    @Override
    public void onChunkUpdate() {
        super.onChunkUpdate();
        Collection<ChunkPosition> list = ((MobileChunkServer) ship.getShipChunk()).getSendQueue();
        if (firstChunkUpdate) {
            ((ShipCapabilities)ship.getCapabilities()).spawnSeatEntities();
        } else {
            MsgChunkBlockUpdate msg = new MsgChunkBlockUpdate(ship, list);
            ArchimedesShipMod.instance.pipeline.sendToAllAround(msg, new TargetPoint(ship.worldObj.provider.dimensionId, ship.posX, ship.posY, ship.posZ, 64D));
        }
        list.clear();
        firstChunkUpdate = false;
    }
}
