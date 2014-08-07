package ckathode.archimedes.blockitem;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;

public class ItemGaugeBlock extends ItemBlock
{
	public ItemGaugeBlock(Block block)
	{
		super(block);
		setHasSubtypes(true);
	}
	
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return field_150939_a.getIcon(0, damage << 2);
	}
}
