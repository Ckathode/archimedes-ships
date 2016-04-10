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
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class TileEntityHelmRenderer extends TileEntitySpecialRenderer {

    public static HashMap<EnumFacing, IBakedModel> helmModels;
    Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
        public TextureAtlasSprite apply(ResourceLocation location) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
        }
    };

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (helmModels == null)
            helmModels = new HashMap<EnumFacing, IBakedModel>();
        if (helmModels.keySet().isEmpty()) {
            IModel model = null;

            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(ArchimedesShipMod.RESOURCE_DOMAIN + "block/helmWheel"));
            } catch (Exception e) {
                ArchimedesShipMod.modLog.error("A critical exception occured when rendering a helm model, " + e.getLocalizedMessage());
            }

            if (model != null) {
                helmModels.put(EnumFacing.NORTH, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 0)), Attributes.DEFAULT_BAKED_FORMAT, textureGetter));
                helmModels.put(EnumFacing.SOUTH, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)), Attributes.DEFAULT_BAKED_FORMAT, textureGetter));
                helmModels.put(EnumFacing.WEST, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, -90)), Attributes.DEFAULT_BAKED_FORMAT, textureGetter));
                helmModels.put(EnumFacing.EAST, model.bake(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)), Attributes.DEFAULT_BAKED_FORMAT, textureGetter));
            }
        }

        try {
            renderHelm((TileEntityHelm) te, x, y, z, partialTicks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderHelm(TileEntityHelm helm, double x, double y, double z, float partialTicks) throws IOException {
        EntityShip ship = null;
        IBlockState blockState = getWorld().getBlockState(helm.getPos());
        EnumFacing blockStateFacing = EnumFacing.UP;
        if (blockState.getBlock() instanceof BlockHelm)
            blockStateFacing = blockState.getValue(BlockHelm.FACING);

        if (((IMovingWorldTileEntity) helm).getParentMovingWorld() != null && ((IMovingWorldTileEntity) helm).getParentMovingWorld() instanceof EntityShip) {
            ship = (EntityShip) ((IMovingWorldTileEntity) helm).getParentMovingWorld();
        }

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
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

        IBakedModel modelBaked = null;
        if (helmModels.containsKey(blockStateFacing)) {
            bindTexture(TextureMap.locationBlocksTexture);
            modelBaked = helmModels.get(blockStateFacing);
        }
        vertexBuffer.begin(GL11.GL_QUADS, Attributes.DEFAULT_BAKED_FORMAT);
        GlStateManager.translate(newX, newY, newZ);

        if (modelBaked != null) {
            List<BakedQuad> generalQuads = modelBaked.getQuads(blockState, blockStateFacing, getWorld().rand.nextLong());

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
                vertexBuffer.color(1.0F, 1.0F, 1.0F, 1.0F);
                vertexBuffer.addVertexData(vd);
            }

        }

        tessellator.draw();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }
}
