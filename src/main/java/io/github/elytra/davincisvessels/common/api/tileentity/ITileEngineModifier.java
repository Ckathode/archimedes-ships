package io.github.elytra.davincisvessels.common.api.tileentity;

import io.github.elytra.davincisvessels.common.entity.ShipCapabilities;
import io.github.elytra.movingworld.api.IMovingWorldTileEntity;

public interface ITileEngineModifier extends IMovingWorldTileEntity {

    /**
     * Return the power of the tile that is added to the ePower variable in the ship's datawatcher
     * used for speed calculations.
     */
    float getPowerIncrement(ShipCapabilities shipCapabilities);

}
