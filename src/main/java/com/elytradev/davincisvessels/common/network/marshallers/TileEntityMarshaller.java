package com.elytradev.davincisvessels.common.network.marshallers;

import io.github.elytra.concrete.Marshaller;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by darkevilmac on 2/2/2017.
 */
public class TileEntityMarshaller implements Marshaller<TileEntity> {

    public static final String MARSHALLER_NAME = "io.github.elytra.davincisvessels.common.network.marshallers.TileEntityMarshaller";
    public static final TileEntityMarshaller INSTANCE = new TileEntityMarshaller();

    @Override
    public TileEntity unmarshal(ByteBuf in) {
        TileEntity tileEntity = null;

        if(in.readBoolean()){
            tileEntity = DimensionManager.getWorld(in.readInt()).getTileEntity(BlockPos.fromLong(in.readLong()));
        }

        return tileEntity;
    }

    @Override
    public void marshal(ByteBuf out, TileEntity tileEntity) {
        if (tileEntity == null || tileEntity.getWorld() == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);

            out.writeInt(tileEntity.getWorld().provider.getDimension());
            out.writeLong(tileEntity.getPos().toLong());
        }
    }
}
