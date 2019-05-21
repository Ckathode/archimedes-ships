package com.tridevmc.davincisvessels.client.control;

import com.google.common.collect.Lists;
import com.tridevmc.compound.config.ConfigType;
import com.tridevmc.compound.config.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@ConfigType(ModConfig.Type.CLIENT)
public class DavincisKeybinds {

    @ConfigValue(comment = "Keybindings should be managed in game.")
    public KeyBinding kbUp, kbDown, kbBrake, kbAlign, kbDisassemble, kbShipInv;

    private void loadKeybindings() {
        kbUp = new KeyBinding("key.davincis.up", 0x2D, "Davincis Vessels");
        kbDown = new KeyBinding("key.davincis.down", 0x2C, "Davincis Vessels");
        kbBrake = new KeyBinding("key.davincis.brake", 0x2E, "Davincis Vessels");
        kbAlign = new KeyBinding("key.davincis.align", 0x0D, "Davincis Vessels");
        kbDisassemble = new KeyBinding("key.davincis.decompile", 0x2B, "Davincis Vessels");
        kbShipInv = new KeyBinding("key.davincis.shipinv", 0x25, "Davincis Vessels");
    }

    public void addToControlsMenu() {
        Minecraft mc = Minecraft.getInstance();
        List<KeyBinding> keyBindings = Arrays.asList(mc.gameSettings.keyBindings);
        keyBindings.removeAll(Lists.newArrayList(kbUp, kbDown, kbBrake, kbAlign, kbDisassemble, kbShipInv));
        keyBindings.addAll(Lists.newArrayList(kbUp, kbDown, kbBrake, kbAlign, kbDisassemble, kbShipInv));
        KeyBinding[] binds = new KeyBinding[keyBindings.size()];
        mc.gameSettings.keyBindings = keyBindings.toArray(binds);
    }

}
