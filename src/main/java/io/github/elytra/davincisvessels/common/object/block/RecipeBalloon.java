package io.github.elytra.davincisvessels.common.object.block;

import com.google.common.collect.Lists;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import io.github.elytra.davincisvessels.common.object.DavincisVesselsObjects;

public class RecipeBalloon implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                ItemStack itemstack = inventorycrafting.getStackInRowAndColumn(i, j);
                if (itemstack == null) continue;
                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.WOOL)) {
                    ItemStack itemStack1 = inventorycrafting.getStackInRowAndColumn(i, j + 1);
                    return itemStack1 != null && itemStack1.getItem() == Items.STRING;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                ItemStack itemstack = inventorycrafting.getStackInRowAndColumn(i, j);
                if (itemstack == null) continue;
                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.WOOL)) {
                    ItemStack itemStack1 = inventorycrafting.getStackInRowAndColumn(i, j + 1);
                    if (itemStack1 != null && itemStack1.getItem() == Items.STRING) {
                        return new ItemStack(DavincisVesselsObjects.blockBalloon, 1, itemstack.getItemDamage());
                    }
                    return null;
                }
                return null;
            }
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(DavincisVesselsObjects.blockBalloon);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventoryCrafting) {
        return NonNullList.create();
    }

}
