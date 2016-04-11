package darkevilmac.archimedes.common.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3i;

public class NBTTagUtils {

    public static void writeVec3iToNBT(NBTTagCompound tag, String prefix, Vec3i vec3i) {
        if (tag == null || vec3i == null)
            return;

        tag.setInteger(prefix + "vecX", vec3i.getX());
        tag.setInteger(prefix + "vecY", vec3i.getY());
        tag.setInteger(prefix + "vecZ", vec3i.getZ());
    }

    public static Vec3i readVec3iFromNBT(NBTTagCompound nbtTagCompound, String prefix) {
        return new Vec3i(nbtTagCompound.getInteger(prefix + "vecX"),
                nbtTagCompound.getInteger(prefix + "vecY"),
                nbtTagCompound.getInteger(prefix + "vecZ"));
    }


}
