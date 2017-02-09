package io.github.elytra.davincisvessels.common.network.message;

import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import io.github.elytra.davincisvessels.common.entity.EntityShip;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;
import io.github.elytra.movingworld.common.network.marshallers.EntityMarshaller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by darkevilmac on 2/2/2017.
 */
@ReceivedOn(Side.SERVER)
public class ControlInputMessage extends Message {

    @MarshalledAs(EntityMarshaller.MARSHALLER_NAME)
    public EntityShip ship;

    public ControlInputMessage(EntityShip ship, byte control) {
        super(DavincisVesselsNetworking.NETWORK);
        this.ship = ship;
        this.control = control;
    }

    @MarshalledAs("byte")
    public byte control;

    public ControlInputMessage(NetworkContext ctx) {
        super(ctx);
    }

    @Override
    protected void handle(EntityPlayer sender) {
        if (ship == null)
            return;

        ship.getController().updateControl(ship, sender, control);
    }
}
