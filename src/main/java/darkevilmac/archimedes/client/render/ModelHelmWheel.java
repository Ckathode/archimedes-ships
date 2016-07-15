package darkevilmac.archimedes.client.render;

import com.google.common.base.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.HashMap;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.tileentity.TileEntityHelm;

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
            helmModels = new HashMap<EnumFacing, IBakedModel>();
        if (helmModels.keySet().isEmpty()) {
            IModel model = null;

            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + "block/helmWheel"));
            } catch (Exception e) {
                ArchimedesShipMod.modLog.error("A critical exception occurred when rendering a helm model, " + e.getLocalizedMessage());
            }

            if (model != null) {
                helmModels.put(EnumFacing.NORTH, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 0)), DefaultVertexFormats.ITEM, textureGetter));
                helmModels.put(EnumFacing.SOUTH, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)), DefaultVertexFormats.ITEM, textureGetter));
                helmModels.put(EnumFacing.WEST, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, -90)), DefaultVertexFormats.ITEM, textureGetter));
                helmModels.put(EnumFacing.EAST, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)), DefaultVertexFormats.ITEM, textureGetter));
            }

            reloadListener = new ReloadListener();
            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(reloadListener);
        }
    }

    public double x, y, z;

    public void render(double x, double y, double z, IBlockState state, TileEntityHelm helm, EnumFacing direction) {
        setTransforms(x, y, z);
        IBakedModel modelBaked;

        if (helmModels.containsKey(direction)) {
            modelBaked = helmModels.get(direction);
        } else return;

        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        } else {
            GlStateManager.shadeModel(7424);
        }

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.BLOCK);
        vertexBuffer.setTranslation(x, y, z);
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        blockrendererdispatcher.getBlockModelRenderer().renderModel(helm.getWorld(), modelBaked, state, BlockPos.ORIGIN, vertexBuffer, false, MathHelper.getPositionRandom(helm.getPos()));
        vertexBuffer.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }


    public void setTransforms(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public class ReloadListener implements IResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            helmModels.clear();

            IModel model = null;

            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + "block/helmWheel"));
            } catch (Exception e) {
                ArchimedesShipMod.modLog.error("A critical exception occurred when rendering a helm model, " + e.getLocalizedMessage());
            }

            if (model != null) {
                helmModels.put(EnumFacing.NORTH, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 0)), DefaultVertexFormats.BLOCK, textureGetter));
                helmModels.put(EnumFacing.SOUTH, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)), DefaultVertexFormats.BLOCK, textureGetter));
                helmModels.put(EnumFacing.WEST, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, -90)), DefaultVertexFormats.BLOCK, textureGetter));
                helmModels.put(EnumFacing.EAST, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)), DefaultVertexFormats.BLOCK, textureGetter));
            }
        }
    }
}
