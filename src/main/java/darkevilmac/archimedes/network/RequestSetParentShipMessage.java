package darkevilmac.archimedes.network;

import cpw.mods.fml.relauncher.Side;
import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.entity.EntitySeat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;

public class RequestSetParentShipMessage extends ArchimedesShipsMessage {

    public EntitySeat seat;
    private int dimID;
    private int seatID;

    public RequestSetParentShipMessage(EntitySeat seat, int dimID) {
        this.seatID = seat.getEntityId();
        this.dimID = dimID;
        this.seat = seat;
    }

    public RequestSetParentShipMessage() {
        seat = null;
        dimID = -10000;
        seatID = -0;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        buf.writeInt(seat.getEntityId());
        buf.writeInt(dimID);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player, Side side) {
        seatID = buf.readInt();
        dimID = buf.readInt();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        System.out.println("HANDLESERVERSIDEREQUEST");
        if (seatID != -0 && dimID != -10000)
            seat = (EntitySeat) DimensionManager.getWorld(dimID).getEntityByID(seatID);
        if (seat != null) {
            ArchimedesShipMod.instance.network.sendTo(new SetParentShipMessage(seat.getParentShip(), seat), (EntityPlayerMP) player);
        }
    }
}
