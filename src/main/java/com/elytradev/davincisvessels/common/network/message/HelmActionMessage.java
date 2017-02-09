package com.elytradev.davincisvessels.common.network.message;

import com.elytradev.davincisvessels.common.network.marshallers.TileEntityMarshaller;
import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import com.elytradev.davincisvessels.common.network.DavincisVesselsNetworking;
import com.elytradev.davincisvessels.common.network.HelmClientAction;
import com.elytradev.davincisvessels.common.tileentity.TileHelm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by darkevilmac on 2/2/2017.
 */
@ReceivedOn(Side.SERVER)
public class HelmActionMessage extends Message {

    @MarshalledAs(TileEntityMarshaller.MARSHALLER_NAME)
    public TileHelm helm;
    public HelmClientAction action;

    public HelmActionMessage(NetworkContext ctx) {
        super(ctx);
    }

    public HelmActionMessage(TileHelm helm, HelmClientAction action) {
        super(DavincisVesselsNetworking.NETWORK);
        this.helm = helm;
        this.action = action;
    }

    @Override
    protected void handle(EntityPlayer sender) {
        if (helm == null)
            return;

        switch (action) {
            case ASSEMBLE:
                helm.assembleMovingWorld(sender);
                break;
            case MOUNT:
                helm.mountMovingWorld(sender, helm.getMovingWorld(helm.getWorld()));
                break;
            case UNDOCOMPILE:
                helm.undoCompilation(sender);
                break;
            default:
                break;
        }
    }
}
