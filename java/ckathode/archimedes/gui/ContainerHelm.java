package ckathode.archimedes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ckathode.archimedes.blockitem.TileEntityHelm;

public class ContainerHelm extends Container
{
	public final TileEntityHelm	tileEntity;
	public final EntityPlayer	player;
	
	public ContainerHelm(TileEntityHelm te, EntityPlayer entityplayer)
	{
		super();
		tileEntity = te;
		player = entityplayer;
		
		/*for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				addSlotToContainer(new Slot(tileEntity, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}*/
		
		//bindPlayerInventory(entityplayer.inventory);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.worldObj.getTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) == tileEntity && player.getDistanceSq(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) < 25D;
	}
	
	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		
		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		
		//null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack())
		{
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			
			//merges the item into player inventory since its in the tileEntity
			if (slot < 9)
			{
				if (!this.mergeItemStack(stackInSlot, 0, 35, true))
				{
					return null;
				}
			}
			//places it into the tileEntity is possible since its in the player inventory
			else if (!this.mergeItemStack(stackInSlot, 0, 9, false))
			{
				return null;
			}
			
			if (stackInSlot.stackSize == 0)
			{
				slotObject.putStack(null);
			} else
			{
				slotObject.onSlotChanged();
			}
			
			if (stackInSlot.stackSize == stack.stackSize)
			{
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}
}
