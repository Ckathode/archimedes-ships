package ckathode.archimedes.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockGauge implements ISimpleBlockRenderingHandler
{
	public RenderBlockGauge()
	{
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;
		int l = world.getBlockMetadata(x, y, z);
		IIcon iicon = renderer.getBlockIconFromSideAndMetadata(block, 0, l);
		int dir = l & 3;
		
		tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		double u0 = iicon.getMinU();
		double v0 = iicon.getMinV();
		double u1 = iicon.getMaxU();
		double v1 = iicon.getMaxV();
		double yoffset = 0.0625D;
		double dx = x + 1;
		double dz = z + 1;
		double dy = y + yoffset;
		
		switch (dir)
		{
		case 0:
			tessellator.addVertexWithUV(x, dy, z, u1, v1);
			tessellator.addVertexWithUV(x, dy, dz, u1, v0);
			tessellator.addVertexWithUV(dx, dy, dz, u0, v0);
			tessellator.addVertexWithUV(dx, dy, z, u0, v1);
			break;
		case 1:
			tessellator.addVertexWithUV(x, dy, z, u1, v0);
			tessellator.addVertexWithUV(x, dy, dz, u0, v0);
			tessellator.addVertexWithUV(dx, dy, dz, u0, v1);
			tessellator.addVertexWithUV(dx, dy, z, u1, v1);
			break;
		case 2:
			tessellator.addVertexWithUV(x, dy, z, u0, v0);
			tessellator.addVertexWithUV(x, dy, dz, u0, v1);
			tessellator.addVertexWithUV(dx, dy, dz, u1, v1);
			tessellator.addVertexWithUV(dx, dy, z, u1, v0);
			break;
		case 3:
		default:
			tessellator.addVertexWithUV(x, dy, z, u0, v1);
			tessellator.addVertexWithUV(x, dy, dz, u1, v1);
			tessellator.addVertexWithUV(dx, dy, dz, u1, v0);
			tessellator.addVertexWithUV(dx, dy, z, u0, v0);
		}
		return true;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return false;
	}
	
	@Override
	public int getRenderId()
	{
		return 0;
	}
	
}
