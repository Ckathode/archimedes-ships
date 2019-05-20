package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.LogicalSide;

/**
 * Created by darkevilmac on 2/2/2017.
 */
@RegisteredMessage(channel = "davincisvessels", destination = LogicalSide.SERVER)
public class OpenGuiMessage extends Message {

    public int guiID;

    public OpenGuiMessage(int guiID) {
        super();
        this.guiID = guiID;
    }

    public OpenGuiMessage() {
        super();
    }

    @Override
    public void handle(EntityPlayer sender) {
        sender.openGui(DavincisVesselsMod.INSTANCE, guiID, sender.world, 0, 0, 0);
    }
}
