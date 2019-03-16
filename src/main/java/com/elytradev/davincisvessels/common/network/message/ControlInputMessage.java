package com.elytradev.davincisvessels.common.network.message;

import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import net.minecraft.entity.player.EntityPlayer;
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
    public void handle(EntityPlayer sender) {
        if (ship == null)
            return;

        ship.getController().updateControl(ship, sender, control);
    }
}
