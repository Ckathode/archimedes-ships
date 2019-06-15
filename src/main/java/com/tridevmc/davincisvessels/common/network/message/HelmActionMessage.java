package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import com.tridevmc.davincisvessels.common.network.HelmClientAction;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;

/**
 * Created by darkevilmac on 2/2/2017.
 */
@RegisteredMessage(channel = "davincisvessels", destination = LogicalSide.SERVER)
public class HelmActionMessage extends Message {

    public TileHelm helm;
    public HelmClientAction action;

    public HelmActionMessage() {
        super();
    }

    public HelmActionMessage(TileHelm helm, HelmClientAction action) {
        super();
        this.helm = helm;
        this.action = action;
    }

    @Override
    public void handle(PlayerEntity sender) {
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
