package darkevilmac.archimedes.common.network;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;


public abstract class ShipMessage extends ArchimedesShipsMessage {

    public EntityShip ship;

    public ShipMessage() {
        ship = null;
    }

    public ShipMessage(EntityShip ship) {
        this.ship = ship;
    }


    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        buf.writeInt(ship.getEntityId());
        buf.writeInt(ship.worldObj.provider.getDimension());
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        int entityID = buf.readInt();
        int dimID = buf.readInt();
        World theWorld = DimensionManager.getWorld(dimID);
        if (theWorld == null) {
            ArchimedesShipMod.instance.modLog.warn("Unable to find dimension with ID " + dimID);
            return;
        }

        Entity entity = theWorld.getEntityByID(entityID);
        if (entity instanceof EntityShip) {
            ship = (EntityShip) entity;
        } else {
            ArchimedesShipMod.modLog.warn("Unable to find Ship entity with ID " + entityID);
        }
    }
}
