package com.elytradev.davincisvessels.client.gui;

import com.elytradev.davincisvessels.common.tileentity.TileHelm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerHelm extends Container {
    public final TileHelm tileEntity;
    public final EntityPlayer player;

    public ContainerHelm(TileHelm te, EntityPlayer entityplayer) {
        super();
        tileEntity = te;
        player = entityplayer;

        bindPlayerInventory(entityplayer.inventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return player.world.getTileEntity(tileEntity.getPos()) == tileEntity && player.getDistanceSq(tileEntity.getPos()) < 25D;
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                int xOff = 40;
                int yOff = 90;
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18 + xOff, 84 + i * 18 + yOff));
            }
        }

        for (int i = 0; i < 9; i++) {
            int xOff = 40;
            int yOff = 90;
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18 + xOff, 142 + yOff));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = inventorySlots.get(slot);

        //null checks and checks if the item can be stacked (maxStackSize > 1)
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            //merges the item into player inventory since its in the tileEntity
            if (slot < 9) {
                if (!this.mergeItemStack(stackInSlot, 0, 35, true)) {
                    return ItemStack.EMPTY;
                }
            }
            //places it into the tileEntity is possible since its in the player inventory
            else if (!this.mergeItemStack(stackInSlot, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.getCount() == 0) {
                slotObject.putStack(ItemStack.EMPTY);
            } else {
                slotObject.onSlotChanged();
            }

            if (stackInSlot.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            slotObject.onTake(player, stackInSlot);
        }
        return stack;
    }
}
