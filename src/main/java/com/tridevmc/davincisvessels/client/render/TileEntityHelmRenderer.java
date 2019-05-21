package com.tridevmc.davincisvessels.client.render;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.content.block.BlockHelm;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import com.tridevmc.movingworld.api.IMovingTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.EnumFacing;

import java.io.IOException;

public class TileEntityHelmRenderer extends TileEntityRenderer<TileHelm> {

    @Override
    public void render(TileHelm te, double x, double y, double z, float partialTicks, int destroyStage) {
        try {
            renderHelm(te, x, y, z, partialTicks);
        } catch (Exception e) {
            if (e instanceof IOException)
                e.printStackTrace();
            else
                DavincisVesselsMod.LOG.error("Error when rendering helm, ", e);
        }
    }

    private void renderHelm(TileHelm helm, double x, double y, double z, float partialTicks) throws Exception {
        EntityShip ship = null;
        IBlockState blockState = getWorld().getBlockState(helm.getPos());
        EnumFacing blockStateFacing = EnumFacing.UP;

        if (blockState.getBlock() instanceof BlockHelm)
            blockStateFacing = blockState.get(BlockHelm.FACING);
        if (((IMovingTile) helm).getParentMovingWorld() != null && ((IMovingTile) helm).getParentMovingWorld() instanceof EntityShip) {
            ship = (EntityShip) ((IMovingTile) helm).getParentMovingWorld();
        }

        float shipPitch = 0;
        if (ship != null)
            shipPitch = ship.prevRotationPitch + (ship.rotationPitch - ship.prevRotationPitch) * partialTicks;

        if (blockStateFacing == EnumFacing.NORTH || blockStateFacing == EnumFacing.WEST) {
            shipPitch *= -1;
        }
        boolean onZAxis = blockStateFacing.getAxis() == EnumFacing.Axis.Z;

        float translateX, translateY, translateZ;

        if (onZAxis) {
            translateX = .5F;
            translateY = 10F / 16F;
            translateZ = 0F;
        } else {
            translateX = 0F;
            translateY = 10F / 16F;
            translateZ = -.5F;
        }

        GlStateManager.pushMatrix();
        Minecraft.getInstance().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.enableRescaleNormal();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        } else {
            GlStateManager.shadeModel(7424);
        }

        GlStateManager.translated(x, y, z + 1);

        GlStateManager.translatef(translateX, translateY, translateZ);
        GlStateManager.rotatef(shipPitch * 10, onZAxis ? 0 : 1, 0, onZAxis ? 1 : 0);
        GlStateManager.translatef(-translateX, -translateY, -translateZ);

        IBlockState wheelState = blockState.with(BlockHelm.IS_WHEEL, true);
        IBakedModel stateModel = Minecraft.getInstance().getBlockRendererDispatcher()
                .getModelForState(wheelState);
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer()
                .renderModelBrightness(stateModel, wheelState, 1, false);

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

    }
}
