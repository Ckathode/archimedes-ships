package darkevilmac.archimedes.network;

import cpw.mods.fml.relauncher.Side;
import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.entity.EntitySeat;
import darkevilmac.archimedes.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;


public class SetParentShipMessage extends ShipMessage {

    public EntitySeat seat;
    public int chunkPosX, chunkPosY, chunkPosZ;

    public SetParentShipMessage() {
        super(null);
        seat = null;
        chunkPosX = 0;
        chunkPosY = 0;
        chunkPosZ = 0;
    }

    public SetParentShipMessage(EntityShip ship, EntitySeat seat) {
        super(ship);
        this.seat = seat;
        chunkPosX = seat.getChunkPosition().chunkPosX;
        chunkPosY = seat.getChunkPosition().chunkPosY;
        chunkPosZ = seat.getChunkPosition().chunkPosZ;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        super.encodeInto(ctx, buf, side);
        buf.writeInt(seat.getEntityId());
        buf.writeInt(chunkPosX);
        buf.writeInt(chunkPosY);
        buf.writeInt(chunkPosZ);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player, Side side) {
        super.decodeInto(ctx, buf, player, side);

        int seatID = buf.readInt();
        chunkPosX = buf.readInt();
        chunkPosY = buf.readInt();
        chunkPosZ = buf.readInt();

        Entity entity = player.worldObj.getEntityByID(seatID);
        if (entity instanceof EntitySeat) {
            seat = (EntitySeat) entity;
        } else {
            ArchimedesShipMod.modLog.warn("Unable to find Seat entity with ID " + seatID);
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        if (!player.worldObj.isRemote)
            return;

        seat.setParentShip(ship, chunkPosX, chunkPosY, chunkPosZ);
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
