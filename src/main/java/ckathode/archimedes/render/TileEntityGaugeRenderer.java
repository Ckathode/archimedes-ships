package ckathode.archimedes.render;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ckathode.archimedes.blockitem.TileEntityGauge;

public class TileEntityGaugeRenderer extends TileEntitySpecialRenderer
{
	public static final ResourceLocation	GAUGE_TEXTURE_DEFAULT	= new ResourceLocation("archimedes", "textures/blocks/gauge.png");
	public static final ResourceLocation	GAUGE_TEXTURE_AIRSHIP	= new ResourceLocation("archimedes", "textures/blocks/gauge_ext.png");
	
	public void renderGauge(TileEntityGauge tileentity, double x, double y, double z, float partialticks)
	{
		RenderHelper.disableStandardItemLighting();
		
		boolean extended = (tileentity.blockMetadata & 4) != 0;
		int meta = tileentity.blockMetadata & 3;
		Tessellator tess = Tessellator.instance;
		
		double dx, dy, dz;
		if (tileentity.parentShip == null)
		{
			dx = x + 0.5F;
			dy = y;
			dz = z + 0.5F;
		} else if (tileentity.parentShip.riddenByEntity instanceof EntityPlayerSP)
		{
			dx = tileentity.parentShip.seatX - tileentity.xCoord;
			dy = tileentity.parentShip.seatY - tileentity.yCoord;
			dz = tileentity.parentShip.seatZ - tileentity.zCoord;
		} else
		{
			dx = tileentity.parentShip.posX - RenderManager.renderPosX;
			dy = tileentity.parentShip.posY - RenderManager.renderPosY;
			dz = tileentity.parentShip.posZ - RenderManager.renderPosZ;
		}
		double d = dx * dx + dy * dy + dz * dz;
		if (d > 256D) return;
		
		GL11.glLineWidth(8F / (float) Math.sqrt(d));
		
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		float northgaugeang;
		float velgaugeang;
		if (tileentity.parentShip == null)
		{
			northgaugeang = meta * 90F;
			velgaugeang = 0F;
		} else
		{
			velgaugeang = -(tileentity.parentShip.getHorizontalVelocity() * 3.6F * 20) / 60F * 270F; //vel in m/s * Tick rate (20 Hz) * 3.6 (conversion to km/h) with 60 km/h being 270 degrees.
			if (tileentity.parentShip.dimension == 0)
			{
				northgaugeang = -tileentity.parentShip.rotationYaw + meta * 90F;
			} else
			{
				//ang = (float) Math.random() * 360F;
				northgaugeang = (tileentity.parentShip.ticksExisted + partialticks) * 42F + tileentity.parentShip.rotationYaw / 3F;
			}
		}
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.05F, (float) z + 0.5F);
		GL11.glRotatef(180F - meta * 90F, 0F, 1F, 0F);
		
		//Direction gauge
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.28125F, 0.02F, -0.28125F);
		GL11.glRotatef(northgaugeang, 0F, 1F, 0F);
		
		tess.startDrawing(GL11.GL_LINES);
		tess.setColorOpaque_F(1F, 0F, 0F);
		tess.addVertex(0D, 0D, 0D);
		tess.addVertex(0D, 0D, 0.15D);
		tess.setColorOpaque_F(1F, 1F, 1F);
		tess.addVertex(0D, 0D, 0D);
		tess.addVertex(0D, 0D, -0.15D);
		tess.draw();
		GL11.glPopMatrix();
		
		//Velocity gauge
		GL11.glPushMatrix();
		GL11.glTranslatef(0.25F, 0.02F, -0.25F);
		GL11.glRotatef(velgaugeang, 0F, 1F, 0F);
		
		tess.startDrawing(GL11.GL_LINES);
		tess.setColorOpaque_F(0F, 0F, 0.5F);
		tess.addVertex(0D, 0D, 0D);
		tess.addVertex(0D, 0D, 0.2D);
		tess.draw();
		GL11.glPopMatrix();
		
		if (extended)
		{
			float vertgaugeang;
			float height = tileentity.yCoord;
			if (tileentity.parentShip == null)
			{
				vertgaugeang = 0F;
			} else
			{
				vertgaugeang = MathHelper.clamp_float(((float) tileentity.parentShip.motionY * 3.6F * 20) / 40F * 360F, -90F, 90F);
				height += (float) tileentity.parentShip.posY;
			}
			float heightgaugelongang = -height / 10F * 360F;
			//float heightgaugeshortang = -height / 100F * 360F;
			float heightgaugeshortang = heightgaugelongang / 10F;
			
			//Vertical velocity gauge
			GL11.glPushMatrix();
			GL11.glTranslatef(0.25F, 0.02F, 0.25F);
			GL11.glRotatef(vertgaugeang, 0F, 1F, 0F);
			
			tess.startDrawing(GL11.GL_LINES);
			tess.setColorOpaque_F(0F, 0F, 0.5F);
			tess.addVertex(0D, 0D, 0D);
			tess.addVertex(0.2D, 0D, 0D);
			tess.draw();
			GL11.glPopMatrix();
			
			//Height gauge
			GL11.glPushMatrix();
			GL11.glTranslatef(-0.25F, 0.02F, 0.25F);
			GL11.glPushMatrix();
			GL11.glRotatef(heightgaugelongang, 0F, 1F, 0F);
			
			tess.startDrawing(GL11.GL_LINES);
			tess.setColorOpaque_F(0.9F, 0.9F, 0F);
			tess.addVertex(0D, 0D, 0D);
			tess.addVertex(0D, 0D, -0.2D);
			tess.draw();
			GL11.glPopMatrix();
			
			GL11.glRotatef(heightgaugeshortang, 0F, 1F, 0F);
			
			tess.startDrawing(GL11.GL_LINES);
			tess.setColorOpaque_F(0.7F, 0.7F, 0F);
			tess.addVertex(0D, -0.01D, 0D);
			tess.addVertex(0D, -0.01, -0.15D);
			tess.draw();
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		GL11.glPopAttrib();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialticks)
	{
		renderGauge((TileEntityGauge) tileentity, x, y, z, partialticks);
	}
	
}
