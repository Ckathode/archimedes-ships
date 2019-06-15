package com.tridevmc.davincisvessels.common.content.item;


import com.tridevmc.davincisvessels.DavincisVesselsMod;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;

public class ItemSecuredBed extends BlockItem {

    public ItemSecuredBed() {
        super(DavincisVesselsMod.CONTENT.blockSecuredBed, new Item.Properties().group(DavincisVesselsMod.CONTENT.itemGroup).maxStackSize(1));
    }

    @Override
    public boolean placeBlock(BlockItemUseContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getPos(), state, 26);
    }

}
