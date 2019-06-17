package com.tridevmc.davincisvessels.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.content.block.BlockGauge;
import com.tridevmc.davincisvessels.common.tileentity.TileGauge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.opengl.GL11;

public class TileEntityGaugeRenderer extends TileEntityRenderer<TileGauge> {
    @Override
    public void render(TileGauge gauge, double x, double y, double z, float partialTicks, int destroyStage) {
        RenderHelper.disableStandardItemLighting();

        boolean extended = gauge.getBlockState().getBlock() == DavincisVesselsMod.CONTENT.blockGaugeExtended;
        int meta = gauge.getBlockState().get(BlockGauge.FACING).getHorizontalIndex();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        Vec3d dVec = new Vec3d(0, 0, 0);
        if (gauge.parentVessel == null) {
            dVec.add(0.5F, 0, 0.5F);
        } else if (gauge.parentVessel.getControllingPassenger() instanceof ClientPlayerEntity) {
            dVec = new Vec3d(gauge.parentVessel.riderDestination.getX() - gauge.getPos().getX(),
                    gauge.parentVessel.riderDestination.getY() - gauge.getPos().getY(),
                    gauge.parentVessel.riderDestination.getZ() - gauge.getPos().getZ());
        } else {
            dVec = new Vec3d(gauge.parentVessel.posX - Minecraft.getInstance().getRenderManager().info.getProjectedView().x,
                    gauge.parentVessel.posY - Minecraft.getInstance().getRenderManager().info.getProjectedView().y,
                    gauge.parentVessel.posZ - Minecraft.getInstance().getRenderManager().info.getProjectedView().z);
        }
        double d = dVec.x * dVec.x + dVec.y * dVec.y + dVec.z * dVec.z;
        if (d > 256D) return;

        GL11.glLineWidth(6);

        GlStateManager.disableTexture();

        float northGaugeAngle;
        float velGaugeAngle;
        if (gauge.parentVessel == null) {
            northGaugeAngle = meta * 90F;
            velGaugeAngle = 0F;
        } else {
            velGaugeAngle = -(gauge.parentVessel.getHorizontalVelocity() * 3.6F * 20) / 60F * 270F; //vel in m/s * Tick rate (20 Hz) * 3.6 (conversion to km/h) with 60 km/h being 270 degrees.
            if (gauge.parentVessel.dimension == DimensionType.OVERWORLD) {
                northGaugeAngle = -gauge.parentVessel.rotationYaw + meta * 90F;
            } else {
                northGaugeAngle = (gauge.parentVessel.ticksExisted + partialTicks) * 42F + gauge.parentVessel.rotationYaw / 3F;
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x + 0.5F, (float) y + 0.05F, (float) z + 0.5F);
        GlStateManager.rotatef(180F - meta * 90F, 0F, 1F, 0F);

        //Direction gauge
        GlStateManager.pushMatrix();
        GlStateManager.translatef(-0.28125F, 0.02F, -0.28125F);
        GlStateManager.rotatef(northGaugeAngle, 0F, 1F, 0F);

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        GlStateManager.color4f(1F, 0F, 0F, 1F);
        buffer.pos(0D, 0D, 0D).endVertex();
        buffer.pos(0D, 0D, 0.15D).endVertex();
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        buffer.pos(0D, 0D, 0D).endVertex();
        buffer.pos(0D, 0D, -0.15D).endVertex();
        tess.draw();
        GlStateManager.popMatrix();

        //Velocity gauge
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.25F, 0.02F, -0.25F);
        GlStateManager.rotatef(velGaugeAngle, 0F, 1F, 0F);

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        GlStateManager.color4f(0F, 0F, 0.5F, 1F);
        buffer.pos(0D, 0D, 0D).endVertex();
        buffer.pos(0D, 0D, 0.2D).endVertex();
        tess.draw();
        GlStateManager.popMatrix();

        if (extended) {
            float vertGaugeAng;
            float height = gauge.getPos().getY();
            if (gauge.parentVessel == null) {
                vertGaugeAng = 0F;
            } else {
                vertGaugeAng = MathHelper.clamp(((float) gauge.parentVessel.getMotion().y * 3.6F * 20) / 40F * 360F, -90F, 90F);
                height += (float) gauge.parentVessel.posY;
            }
            float heightGaugeLongAng = -height / 10F * 360F;
            float heightGaugeShortAng = heightGaugeLongAng / 10F;

            //Vertical velocity gauge
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.25F, 0.02F, 0.25F);
            GlStateManager.rotatef(vertGaugeAng, 0F, 1F, 0F);

            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
            GlStateManager.color4f(0F, 0F, 0.5F, 1F);
            buffer.pos(0D, 0D, 0D).endVertex();
            buffer.pos(0.2D, 0D, 0D).endVertex();
            tess.draw();
            GlStateManager.popMatrix();

            //Height gauge
            GlStateManager.pushMatrix();
            GlStateManager.translatef(-0.25F, 0.02F, 0.25F);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(heightGaugeLongAng, 0F, 1F, 0F);

            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
            GlStateManager.color4f(0.9F, 0.9F, 0F, 1F);
            buffer.pos(0D, 0D, 0D).endVertex();
            buffer.pos(0D, 0D, -0.2D).endVertex();
            tess.draw();
            GlStateManager.popMatrix();

            GlStateManager.rotatef(heightGaugeShortAng, 0F, 1F, 0F);

            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
            GlStateManager.color4f(0.7F, 0.7F, 0F, 1F);
            buffer.pos(0D, -0.01D, 0D).endVertex();
            buffer.pos(0D, -0.01, -0.15D).endVertex();
            tess.draw();

            GlStateManager.popMatrix();
        }

        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }
}
