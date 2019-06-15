package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;

@RegisteredMessage(channel = "davincisvessels", destination = LogicalSide.SERVER)
public class ControlInputMessage extends Message {

    public EntityShip ship;
    public int control;

    public ControlInputMessage(EntityShip ship, int control) {
        super();
        this.ship = ship;
        this.control = control;
    }

    public ControlInputMessage() {
        super();
    }

    @Override
    public void handle(PlayerEntity sender) {
        if (ship == null)
            return;

        ship.getController().updateControl(ship, sender, control);
    }
}
