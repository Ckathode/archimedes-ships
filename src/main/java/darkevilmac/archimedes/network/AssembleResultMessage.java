package darkevilmac.archimedes.network;

import darkevilmac.archimedes.entity.ShipAssemblyInteractor;
import darkevilmac.archimedes.gui.ContainerHelm;
import darkevilmac.movingworld.chunk.AssembleResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class AssembleResultMessage extends ArchimedesShipsMessage {
    public AssembleResult result;
    public boolean prevFlag;

    public AssembleResultMessage() {
        result = null;
        prevFlag = false;
    }

    public AssembleResultMessage(AssembleResult compileResult, boolean prev) {
        result = compileResult;
        prevFlag = prev;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) {
        buf.writeBoolean(prevFlag);
        if (result == null) {
            buf.writeByte(AssembleResult.RESULT_NONE);
        } else {
            buf.writeByte(result.getCode());
            buf.writeInt(result.getBlockCount());
            buf.writeInt(result.getTileEntityCount());
            buf.writeFloat(result.getMass());
            buf.writeInt(((ShipAssemblyInteractor) result.assemblyInteractor).getBalloonCount());
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) {
        prevFlag = buf.readBoolean();
        result = new AssembleResult(buf);
        result.assemblyInteractor = new ShipAssemblyInteractor().fromByteBuf(buf);
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        if (player.openContainer instanceof ContainerHelm) {
            if (prevFlag) {
                ((ContainerHelm) player.openContainer).tileEntity.setPrevAssembleResult(result);
            } else {
                ((ContainerHelm) player.openContainer).tileEntity.setAssembleResult(result);
            }
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
    }

}

