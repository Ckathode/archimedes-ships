package com.elytradev.davincisvessels.common.api.block;

import net.minecraft.tileentity.TileEntity;

public interface IBlockBalloon {

    /**
     * How many balloon blocks is this block equivalent to?
     *
     * @param tileEntity null if not applicable.
     */
    int getBalloonWorth(TileEntity tileEntity);

}
