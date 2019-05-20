package com.tridevmc.davincisvessels.common.network.marshallers;

import com.tridevmc.compound.network.marshallers.Marshaller;
import com.tridevmc.compound.network.marshallers.RegisteredMarshaller;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

@RegisteredMarshaller(channel = "davincisvessels", acceptedTypes = {TileEntity.class}, ids = {"tile", "tileentity"})
public class TileEntityMarshaller extends Marshaller<TileEntity> {

    @Override
    public TileEntity readFrom(ByteBuf in) {
        TileEntity tileEntity = null;

        if (in.readBoolean()) {
            DimensionType dimension = DimensionType.getById(in.readInt());
            World world = DimensionManager.getWorld(LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER), dimension, true, true);
            tileEntity = world.getTileEntity(BlockPos.fromLong(in.readLong()));
        }

        return tileEntity;
    }

    @Override
    public void writeTo(ByteBuf out, TileEntity tileEntity) {
        if (tileEntity == null || tileEntity.getWorld() == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);

            out.writeInt(tileEntity.getWorld().getDimension().getType().getId());
            out.writeLong(tileEntity.getPos().toLong());
        }
    }
}
