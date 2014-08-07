package ckathode.archimedes.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import ckathode.archimedes.entity.EntityParachute;

public class RenderParachute extends Render
{
	public static final ResourceLocation	PARACHUTE_TEXTURE	= new ResourceLocation("archimedes", "textures/entity/parachute.png");
	
	public ModelParachute					model;
	
	public RenderParachute()
	{
		model = new ModelParachute();
	}
	
	public void renderParachute(EntityParachute entity, double x, double y, double z, float yaw, float pitch)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y + 4F, (float) z);
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.0625F, -0.0625F, -0.0625F);
		bindEntityTexture(entity);
		model.render(entity, 0F, 0F, 0F, 0F, 0F, 1F);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		
		GL11.glColor4f(0F, 0F, 0F, 1F);
		GL11.glLineWidth(4F);
		Tessellator tess = Tessellator.instance;
		tess.startDrawing(GL11.GL_LINES);
		tess.addTranslation(0F, 0F, 0F);
		tess.addVertex(0D, -3D, 0D);
		tess.addVertex(-1D, 0D, 1D);
		
		tess.addVertex(0D, -3D, 0D);
		tess.addVertex(-1D, 0D, -1D);
		
		tess.addVertex(0D, -3D, 0D);
		tess.addVertex(1D, 0D, 1D);
		
		tess.addVertex(0D, -3D, 0D);
		tess.addVertex(1D, 0D, -1D);
		tess.draw();
		tess.setTranslation(0F, 0F, 0F);
		
		GL11.glPopMatrix();
	}
	
	@Override
	public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
	{
		renderParachute((EntityParachute) entity, d0, d1, d2, f, f1);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return PARACHUTE_TEXTURE;
	}
	
}
