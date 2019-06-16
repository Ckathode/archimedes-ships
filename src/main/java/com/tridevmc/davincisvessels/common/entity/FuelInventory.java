package com.tridevmc.davincisvessels.common.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.FurnaceTileEntity;

public class FuelInventory implements IInventory {

    private EntityVessel vessel;
    private ItemStack[] contents;

    public FuelInventory(EntityVessel entityvessel) {
        vessel = entityvessel;
        contents = new ItemStack[getSizeInventory()];
    }

    @Override
    public int getSizeInventory() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.contents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return i >= 0 && i < 4 ? contents[i] : null;
    }

    @Override
    public ItemStack decrStackSize(int i, int n) {
        if (contents[i] != null) {
            ItemStack itemstack;

            if (contents[i].getCount() <= n) {
                itemstack = contents[i];
                contents[i] = null;
                markDirty();
                return itemstack;
            }

            itemstack = contents[i].split(n);
            if (contents[i].getCount() <= 0) {
                contents[i] = null;
            }

            markDirty();
            return itemstack;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        ItemStack content = contents[i].copy();
        contents[i] = null;

        return content;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack is) {
        if (i >= 0 && i < 4) {
            contents[i] = is;
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return player.getRidingEntity() == vessel;
    }

    @Override
    public void openInventory(PlayerEntity player) {
    }

    @Override
    public void closeInventory(PlayerEntity playe) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack is) {
        return i >= 0 && i < 4 && FurnaceTileEntity.func_213991_b(is);
    }

    @Override
    public void clear() {
    }

}
