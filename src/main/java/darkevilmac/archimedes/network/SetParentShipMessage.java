package darkevilmac.archimedes.network;

import cpw.mods.fml.relauncher.Side;
import darkevilmac.archimedes.entity.EntitySeat;
import darkevilmac.archimedes.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public class SetParentShipMessage extends ShipMessage {

    public int seatID;
    public int chunkPosX, chunkPosY, chunkPosZ;

    public SetParentShipMessage() {
        super();
        seatID = -0;
        chunkPosX = 0;
        chunkPosY = 0;
        chunkPosZ = 0;
    }

    public SetParentShipMessage(EntityShip ship, EntitySeat seat) {
        super(ship);
        seatID = seat.getEntityId();
        chunkPosX = seat.getChunkPosition().chunkPosX;
        chunkPosY = seat.getChunkPosition().chunkPosY;
        chunkPosZ = seat.getChunkPosition().chunkPosZ;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        super.encodeInto(ctx, buf, side);
        buf.writeInt(seatID);
        buf.writeInt(chunkPosX);
        buf.writeInt(chunkPosY);
        buf.writeInt(chunkPosZ);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player, Side side) {
        super.decodeInto(ctx, buf, player, side);
        seatID = buf.readInt();
        chunkPosX = buf.readInt();
        chunkPosY = buf.readInt();
        chunkPosZ = buf.readInt();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        if (!player.worldObj.isRemote)
            return;

        System.out.println(seatID + " " + chunkPosX + " " + chunkPosY + " " + chunkPosZ + " " + player.worldObj.isRemote);
        World world = player.worldObj;
        EntitySeat seat = (EntitySeat) world.getEntityByID(seatID);
        seat.setParentShip(ship, chunkPosX, chunkPosY, chunkPosZ);
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
