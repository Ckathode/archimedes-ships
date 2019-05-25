package com.tridevmc.davincisvessels.common.content.item;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ItemSecuredBed extends ItemBlock {

    public ItemSecuredBed(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean placeBlock(BlockItemUseContext context, IBlockState state) {
        return context.getWorld().setBlockState(context.getPos(), state, 26);
    }

}
