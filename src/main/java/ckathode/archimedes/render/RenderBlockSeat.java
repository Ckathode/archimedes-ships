package ckathode.archimedes.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import ckathode.archimedes.blockitem.BlockSeat;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockSeat implements ISimpleBlockRenderingHandler
{
	
	@Override
	public void renderInventoryBlock(Block block, int meta, int modelID, RenderBlocks renderer)
	{
		Tessellator tess = Tessellator.instance;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tess.startDrawingQuads();
		tess.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 0));
		tess.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 1));
		tess.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 5));
		tess.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 2));
		tess.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 3));
		tess.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 4));
		tess.draw();
		
		renderer.setRenderBounds(0F, block.getBlockBoundsMaxY(), 0F, 1F, 1F, 0.2F);
		tess.startDrawingQuads();
		tess.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 0));
		tess.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 1));
		tess.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 5));
		tess.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 2));
		tess.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 3));
		tess.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 4));
		tess.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		
		/*
		renderer.renderAllFaces = true;
		renderer.setRenderBounds(0D, 0D, 0D, 1D, 0.5D, 1D);
		renderer.renderStandardBlock(block, 0, 0, 0);
		if (meta == 0)
		{
			renderer.setRenderBounds(0D, 0D, 0D, 0.2D, 1D, 1D);
		} else if (meta == 1)
		{	
			
		} else if (meta == 2)
		{
			renderer.setRenderBounds(0.8D, 0D, 0D, 1D, 1D, 1D);
		} else if (meta == 3)
		{	
			
		}
		//renderer.renderStandardBlock(block, x, y, z);
		renderer.renderAllFaces = false;
		*/
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		int meta = world.getBlockMetadata(x, y, z);
		renderer.setRenderBounds(0D, 0D, 0D, 1D, block.getBlockBoundsMaxY(), 1D);
		renderer.renderAllFaces = true;
		renderer.renderStandardBlock(block, x, y, z);
		if (meta == 0)
		{
			renderer.setRenderBounds(0D, block.getBlockBoundsMaxY(), 0.8D, 1D, 1D, 1D);
		} else if (meta == 1)
		{
			renderer.setRenderBounds(0D, block.getBlockBoundsMaxY(), 0D, 0.2D, 1D, 1D);
		} else if (meta == 2)
		{
			renderer.setRenderBounds(0D, block.getBlockBoundsMaxY(), 0D, 1D, 1D, 0.2D);
		} else if (meta == 3)
		{
			renderer.setRenderBounds(0.8D, block.getBlockBoundsMaxY(), 0D, 1D, 1D, 1D);
		}
		renderer.renderStandardBlock(block, x, y, z);
		renderer.renderAllFaces = false;
		return false;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelid)
	{
		return true;
	}
	
	@Override
	public int getRenderId()
	{
		return BlockSeat.seatBlockRenderID;
	}
	
}
