package io.github.elytra.davincisvessels.common.network.message;

import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.common.entity.EntityShip;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;
import io.github.elytra.davincisvessels.common.object.DavincisVesselsObjects;
import io.github.elytra.movingworld.common.network.marshallers.EntityMarshaller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by darkevilmac on 1/29/2017.
 */
@ReceivedOn(Side.SERVER)
public class RequestSubmerseMessage extends Message {

    @MarshalledAs(EntityMarshaller.MARSHALLER_NAME)
    public EntityShip ship;
    public boolean doSumberse;

    public RequestSubmerseMessage(EntityShip ship, boolean doSumberse) {
        super(DavincisVesselsNetworking.NETWORK);
        this.ship = ship;
        this.doSumberse = doSumberse;
    }

    public RequestSubmerseMessage(NetworkContext ctx) {
        super(ctx);
    }

    @Override
    protected void handle(EntityPlayer sender) {
        if (ship != null) {
            if (doSumberse && !ship.canSubmerge()) {
                if (sender != null && sender instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) sender).connection.disconnect("Invalid submerse request!" +
                            "\nCheating to go underwater... reconsider your life choices.");
                    if (sender != null && sender.getGameProfile() != null)
                        DavincisVesselsMod.LOG.warn("A user tried to submerse in a vessel that can't, user info: " + sender.getGameProfile().toString());
                }
                return;
            }

            ship.setSubmerge(doSumberse);
            sender.addStat(DavincisVesselsObjects.achievementSubmerseShip);
        }
    }
}
