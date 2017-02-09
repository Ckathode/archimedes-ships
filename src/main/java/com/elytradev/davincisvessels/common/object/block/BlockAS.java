package com.elytradev.davincisvessels.common.object.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockAS extends Block {
    public BlockAS(Material material, SoundType soundType) {
        super(material);
        this.setSoundType(soundType);
    }
}
