package io.github.elytra.davincisvessels.client.render;

import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.common.entity.EntityShip;
import io.github.elytra.davincisvessels.common.object.block.BlockHelm;
import io.github.elytra.davincisvessels.common.tileentity.TileHelm;
import io.github.elytra.movingworld.api.IMovingTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.io.IOException;

public class TileEntityHelmRenderer extends TileEntitySpecialRenderer {

    public ModelHelmWheel wheel = new ModelHelmWheel();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        try {
            renderHelm((TileHelm) te, x, y, z, partialTicks);
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

        GlStateManager.pushMatrix();

        float shipPitch = 0;
        if (ship != null)
            shipPitch = ship.prevRotationPitch + (ship.rotationPitch - ship.prevRotationPitch) * partialTicks;

        if (blockStateFacing == EnumFacing.NORTH || blockStateFacing == EnumFacing.WEST) {
            shipPitch *= -1;
        }

        boolean onZAxis = blockStateFacing.getAxis() == EnumFacing.Axis.Z;

        float translateX, translateY, translateZ;

        if (onZAxis) {
            translateX = 4.5F;
            translateY = 1.65F;
            translateZ = 0F;
        } else {
            translateX = 0F;
            translateY = 1.65F;
            translateZ = 4.5F;
        }

        GlStateManager.translate(translateX, translateY, translateZ);
        GlStateManager.rotate(shipPitch * 10, onZAxis ? 0 : 1, 0, onZAxis ? 1 : 0);
        GlStateManager.translate(-translateX, -translateY, -translateZ);

        wheel.render(x, y, z, blockState, helm, blockStateFacing);
        GlStateManager.popMatrix();
    }
}
