package com.tridevmc.davincisvessels.common.content.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;

public class BlockBalloon extends Block {
    public BlockBalloon(EnumDyeColor colour) {
        super(Block.Properties.create(Material.CLOTH, colour)
                .sound(SoundType.CLOTH)
                .hardnessAndResistance(0.35F, 1F));
    }
}
