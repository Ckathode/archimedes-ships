package darkevilmac.archimedes.client.render;

import darkevilmac.archimedes.common.entity.EntityParachute;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

//TODO: possible rewrite?

public class RenderParachute extends Render {
    public static final ResourceLocation PARACHUTE_TEXTURE = new ResourceLocation("archimedesshipsplus", "textures/entity/parachute.png");

    public ModelParachute model;

    public RenderParachute(RenderManager renderManager) {
        super(renderManager);
        model = new ModelParachute();
    }

    public void renderParachute(EntityParachute entity, double x, double y, double z, float yaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + (entity != null && entity.riddenByEntity != null ? entity.riddenByEntity.height * 2.5F : 4F), (float) z);

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.0625F, -0.0625F, -0.0625F);
        bindEntityTexture(entity);
        model.render(entity, 0F, 0F, 0F, 0F, 0F, 1F);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        GlStateManager.color(0F, 0F, 0F, 1F);
        GL11.glLineWidth(4F);
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer worldRenderer = tess.getWorldRenderer();
        GlStateManager.color(0, 0, 0);
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        worldRenderer.pos(0D, -3D, 0D).endVertex();
        worldRenderer.pos(-1D, 0D, 1D).endVertex();

        worldRenderer.pos(0D, -3D, 0D).endVertex();
        worldRenderer.pos(-1D, 0D, -1D).endVertex();

        worldRenderer.pos(0D, -3D, 0D).endVertex();
        worldRenderer.pos(1D, 0D, 1D).endVertex();

        worldRenderer.pos(0D, -3D, 0D).endVertex();
        worldRenderer.pos(1D, 0D, -1D).endVertex();
        tess.draw();
        worldRenderer.setTranslation(0F, 0F, 0F);

        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1) {
        renderParachute((EntityParachute) entity, d0, d1, d2, f, f1);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return PARACHUTE_TEXTURE;
    }

}
