package com.elytradev.davincisvessels.common.network.message;

import com.elytradev.davincisvessels.client.gui.ContainerHelm;
import com.elytradev.davincisvessels.common.network.DavincisVesselsNetworking;
import com.elytradev.davincisvessels.common.network.marshallers.AssembleResultMarshaller;
import com.elytradev.concrete.Message;
import com.elytradev.concrete.NetworkContext;
import com.elytradev.concrete.annotation.field.MarshalledAs;
import com.elytradev.concrete.annotation.type.ReceivedOn;
import com.elytradev.movingworld.common.chunk.assembly.AssembleResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by darkevilmac on 1/29/2017.
 */
@ReceivedOn(Side.CLIENT)
public class AssembleResultMessage extends Message {

    @MarshalledAs(AssembleResultMarshaller.MARSHALLER_NAME)
    public AssembleResult result;
    public boolean setPrevious;

    public AssembleResultMessage(AssembleResult result, boolean setPrevious) {
        super(DavincisVesselsNetworking.NETWORK);
        this.result = result;
        this.setPrevious = setPrevious;
    }

    public AssembleResultMessage(NetworkContext ctx) {
        super(ctx);
    }

    @Override
    protected void handle(EntityPlayer sender) {
        if (sender != null && sender.openContainer instanceof ContainerHelm) {
            if (setPrevious) {
                ((ContainerHelm) sender.openContainer).tileEntity.setPrevAssembleResult(result);
                ((ContainerHelm) sender.openContainer).tileEntity.getPrevAssembleResult().assemblyInteractor = result.assemblyInteractor;
            } else {
                ((ContainerHelm) sender.openContainer).tileEntity.setAssembleResult(result);
                ((ContainerHelm) sender.openContainer).tileEntity.getAssembleResult().assemblyInteractor = result.assemblyInteractor;
            }
        }
    }
}
