package ckathode.archimedes.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

public class TileEntityHelmRenderer extends TileEntitySpecialRenderer
{
	public static final ResourceLocation	MODEL_HELM		= new ResourceLocation("archimedes", "/model/shipHelm.obj");
	public static final ResourceLocation	TEXTURE_HELM	= new ResourceLocation("archimedes", "/model/wood.png");
	private IModelCustom					modelHelm;
	
	public TileEntityHelmRenderer()
	{
		modelHelm = AdvancedModelLoader.loadModel(MODEL_HELM);
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float rendertime)
	{
		bindTexture(TEXTURE_HELM);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		GL11.glRotatef(90F * te.blockMetadata, 0F, 1F, 0F);
		modelHelm.renderAll();
		GL11.glPopMatrix();
	}
	
}
