package io.github.elytra.davincisvessels.client.render;

import com.google.common.base.Function;
import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.common.tileentity.TileEntityHelm;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ModelHelmWheel {

    public static ReloadListener reloadListener;
    public static IBakedModel helmModel;
    Function<ResourceLocation, TextureAtlasSprite> textureGetter = location ->
            Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());

    public ModelHelmWheel() {
        if (helmModel == null) {
            IModel model = null;

            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "block/helmWheel"));
            } catch (Exception e) {
                DavincisVesselsMod.modLog.error("A critical exception occurred when rendering a helm model, " + e.getLocalizedMessage());
            }

            if (model != null) {
                helmModel = model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK, textureGetter);
            }

            reloadListener = new ReloadListener();
            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(reloadListener);
        }
    }

    public void render(double x, double y, double z, IBlockState state, TileEntityHelm helm, EnumFacing direction) {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        } else {
            GlStateManager.shadeModel(7424);
        }

        GlStateManager.translate(x, y, z + 1);

        switch (direction) {
            case NORTH: {
                GlStateManager.translate(0.5, 0, -0.5);
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(-0.5, 0, 0.5);
            }
            case SOUTH: {
                GlStateManager.translate(0.5, 0, -0.5);
                GlStateManager.rotate(-90, 0, 1, 0);
                GlStateManager.translate(-0.5, 0, 0.5);
            }
            case EAST: {
                GlStateManager.translate(0.5, 0, -0.5);
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(-0.5, 0, 0.5);
            }
        }

        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightness(helmModel,
                state,
                helm.getWorld().getCombinedLight(helm.getPos(), 0), false);

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    public class ReloadListener implements IResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            helmModel = null;
            IModel model = null;

            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "block/helmWheel"));
            } catch (Exception e) {
                DavincisVesselsMod.modLog.error("A critical exception occurred when rendering a helm model, " + e.getLocalizedMessage());
            }

            if (model != null) {
                helmModel = model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK, textureGetter);
            }
        }
    }
}
