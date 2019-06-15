package com.tridevmc.davincisvessels.common.tileentity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class BlockLocation {
    private final BlockPos pos;
    private final DimensionType dim;

    public BlockLocation(BlockPos pos, DimensionType dim) {
        this.pos = pos;
        this.dim = dim;
    }

    public BlockPos getPos() {
        return pos;
    }

    public DimensionType getDim() {
        return dim;
    }
}
