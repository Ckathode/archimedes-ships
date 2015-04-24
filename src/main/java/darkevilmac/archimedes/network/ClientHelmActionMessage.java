package darkevilmac.archimedes.network;

import darkevilmac.archimedes.blockitem.TileEntityHelm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class ClientHelmActionMessage extends ArchimedesShipsMessage {
    public TileEntityHelm tileEntity;
    public int actionID;

    private int x, y, z;

    public ClientHelmActionMessage() {
        tileEntity = null;
        actionID = -1;
    }

    public ClientHelmActionMessage(TileEntityHelm tileentity, int id) {
        tileEntity = tileentity;
        actionID = id;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) {
        buf.writeByte(actionID);
        buf.writeInt(tileEntity.xCoord);
        buf.writeInt(tileEntity.yCoord);
        buf.writeInt(tileEntity.zCoord);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) {
        actionID = buf.readByte();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        if (player.worldObj.getTileEntity(x, y, z) != null && player.worldObj.getTileEntity(x, y, z) instanceof TileEntityHelm) {
            tileEntity = (TileEntityHelm) player.worldObj.getTileEntity(x, y, z);
            switch (actionID) {
                case 0:
                    tileEntity.assembleMovingWorld(player);
                    break;
                case 1:
                    tileEntity.mountMovingWorld(player, tileEntity.getMovingWorld(tileEntity.getWorldObj()));
                    break;
                case 2:
                    tileEntity.undoCompilation(player);
                    break;
                default:
                    break;
            }
        }
    }
}
