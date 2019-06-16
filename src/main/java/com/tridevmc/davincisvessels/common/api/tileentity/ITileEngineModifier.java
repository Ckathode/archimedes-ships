package com.tridevmc.davincisvessels.common.api.tileentity;

import com.tridevmc.davincisvessels.common.entity.VesselCapabilities;
import com.tridevmc.movingworld.api.IMovingTile;

public interface ITileEngineModifier extends IMovingTile {

    /**
     * Return the power of the tile that is added to the ePower variable in the vessel's datawatcher
     * used for speed calculations.
     */
    float getPowerIncrement(VesselCapabilities vesselCapabilities);

}
