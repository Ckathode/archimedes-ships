package com.tridevmc.davincisvessels.client.control;

import com.google.gson.Gson;
import com.tridevmc.compound.config.IConfigObjectSerializer;
import com.tridevmc.compound.config.RegisteredConfigObjectSerializer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@RegisteredConfigObjectSerializer("davincisvessels")
@OnlyIn(Dist.CLIENT)
public class KeybindSerializer implements IConfigObjectSerializer<KeyBinding> {
    @Override
    public String toString(Class aClass, KeyBinding keyBinding) {
        return new Gson().toJson(keyBinding);
    }

    @Override
    public KeyBinding fromString(Class aClass, String s) {
        return new Gson().fromJson(s, KeyBinding.class);
    }

    @Override
    public boolean accepts(Class aClass) {
        return KeyBinding.class.isAssignableFrom(aClass);
    }
}
