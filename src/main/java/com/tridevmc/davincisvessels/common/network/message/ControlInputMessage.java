package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;

@RegisteredMessage(channel = "davincisvessels", destination = LogicalSide.SERVER)
public class ControlInputMessage extends Message {

    public EntityVessel vessel;
    public int control;

    public ControlInputMessage(EntityVessel vessel, int control) {
        super();
        this.vessel = vessel;
        this.control = control;
    }

    public ControlInputMessage() {
        super();
    }

    @Override
    public void handle(PlayerEntity sender) {
        if (vessel == null)
            return;

        vessel.getController().updateControl(vessel, sender, control);
    }
}
