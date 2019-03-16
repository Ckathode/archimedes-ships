package com.elytradev.davincisvessels.common.tileentity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.Dimension;

public class BlockLocation {
    private final BlockPos pos;
    private final Dimension dim;

    public BlockLocation(BlockPos pos, Dimension dim) {
        this.pos = pos;
        this.dim = dim;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Dimension getDim() {
        return dim;
    }
}
