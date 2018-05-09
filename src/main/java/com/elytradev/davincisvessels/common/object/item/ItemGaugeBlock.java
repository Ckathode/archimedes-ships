package com.elytradev.davincisvessels.common.object.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemGaugeBlock extends ItemBlock {
    public ItemGaugeBlock(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world
     * when this Item is placed as a Block (mostly used with ItemBlocks).
     */
    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + (stack.getMetadata() != 0 ? "_ext" : "");
    }
}

