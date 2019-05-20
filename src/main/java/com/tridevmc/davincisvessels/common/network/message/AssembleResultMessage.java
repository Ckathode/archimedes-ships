package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.davincisvessels.client.gui.ContainerHelm;
import com.tridevmc.movingworld.common.chunk.assembly.AssembleResult;
import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.LogicalSide;

/**
 * Created by darkevilmac on 1/29/2017.
 */
@RegisteredMessage(channel = "davincisvessels", destination = LogicalSide.CLIENT)
public class AssembleResultMessage extends Message {

    public AssembleResult result;
    public boolean setPrevious;

    public AssembleResultMessage(AssembleResult result, boolean setPrevious) {
        super();
        this.result = result;
        this.setPrevious = setPrevious;
    }

    public AssembleResultMessage() {
        super();
    }

    @Override
    public void handle(EntityPlayer sender) {
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
