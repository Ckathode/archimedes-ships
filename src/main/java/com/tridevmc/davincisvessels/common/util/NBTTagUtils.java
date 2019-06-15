package com.tridevmc.davincisvessels.common.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class NBTTagUtils {

    public static void writeVec3iToNBT(CompoundNBT tag, String prefix, Vec3i vec3i) {
        if (tag == null || vec3i == null)
            return;

        tag.putInt(prefix + "VecX", vec3i.getX());
        tag.putInt(prefix + "VecY", vec3i.getY());
        tag.putInt(prefix + "VecZ", vec3i.getZ());
    }

    public static Vec3i readVec3iFromNBT(CompoundNBT tag, String prefix) {
        return new BlockPos(tag.getInt(prefix + "VecX"),
                tag.getInt(prefix + "VecY"),
                tag.getInt(prefix + "VecZ"));
    }

}
