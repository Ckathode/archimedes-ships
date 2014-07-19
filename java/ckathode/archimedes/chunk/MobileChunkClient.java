package ckathode.archimedes.chunk;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ckathode.archimedes.entity.EntityShip;
import ckathode.archimedes.render.MobileChunkRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MobileChunkClient extends MobileChunk
{
	private MobileChunkRenderer	renderer;
	
	public MobileChunkClient(World world, EntityShip entityship)
	{
		super(world, entityship);
		renderer = new MobileChunkRenderer(this);
	}
	
	public MobileChunkRenderer getRenderer()
	{
		return renderer;
	}
	
	@Override
	public void onChunkUnload()
	{
		List<TileEntity> iterator = new ArrayList<TileEntity>(chunkTileEntityMap.values());
		for (TileEntity te : iterator)
		{
			removeChunkBlockTileEntity(te.xCoord, te.yCoord, te.zCoord);
		}
		super.onChunkUnload();
		renderer.markRemoved();
	}
	
	@Override
	public void setChunkModified()
	{
		super.setChunkModified();
		renderer.markDirty();
	}
}
