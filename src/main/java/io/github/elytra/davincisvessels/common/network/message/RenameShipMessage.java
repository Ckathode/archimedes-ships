package io.github.elytra.davincisvessels.common.network.message;

import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;
import io.github.elytra.davincisvessels.common.network.marshallers.TileEntityMarshaller;
import io.github.elytra.davincisvessels.common.tileentity.TileHelm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by darkevilmac on 2/2/2017.
 */
@ReceivedOn(Side.SERVER)
public class RenameShipMessage extends Message {

    @MarshalledAs(TileEntityMarshaller.MARSHALLER_NAME)
    public TileHelm helm;
    public String newShipName;

    public RenameShipMessage(TileHelm helm, String newShipName) {
        super(DavincisVesselsNetworking.NETWORK);
        this.newShipName = newShipName;
        this.helm = helm;
    }

    public RenameShipMessage(NetworkContext ctx) {
        super(ctx);
    }

    @Override
    protected void handle(EntityPlayer sender) {
        if (helm == null)
            return;

        helm.getInfo().setName(newShipName);
    }
}
