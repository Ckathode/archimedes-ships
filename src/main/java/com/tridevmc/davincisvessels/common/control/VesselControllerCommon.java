package com.tridevmc.davincisvessels.common.control;

import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import net.minecraft.entity.player.PlayerEntity;

public class VesselControllerCommon {
    private int vesselControl = 0;

    public void updateControl(EntityVessel vessel, PlayerEntity player, int i) {
        vesselControl = i;
    }

    public int getVesselControl() {
        return vesselControl;
    }
}
