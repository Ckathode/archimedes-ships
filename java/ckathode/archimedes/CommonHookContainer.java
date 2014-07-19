package ckathode.archimedes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import ckathode.archimedes.blockitem.TileEntityCrate;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommonHookContainer
{
	@SubscribeEvent
	public void onInteractWithEntity(EntityInteractEvent event)
	{
		if (event.entityPlayer != null)
		{
			int x = MathHelper.floor_double(event.target.posX);
			int y = MathHelper.floor_double(event.target.posY);
			int z = MathHelper.floor_double(event.target.posZ);
			
			TileEntity te = event.entity.worldObj.getTileEntity(x, y, z);
			if (te instanceof TileEntityCrate && ((TileEntityCrate) te).getContainedEntity() == event.target)
			{
				((TileEntityCrate) te).releaseEntity();
				event.setCanceled(true);
			}
		}
	}
}
