package ckathode.archimedes.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class FuelInventory implements IInventory
{
	private EntityShip	ship;
	private ItemStack[]	itemstacks;
	
	public FuelInventory(EntityShip entityship)
	{
		ship = entityship;
		itemstacks = new ItemStack[getSizeInventory()];
	}
	
	@Override
	public int getSizeInventory()
	{
		return 4;
	}
	
	@Override
	public ItemStack getStackInSlot(int i)
	{
		return i >= 0 && i < 4 ? itemstacks[i] : null;
	}
	
	@Override
	public ItemStack decrStackSize(int i, int n)
	{
		if (itemstacks[i] != null)
		{
			ItemStack itemstack;
			
			if (itemstacks[i].stackSize <= n)
			{
				itemstack = itemstacks[i];
				itemstacks[i] = null;
				markDirty();
				return itemstack;
			}
			
			itemstack = itemstacks[i].splitStack(n);
			if (itemstacks[i].stackSize <= 0)
			{
				itemstacks[i] = null;
			}
			
			markDirty();
			return itemstack;
		}
		return null;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		return getStackInSlot(i);
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack is)
	{
		if (i >= 0 && i < 4)
		{
			itemstacks[i] = is;
		}
	}
	
	@Override
	public String getInventoryName()
	{
		return "Engine Inventory";
	}
	
	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public void markDirty()
	{
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return player.ridingEntity == ship;
	}
	
	@Override
	public void openInventory()
	{
	}
	
	@Override
	public void closeInventory()
	{
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack is)
	{
		return i >= 0 && i < 4 && TileEntityFurnace.isItemFuel(is);
	}
	
}
