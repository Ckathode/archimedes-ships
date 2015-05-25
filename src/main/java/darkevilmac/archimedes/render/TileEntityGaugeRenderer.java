package darkevilmac.archimedes.render;

import com.sun.javafx.geom.Vec3d;
import darkevilmac.archimedes.blockitem.TileEntityGauge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class TileEntityGaugeRenderer extends TileEntitySpecialRenderer {

    public void renderGauge(TileEntityGauge tileEntity, double x, double y, double z, float partialTicks) {
        RenderHelper.disableStandardItemLighting();

        boolean extended = tileEntity.getBlockMetadata() > 3;

        int meta = tileEntity.blockMetadata & 3;

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer worldRenderer = tess.getWorldRenderer();

        Vec3d dVec = new Vec3d(0, 0, 0);
        if (tileEntity.parentShip == null) {
            dVec.set(x + 0.5F, y, z + 0.5F);
        } else if (tileEntity.parentShip.riddenByEntity instanceof EntityPlayerSP) {
            dVec.set(tileEntity.parentShip.riderDestination.getX() - tileEntity.getPos().getX(),
                    tileEntity.parentShip.riderDestination.getY() - tileEntity.getPos().getY(),
                    tileEntity.parentShip.riderDestination.getZ() - tileEntity.getPos().getZ());
        } else {
            dVec.set(tileEntity.parentShip.posX - Minecraft.getMinecraft().getRenderManager().viewerPosX,
                    tileEntity.parentShip.posY - Minecraft.getMinecraft().getRenderManager().viewerPosY,
                    tileEntity.parentShip.posZ - Minecraft.getMinecraft().getRenderManager().viewerPosZ);
        }
        double d = dVec.x * dVec.x + dVec.y * dVec.y + dVec.z * dVec.z;
        if (d > 256D) return;

        GL11.glLineWidth(8F / (float) Math.sqrt(d));

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GlStateManager.disableTexture2D();

        float northGaugeAngle;
        float velGaugeAngle;
        if (tileEntity.parentShip == null) {
            northGaugeAngle = meta * 90F;
            velGaugeAngle = 0F;
        } else {
            velGaugeAngle = -(tileEntity.parentShip.getHorizontalVelocity() * 3.6F * 20) / 60F * 270F; //vel in m/s * Tick rate (20 Hz) * 3.6 (conversion to km/h) with 60 km/h being 270 degrees.
            if (tileEntity.parentShip.dimension == 0) {
                northGaugeAngle = -tileEntity.parentShip.rotationYaw + meta * 90F;
            } else {
                //ang = (float) Math.random() * 360F;
                northGaugeAngle = (tileEntity.parentShip.ticksExisted + partialTicks) * 42F + tileEntity.parentShip.rotationYaw / 3F;
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 0.05F, (float) z + 0.5F);
        GlStateManager.rotate(180F - meta * 90F, 0F, 1F, 0F);

        //Direction gauge
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.28125F, 0.02F, -0.28125F);
        GlStateManager.rotate(northGaugeAngle, 0F, 1F, 0F);

        worldRenderer.startDrawing(GL11.GL_LINES);
        worldRenderer.setColorOpaque_F(1F, 0F, 0F);
        worldRenderer.addVertex(0D, 0D, 0D);
        worldRenderer.addVertex(0D, 0D, 0.15D);
        worldRenderer.setColorOpaque_F(1F, 1F, 1F);
        worldRenderer.addVertex(0D, 0D, 0D);
        worldRenderer.addVertex(0D, 0D, -0.15D);
        tess.draw();
        GlStateManager.popMatrix();

        //Velocity gauge
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.25F, 0.02F, -0.25F);
        GlStateManager.rotate(velGaugeAngle, 0F, 1F, 0F);

        worldRenderer.startDrawing(GL11.GL_LINES);
        worldRenderer.setColorOpaque_F(0F, 0F, 0.5F);
        worldRenderer.addVertex(0D, 0D, 0D);
        worldRenderer.addVertex(0D, 0D, 0.2D);
        tess.draw();
        GlStateManager.popMatrix();

        if (extended) {
            float vertGaugeAng;
            float height = tileEntity.getPos().getY();
            if (tileEntity.parentShip == null) {
                vertGaugeAng = 0F;
            } else {
                vertGaugeAng = MathHelper.clamp_float(((float) tileEntity.parentShip.motionY * 3.6F * 20) / 40F * 360F, -90F, 90F);
                height += (float) tileEntity.parentShip.posY;
            }
            float heightGaugeLongAng = -height / 10F * 360F;
            //float heightgaugeshortang = -height / 100F * 360F;
            float heightGaugeShortAng = heightGaugeLongAng / 10F;

            //Vertical velocity gauge
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.25F, 0.02F, 0.25F);
            GlStateManager.rotate(vertGaugeAng, 0F, 1F, 0F);

            worldRenderer.startDrawing(GL11.GL_LINES);
            worldRenderer.setColorOpaque_F(0F, 0F, 0.5F);
            worldRenderer.addVertex(0D, 0D, 0D);
            worldRenderer.addVertex(0.2D, 0D, 0D);
            tess.draw();
            GlStateManager.popMatrix();

            //Height gauge
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.25F, 0.02F, 0.25F);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(heightGaugeLongAng, 0F, 1F, 0F);

            worldRenderer.startDrawing(GL11.GL_LINES);
            worldRenderer.setColorOpaque_F(0.9F, 0.9F, 0F);
            worldRenderer.addVertex(0D, 0D, 0D);
            worldRenderer.addVertex(0D, 0D, -0.2D);
            tess.draw();
            GlStateManager.popMatrix();

            GlStateManager.rotate(heightGaugeShortAng, 0F, 1F, 0F);

            worldRenderer.startDrawing(GL11.GL_LINES);
            worldRenderer.setColorOpaque_F(0.7F, 0.7F, 0F);
            worldRenderer.addVertex(0D, -0.01D, 0D);
            worldRenderer.addVertex(0D, -0.01, -0.15D);
            tess.draw();

            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();

        GlStateManager.popAttrib();
    }


    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double posX, double posY, double posZ, float partialTicks, int par6) {
        renderGauge((TileEntityGauge) tileEntity, posX, posY, posZ, partialTicks);
    }
}
