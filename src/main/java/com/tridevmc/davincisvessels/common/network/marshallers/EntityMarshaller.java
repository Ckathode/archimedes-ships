package com.tridevmc.davincisvessels.common.network.marshallers;

import com.tridevmc.compound.network.marshallers.Marshaller;
import com.tridevmc.compound.network.marshallers.RegisteredMarshaller;
import com.tridevmc.movingworld.MovingWorldMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@RegisteredMarshaller(channel = "davincisvessels", acceptedTypes = {Entity.class}, ids = {"entity"})
public class EntityMarshaller extends Marshaller<Entity> {

    @Override
    public Entity readFrom(ByteBuf in) {
        if (in.readBoolean()) {
            int dimID = in.readInt();
            int entityID = in.readInt();
            World world = MovingWorldMod.PROXY.getWorld(dimID);
            return world.getEntityByID(entityID);
        } else {
            return null;
        }
    }

    @Override
    public void writeTo(ByteBuf out, Entity entity) {
        if (entity != null) {
            out.writeBoolean(true);
            out.writeInt(entity.world.getDimension().getType().getId());
            out.writeInt(entity.getEntityId());
        } else {
            out.writeBoolean(false);
        }
    }
}
