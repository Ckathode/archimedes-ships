package darkevilmac.archimedes.client.render;

import com.google.common.base.Function;
import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.object.block.BlockHelm;
import darkevilmac.archimedes.common.tileentity.TileEntityHelm;
import darkevilmac.movingworld.common.tile.IMovingWorldTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;

import java.io.IOException;
import java.util.List;

public class TileEntityHelmRenderer extends TileEntitySpecialRenderer {

    Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
        public TextureAtlasSprite apply(ResourceLocation location) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
        }
    };

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        try {
            renderHelm((TileEntityHelm) te, x, y, z, partialTicks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderHelm(TileEntityHelm helm, double x, double y, double z, float partialTicks) throws IOException {
        EntityShip ship = null;
        IBlockState blockState = getWorld().getBlockState(helm.getPos());
        EnumFacing blockStateFacing = (EnumFacing) blockState.getValue(BlockHelm.FACING);

        if (((IMovingWorldTileEntity) helm).getParentMovingWorld() != null && ((IMovingWorldTileEntity) helm).getParentMovingWorld() instanceof EntityShip) {
            ship = (EntityShip) ((IMovingWorldTileEntity) helm).getParentMovingWorld();
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        bindTexture(TextureMap.locationBlocksTexture);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        } else {
            GlStateManager.shadeModel(7424);
        }

        GlStateManager.pushMatrix();

        float newX, newY, newZ = 0;
        newX = (float) x;
        newY = (float) y;
        newZ = (float) z;

        int rotDeg = 0;
        if (blockStateFacing == EnumFacing.SOUTH) {
            rotDeg = 180;
        } else if (blockStateFacing == EnumFacing.WEST) {
            rotDeg = -90;
        } else if (blockStateFacing == EnumFacing.EAST) {
            rotDeg = 90;
        }

        GlStateManager.translate(newX, newY, newZ);

        worldRenderer.startDrawingQuads();

        IModel model;
        model = ModelLoaderRegistry.getModel(new ResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + "block/helmWheel"));

        if (model != null) {
            TRSRTransformation trsrTransformation = new TRSRTransformation(ModelRotation.getModelRotation(0, rotDeg));

            IFlexibleBakedModel modelBaked = model.bake(trsrTransformation, Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
            List<BakedQuad> generalQuads = modelBaked.getGeneralQuads();

            float shipPitch = 0;
            if (ship != null) {
                shipPitch = ship.rotationPitch * 5;
                if (blockStateFacing == EnumFacing.NORTH || blockStateFacing == EnumFacing.WEST) {
                    shipPitch *= -1;
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(blockStateFacing.getAxis() == EnumFacing.Axis.Z ? .5 : 0
                    , .6, blockStateFacing.getAxis() == EnumFacing.Axis.X ? .5 : 0);
            if (blockStateFacing.getAxis() == EnumFacing.Axis.Z) {
                GlStateManager.rotate(shipPitch, 0, 0, 1);
            } else if (blockStateFacing.getAxis() == EnumFacing.Axis.X) {
                GlStateManager.rotate(shipPitch, 1, 0, 0);
            }
            GlStateManager.translate(blockStateFacing.getAxis() == EnumFacing.Axis.Z ? -.5 : 0
                    , -.6, blockStateFacing.getAxis() == EnumFacing.Axis.X ? -.5 : 0);

            for (BakedQuad q : generalQuads) {
                int[] vd = q.getVertexData();
                worldRenderer.setVertexFormat(Attributes.DEFAULT_BAKED_FORMAT);
                worldRenderer.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                worldRenderer.addVertexData(vd);
            }

        }

        tessellator.draw();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }
}
