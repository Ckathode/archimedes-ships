package ckathode.archimedes.util;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class RotationHelper
{
	public static boolean rotateArchimedesBlock(Block block, World world, int x, int y, int z, ForgeDirection axis)
	{
		if (axis == ForgeDirection.UP || axis == ForgeDirection.DOWN)
		{
			int d = axis == ForgeDirection.DOWN ? -1 : 1;
			world.setBlockMetadataWithNotify(x, y, z, (world.getBlockMetadata(x, y, z) + d) & 3, 2);
		}
		return true;
	}
	
	
}
