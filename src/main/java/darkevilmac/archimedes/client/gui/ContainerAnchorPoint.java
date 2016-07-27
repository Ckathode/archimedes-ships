package darkevilmac.archimedes.client.gui;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import darkevilmac.archimedes.common.tileentity.TileEntityAnchorPoint;

public class ContainerAnchorPoint extends Container {
    public final TileEntityAnchorPoint tileEntity;
    public final EntityPlayer player;

    public ContainerAnchorPoint(TileEntityAnchorPoint te, EntityPlayer entityplayer) {
        super();
        tileEntity = te;
        player = entityplayer;

        bindPlayerInventory(entityplayer.inventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 48 + j * 18, 124 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 48 + i * 18, 182));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNum) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(slotNum);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotNum < 4) {
                if (!this.mergeItemStack(itemstack1, 4, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 4, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
