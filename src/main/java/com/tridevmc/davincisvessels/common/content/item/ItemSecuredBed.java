package com.tridevmc.davincisvessels.common.content.item;


import com.tridevmc.davincisvessels.DavincisVesselsMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ItemSecuredBed extends ItemBlock {

    public ItemSecuredBed() {
        super(DavincisVesselsMod.CONTENT.blockSecuredBed, new Item.Properties().group(DavincisVesselsMod.CONTENT.itemGroup).maxStackSize(1));
    }

    @Override
    public boolean placeBlock(BlockItemUseContext context, IBlockState state) {
        return context.getWorld().setBlockState(context.getPos(), state, 26);
    }

}
