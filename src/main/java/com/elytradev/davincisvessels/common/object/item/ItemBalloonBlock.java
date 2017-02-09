package com.elytradev.davincisvessels.common.object.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;

public class ItemBalloonBlock extends ItemCloth {
    public ItemBalloonBlock(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        EnumDyeColor colour = EnumDyeColor.byMetadata(stack.getMetadata());
        return super.getUnlocalizedName() + "." + colour.toString();
    }

}
