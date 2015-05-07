package darkevilmac.archimedes.network;

import cpw.mods.fml.relauncher.Side;
import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.entity.EntitySeat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class RequestSetParentShipMessage extends ArchimedesShipsMessage {

    public EntitySeat seat;

    public RequestSetParentShipMessage(EntitySeat seat) {
        this.seat = seat;
    }

    public RequestSetParentShipMessage() {
        seat = null;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        buf.writeInt(seat.getEntityId());
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player, Side side) {
        int seatID = buf.readInt();

        Entity entity = player.worldObj.getEntityByID(seatID);
        if (entity != null && entity instanceof EntitySeat) {
            seat = (EntitySeat) entity;
        } else {
            ArchimedesShipMod.modLog.warn("Unable to find Seat entity with ID " + seatID);
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        if (seat != null) {
            ArchimedesShipMod.instance.network.sendTo(new SetParentShipMessage(seat.getParentShip(), seat), (EntityPlayerMP) player);
        }
    }
}
