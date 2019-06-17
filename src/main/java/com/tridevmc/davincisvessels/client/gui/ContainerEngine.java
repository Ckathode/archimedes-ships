package com.tridevmc.davincisvessels.client.gui;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.tileentity.TileEngine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.FurnaceTileEntity;

public class ContainerEngine extends Container {
    public final TileEngine engine;
    public final PlayerEntity player;

    public ContainerEngine(int window, TileEngine engine, PlayerEntity player) {
        super(DavincisVesselsMod.CONTENT.universalContainerType, window);
        this.engine = engine;
        this.player = player;

        addSlot(new SlotFuel(engine, 0, 26, 23));
        addSlot(new SlotFuel(engine, 1, 44, 23));
        addSlot(new SlotFuel(engine, 2, 26, 41));
        addSlot(new SlotFuel(engine, 3, 44, 41));

        bindPlayerInventory(player.inventory);
    }

    protected void bindPlayerInventory(PlayerInventory inventoryplayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventoryplayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventoryplayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return engine.isUsableByPlayer(player);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when
     * someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(PlayerEntity par1EntityPlayer, int slotNum) {
        ItemStack stackClone = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotNum);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            stackClone = stack.copy();

            if (slotNum < this.engine.getSizeInventory()) {
                if (!this.mergeItemStack(stack, this.engine.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 0, this.engine.getSizeInventory(), false)) {
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
            return FurnaceTileEntity.isFuel(itemstack);
        }
    }
}
