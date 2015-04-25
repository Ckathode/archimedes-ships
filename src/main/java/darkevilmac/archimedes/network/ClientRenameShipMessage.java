package darkevilmac.archimedes.network;

import cpw.mods.fml.relauncher.Side;
import darkevilmac.archimedes.blockitem.TileEntityHelm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class ClientRenameShipMessage extends ArchimedesShipsMessage {
    public TileEntityHelm tileEntity;
    public String newName;
    int x, y, z;

    public ClientRenameShipMessage() {
        tileEntity = null;
        newName = "";
    }

    public ClientRenameShipMessage(TileEntityHelm te, String name) {
        tileEntity = te;
        newName = name;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        buf.writeShort(newName.length());
        buf.writeBytes(newName.getBytes());
        buf.writeInt(tileEntity.xCoord);
        buf.writeInt(tileEntity.yCoord);
        buf.writeInt(tileEntity.zCoord);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player, Side side) {
        byte[] ab = new byte[buf.readShort()];
        buf.readBytes(ab);
        newName = new String(ab);
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
            tileEntity.getInfo().setName(newName);
        }
    }
}
