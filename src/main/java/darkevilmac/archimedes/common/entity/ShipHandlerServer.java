package darkevilmac.archimedes.common.entity;

import darkevilmac.movingworld.common.entity.EntityMovingWorld;
import darkevilmac.movingworld.common.entity.MovingWorldHandlerServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ShipHandlerServer extends MovingWorldHandlerServer {

    private EntityMovingWorld movingWorld;
    private boolean firstChunkUpdate;

    public ShipHandlerServer(EntityShip entityship) {
        super(entityship);
        firstChunkUpdate = true;
    }

    @Override
    public EntityMovingWorld getMovingWorld() {
        return movingWorld;
    }

    @Override
    public void setMovingWorld(EntityMovingWorld movingWorld) {
        this.movingWorld = movingWorld;
    }

    @Override
    public boolean interact(EntityPlayer player, ItemStack stack, EnumHand hand) {
        if (movingWorld.getRidingEntity() == null) {
            player.startRiding(movingWorld);
            return true;
        } else {
            if (player.getRidingEntity() != null)
                player.startRiding(null);
            return movingWorld.getCapabilities().mountEntity(player);
        }
    }

    @Override
    public void onChunkUpdate() {
        super.onChunkUpdate();
        if (firstChunkUpdate) {
            ((ShipCapabilities) movingWorld.getCapabilities()).spawnSeatEntities();
            movingWorld.getDataManager().set(EntityShip.CAN_SUBMERGE, ((ShipCapabilities) movingWorld.getCapabilities()).canSubmerge() ? new Byte((byte) 1) : new Byte((byte) 0));
        }
    }
}