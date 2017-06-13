package com.elytradev.davincisvessels.client.render;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.elytradev.davincisvessels.common.tileentity.TileHelm;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;

import java.io.IOException;

import com.elytradev.davincisvessels.common.object.block.BlockHelm;
import com.elytradev.movingworld.api.IMovingTile;

public class TileEntityHelmRenderer extends TileEntitySpecialRenderer<TileHelm> {

    @Override
    public void func_192841_a(TileHelm te, double x, double y, double z, float partialTicks, int destroyStage, float idk) {
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
            blockStateFacing = blockState.getValue(BlockHelm.FACING);
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
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.enableRescaleNormal();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        } else {
            GlStateManager.shadeModel(7424);
        }

        GlStateManager.translate(x, y, z + 1);

        GlStateManager.translate(translateX, translateY, translateZ);
        GlStateManager.rotate(shipPitch * 10, onZAxis ? 0 : 1, 0, onZAxis ? 1 : 0);
        GlStateManager.translate(-translateX, -translateY, -translateZ);

        IBlockState wheelState = blockState.withProperty(BlockHelm.IS_WHEEL, true);
        IBakedModel stateModel = Minecraft.getMinecraft().getBlockRendererDispatcher()
                .getModelForState(wheelState);
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer()
                .renderModelBrightness(stateModel, wheelState, 1, false);

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

    }
}
