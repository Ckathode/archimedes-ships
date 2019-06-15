package com.tridevmc.davincisvessels.client.gui;


import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.tileentity.TileAnchorPoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAnchorPoint extends Container {
    public final TileAnchorPoint anchorPoint;
    public final PlayerEntity player;

    public ContainerAnchorPoint(int window, TileAnchorPoint anchorPoint, PlayerEntity player) {
        super(DavincisVesselsMod.CONTENT.universalContainerType, window);
        this.anchorPoint = anchorPoint;
        this.player = player;

        bindPlayerInventory(player.inventory);
        addSlot(new SlotAnchor(this.anchorPoint, 0, 32 + 16, 64 + 36));
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return anchorPoint.isUsableByPlayer(player);
    }

    protected void bindPlayerInventory(PlayerInventory inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 48 + j * 18, 138 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventoryPlayer, i, 48 + i * 18, 196));
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotNum) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotNum);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotNum < 4) {
                if (!this.mergeItemStack(itemstack1, 4, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 4, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    public class SlotAnchor extends Slot {
        public SlotAnchor(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack itemstack) {
            return TileAnchorPoint.isItemAnchor(itemstack);
        }
    }

}
