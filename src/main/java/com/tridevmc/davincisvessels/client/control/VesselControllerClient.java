package com.tridevmc.davincisvessels.client.control;

import com.tridevmc.davincisvessels.common.control.VesselControllerCommon;
import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import com.tridevmc.davincisvessels.common.network.message.ControlInputMessage;
import net.minecraft.entity.player.PlayerEntity;

public class VesselControllerClient extends VesselControllerCommon {
    @Override
    public void updateControl(EntityVessel vessel, PlayerEntity player, int control) {
        super.updateControl(vessel, player, control);
        new ControlInputMessage(vessel, control).sendToServer();
    }
}
