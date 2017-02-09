package io.github.elytra.davincisvessels.common.network.message;

import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import io.github.elytra.davincisvessels.client.gui.ContainerHelm;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;
import io.github.elytra.davincisvessels.common.network.marshallers.AssembleResultMarshaller;
import io.github.elytra.movingworld.common.chunk.assembly.AssembleResult;
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
