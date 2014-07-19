package ckathode.archimedes.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotFuel extends Slot
{
	public SlotFuel(IInventory inventory, int id, int x, int y)
	{
		super(inventory, id, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return TileEntityFurnace.isItemFuel(itemstack);
	}
}
