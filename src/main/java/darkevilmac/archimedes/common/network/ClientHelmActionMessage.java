package darkevilmac.archimedes.common.network;

import darkevilmac.archimedes.common.tileentity.TileEntityHelm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class ClientHelmActionMessage extends ArchimedesShipsMessage {
    public TileEntityHelm tileEntity;
    public int actionID;

    private BlockPos pos;

    public ClientHelmActionMessage() {
        tileEntity = null;
        actionID = -1;
    }

    public ClientHelmActionMessage(TileEntityHelm tileentity, int id) {
        tileEntity = tileentity;
        actionID = id;
    }

    @Override
    public boolean onMainThread() {
        return true;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        buf.writeByte(actionID);
        buf.writeInt(tileEntity.getPos().getX());
        buf.writeInt(tileEntity.getPos().getY());
        buf.writeInt(tileEntity.getPos().getZ());
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        actionID = buf.readByte();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        if (player.worldObj.getTileEntity(pos) != null && player.worldObj.getTileEntity(pos) instanceof TileEntityHelm) {
            tileEntity = (TileEntityHelm) player.worldObj.getTileEntity(pos);
            switch (actionID) {
                case 0:
                    tileEntity.assembleMovingWorld(player);
                    break;
                case 1:
                    tileEntity.mountMovingWorld(player, tileEntity.getMovingWorld(tileEntity.getWorld()));
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
