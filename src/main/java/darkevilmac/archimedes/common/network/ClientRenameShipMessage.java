package darkevilmac.archimedes.common.network;

import darkevilmac.archimedes.common.tileentity.TileEntityHelm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class ClientRenameShipMessage extends ArchimedesShipsMessage {
    public TileEntityHelm tileEntity;
    public String newName;
    BlockPos pos;

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
        buf.writeInt(tileEntity.getPos().getX());
        buf.writeInt(tileEntity.getPos().getY());
        buf.writeInt(tileEntity.getPos().getZ());
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        byte[] ab = new byte[buf.readShort()];
        buf.readBytes(ab);
        newName = new String(ab);
        pos = new BlockPos(buf.readInt(),
                buf.readInt(),
                buf.readInt());
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        if (player.worldObj.getTileEntity(pos) != null && player.worldObj.getTileEntity(pos) instanceof TileEntityHelm) {
            tileEntity = (TileEntityHelm) player.worldObj.getTileEntity(pos);
            tileEntity.getInfo().setName(newName);
        }
    }
}
