package com.elytradev.davincisvessels.client.gui;

import com.elytradev.davincisvessels.common.tileentity.TileEngine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerEngine extends Container {
    public final TileEngine tileEntity;
    public final EntityPlayer player;

    public ContainerEngine(TileEngine tileentityengine, EntityPlayer entityplayer) {
        tileEntity = tileentityengine;
        player = entityplayer;

        addSlotToContainer(new SlotFuel(tileentityengine, 0, 26, 23));
        addSlotToContainer(new SlotFuel(tileentityengine, 1, 44, 23));
        addSlotToContainer(new SlotFuel(tileentityengine, 2, 26, 41));
        addSlotToContainer(new SlotFuel(tileentityengine, 3, 44, 41));

        bindPlayerInventory(entityplayer.inventory);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryplayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryplayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryplayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUsableByPlayer(player);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when
     * someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotNum) {
        ItemStack stackClone = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotNum);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            stackClone = stack.copy();

            if (slotNum < this.tileEntity.getSizeInventory()) {
                if (!this.mergeItemStack(stack, this.tileEntity.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 0, this.tileEntity.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return stackClone;
    }

    public static class SlotFuel extends Slot {
        public SlotFuel(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack itemstack) {
            return TileEntityFurnace.isItemFuel(itemstack);
        }
    }
}
