package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;

/**
 * Created by darkevilmac on 2/2/2017.
 */
@RegisteredMessage(channel = "davincisvessels", destination = LogicalSide.SERVER)
public class RenameVesselMessage extends Message {

    public TileHelm helm;
    public String newVesselName;

    public RenameVesselMessage(TileHelm helm, String newVesselName) {
        super();
        this.newVesselName = newVesselName;
        this.helm = helm;
    }

    public RenameVesselMessage() {
        super();
    }

    @Override
    public void handle(PlayerEntity sender) {
        if (helm == null)
            return;

        helm.getInfo().setName(newVesselName);
    }
}
