package com.tridevmc.davincisvessels.common.content.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockAS extends Block {
    public BlockAS(Material material, SoundType soundType) {
        this(material, soundType, 1F, 1F);
    }

    public BlockAS(Material material, SoundType soundType, float hardness, float resistance) {
        super(Block.Properties.create(material).sound(soundType).hardnessAndResistance(hardness, resistance));
    }
}
