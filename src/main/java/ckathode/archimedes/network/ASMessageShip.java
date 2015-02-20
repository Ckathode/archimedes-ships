package ckathode.archimedes.network;

import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.entity.EntityShip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public abstract class ASMessageShip extends ASMessage {
    public EntityShip ship;

    public ASMessageShip() {
        ship = null;
    }

    public ASMessageShip(EntityShip entityship) {
        ship = entityship;
    }

    /**
     * @throws IOException
     */
    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
        buf.writeInt(ship.getEntityId());
    }

    /**
     * @throws IOException
     */
    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player) throws IOException {
        int entityid = buf.readInt();
        Entity entity = player.worldObj.getEntityByID(entityid);
        if (entity instanceof EntityShip) {
            ship = (EntityShip) entity;
        } else {
            ArchimedesShipMod.modLog.warn("Unable to find ship entity for ID " + entityid);
        }
    }
}
