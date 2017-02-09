package com.elytradev.davincisvessels.common.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class NBTTagUtils {

    public static void writeVec3iToNBT(NBTTagCompound tag, String prefix, Vec3i vec3i) {
        if (tag == null || vec3i == null)
            return;

        tag.setInteger(prefix + "VecX", vec3i.getX());
        tag.setInteger(prefix + "VecY", vec3i.getY());
        tag.setInteger(prefix + "VecZ", vec3i.getZ());
    }

    public static Vec3i readVec3iFromNBT(NBTTagCompound tag, String prefix) {
        return new BlockPos(tag.getInteger(prefix + "VecX"),
                tag.getInteger(prefix + "VecY"),
                tag.getInteger(prefix + "VecZ"));
    }


}
