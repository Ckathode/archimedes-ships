package ckathode.archimedes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ckathode.archimedes.blockitem.TileEntityEngine;

public class ContainerEngine extends Container
{
	public final TileEntityEngine	tileEntity;
	public final EntityPlayer		player;
	
	public ContainerEngine(TileEntityEngine tileentityengine, EntityPlayer entityplayer)
	{
		tileEntity = tileentityengine;
		player = entityplayer;
		
		addSlotToContainer(new SlotFuel(tileentityengine, 0, 26, 23));
		addSlotToContainer(new SlotFuel(tileentityengine, 1, 44, 23));
		addSlotToContainer(new SlotFuel(tileentityengine, 2, 26, 41));
		addSlotToContainer(new SlotFuel(tileentityengine, 3, 44, 41));
		
		bindPlayerInventory(entityplayer.inventory);
	}
	
	protected void bindPlayerInventory(InventoryPlayer inventoryplayer)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventoryplayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		
		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(inventoryplayer, i, 8 + i * 18, 142));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tileEntity.isUseableByPlayer(player);
	}
	
	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(par2);
		
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if (par2 < 4)
			{
				if (!this.mergeItemStack(itemstack1, 4, this.inventorySlots.size(), true))
				{
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 4, false))
			{
				return null;
			}
			
			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack) null);
			} else
			{
				slot.onSlotChanged();
			}
		}
		
		return itemstack;
	}
}
