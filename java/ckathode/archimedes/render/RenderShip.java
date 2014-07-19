package ckathode.archimedes.render;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ckathode.archimedes.chunk.MobileChunkClient;
import ckathode.archimedes.entity.EntityShip;

public class RenderShip extends Render
{
	public RenderShip()
	{
		shadowSize = 1F;
	}
	
	public void renderVehicle(EntityShip entity, double x, double y, double z, float yaw, float rendertime)
	{
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		
		float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * rendertime;
		
		float rx = entity.frontDirection == 1 ? -1f : entity.frontDirection == 3 ? 1f : 0f;
		float rz = entity.frontDirection == 0 ? 1f : entity.frontDirection == 2 ? -1f : 0f;
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(yaw, 0F, 1F, 0F);
		GL11.glRotatef(pitch, rx, 0f, rz);
		
		float fx = entity.getShipChunk().getCenterX();
		float fz = entity.getShipChunk().getCenterZ();
		GL11.glTranslatef(-fx, -entity.getShipChunk().minY(), -fz); //minY is always 0
		
		//float f4 = 0.75F;
		bindEntityTexture(entity);
		((MobileChunkClient) entity.getShipChunk()).getRenderer().render(0F);
		GL11.glPopMatrix();
		
		GL11.glPopAttrib();
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float pitch)
	{
		renderVehicle((EntityShip) entity, x, y, z, yaw, pitch);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return TextureMap.locationBlocksTexture;
	}
	
}
