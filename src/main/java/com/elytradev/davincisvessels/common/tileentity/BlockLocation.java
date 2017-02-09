package com.elytradev.davincisvessels.common.tileentity;

import net.minecraft.util.math.BlockPos;

public class BlockLocation {
    public BlockPos pos;
    public int dimID;

    public BlockLocation(BlockPos pos, int dimID) {
        this.pos = pos;
        this.dimID = dimID;
    }
}
