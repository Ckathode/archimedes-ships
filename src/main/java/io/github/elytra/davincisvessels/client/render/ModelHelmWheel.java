package io.github.elytra.davincisvessels.client.render;

import com.google.common.base.Function;
import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.common.tileentity.TileEntityHelm;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
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
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.HashMap;

public class ModelHelmWheel {

    public static ReloadListener reloadListener;
    protected static HashMap<EnumFacing, IBakedModel> helmModels;
    Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
        public TextureAtlasSprite apply(ResourceLocation location) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
        }
    };

    public ModelHelmWheel() {
        if (helmModels == null)
            helmModels = new HashMap<>();
        if (helmModels.keySet().isEmpty()) {
            IModel model = null;

            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "block/helmWheel"));
            } catch (Exception e) {
                DavincisVesselsMod.modLog.error("A critical exception occurred when rendering a helm model, " + e.getLocalizedMessage());
            }

            if (model != null) {
                helmModels.put(EnumFacing.NORTH, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)), DefaultVertexFormats.BLOCK, textureGetter));
                helmModels.put(EnumFacing.SOUTH, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, -90)), DefaultVertexFormats.BLOCK, textureGetter));
                helmModels.put(EnumFacing.WEST, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 0)), DefaultVertexFormats.BLOCK, textureGetter));
                helmModels.put(EnumFacing.EAST, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)), DefaultVertexFormats.BLOCK, textureGetter));
            }

            reloadListener = new ReloadListener();
            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(reloadListener);
        }
    }

    public void render(double x, double y, double z, IBlockState state, TileEntityHelm helm, EnumFacing direction) {
        boolean pushAndPop = false;
        IBakedModel modelBaked;

        if (helmModels.containsKey(direction)) {
            modelBaked = helmModels.get(direction);
        } else return;

        if (pushAndPop)
            GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z + 1);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        } else {
            GlStateManager.shadeModel(7424);
        }

        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightness(modelBaked,
                state,
                helm.getWorld().getCombinedLight(helm.getPos(), 0), false);

        if (pushAndPop)
            GlStateManager.popMatrix();
    }

    public class ReloadListener implements IResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            helmModels.clear();

            IModel model = null;

            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(DavincisVesselsMod.RESOURCE_DOMAIN + "block/helmWheel"));
            } catch (Exception e) {
                DavincisVesselsMod.modLog.error("A critical exception occurred when rendering a helm model, " + e.getLocalizedMessage());
            }

            if (model != null) {
                helmModels.put(EnumFacing.NORTH, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)), DefaultVertexFormats.BLOCK, textureGetter));
                helmModels.put(EnumFacing.SOUTH, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, -90)), DefaultVertexFormats.BLOCK, textureGetter));
                helmModels.put(EnumFacing.WEST, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 0)), DefaultVertexFormats.BLOCK, textureGetter));
                helmModels.put(EnumFacing.EAST, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)), DefaultVertexFormats.BLOCK, textureGetter));
            }
        }
    }
}
