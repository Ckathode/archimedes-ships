package ckathode.archimedes.blockitem;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import ckathode.archimedes.ArchimedesShipMod;

public class RecipeBalloon implements IRecipe
{
	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				ItemStack itemstack = inventorycrafting.getStackInRowAndColumn(i, j);
				if (itemstack == null) continue;
				if (itemstack.getItem() == Item.getItemFromBlock(Blocks.wool))
				{
					ItemStack itemstack1 = inventorycrafting.getStackInRowAndColumn(i, j + 1);
					if (itemstack1 != null && itemstack1.getItem() == Items.string)
					{
						return true;
					}
					return false;
				}
				return false;
			}
		}
		return false;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				ItemStack itemstack = inventorycrafting.getStackInRowAndColumn(i, j);
				if (itemstack == null) continue;
				if (itemstack.getItem() == Item.getItemFromBlock(Blocks.wool))
				{
					ItemStack itemstack1 = inventorycrafting.getStackInRowAndColumn(i, j + 1);
					if (itemstack1 != null && itemstack1.getItem() == Items.string)
					{
						return new ItemStack(ArchimedesShipMod.blockBalloon, 1, itemstack.getItemDamage());
					}
					return null;
				}
				return null;
			}
		}
		return null;
	}
	
	@Override
	public int getRecipeSize()
	{
		return 2;
	}
	
	@Override
	public ItemStack getRecipeOutput()
	{
		return new ItemStack(ArchimedesShipMod.blockBalloon);
	}
	
}
