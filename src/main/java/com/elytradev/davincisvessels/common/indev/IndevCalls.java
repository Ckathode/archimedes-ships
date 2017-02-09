package com.elytradev.davincisvessels.common.indev;

import net.minecraft.launchwrapper.Launch;

/**
 * Just links to common things I call when in development. Prevents me from releasing a version that
 * spams the console by accident.
 */

public class IndevCalls {

    public static void println(String str) {
        if (Launch.blackboard.get("fml.deobfuscatedEnvironment") != null) {
            System.out.println(str);
        }
    }

    public static void print(String str) {
        if (Launch.blackboard.get("fml.deobfuscatedEnvironment") != null) {
            System.out.print(str);
        }
    }
}
