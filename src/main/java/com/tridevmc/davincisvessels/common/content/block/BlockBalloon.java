package com.tridevmc.davincisvessels.common.content.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;

public class BlockBalloon extends Block {
    public BlockBalloon(DyeColor colour) {
        super(Block.Properties.create(Material.WOOL, colour)
                .sound(SoundType.CLOTH)
                .hardnessAndResistance(0.35F, 1F));
    }
}
