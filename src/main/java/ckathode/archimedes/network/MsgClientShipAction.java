package ckathode.archimedes.network;

import ckathode.archimedes.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class MsgClientShipAction extends ASMessageShip {
    public int actionID;

    public MsgClientShipAction() {
        super();
        actionID = 0;
    }

    public MsgClientShipAction(EntityShip entityship, int id) {
        super(entityship);
        actionID = id;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
        super.encodeInto(ctx, buf);
        buf.writeByte(actionID);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) throws IOException {
        super.decodeInto(ctx, buf, player);
        actionID = buf.readByte();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        if (ship != null && ship.riddenByEntity == player) {
            switch (actionID) {
                case 1:
                    ship.alignToGrid();
                    ship.updateRiderPosition(player, ship.seatX, ship.seatY, ship.seatZ, 1);
                    ship.disassemble(false);
                    break;
                case 2:
                    ship.alignToGrid();
                    ship.updateRiderPosition(player, ship.seatX, ship.seatY, ship.seatZ, 1);
                    ship.disassemble(true);
                    break;
                case 3:
                    ship.alignToGrid();
                    break;
                default:
                    break;
            }
        }
    }
}
