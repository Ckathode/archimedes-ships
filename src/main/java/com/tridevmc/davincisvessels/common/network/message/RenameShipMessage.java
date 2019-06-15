package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;

/**
 * Created by darkevilmac on 2/2/2017.
 */
@RegisteredMessage(channel = "davincisvessels", destination = LogicalSide.SERVER)
public class RenameShipMessage extends Message {

    public TileHelm helm;
    public String newShipName;

    public RenameShipMessage(TileHelm helm, String newShipName) {
        super();
        this.newShipName = newShipName;
        this.helm = helm;
    }

    public RenameShipMessage() {
        super();
    }

    @Override
    public void handle(PlayerEntity sender) {
        if (helm == null)
            return;

        helm.getInfo().setName(newShipName);
    }
}
