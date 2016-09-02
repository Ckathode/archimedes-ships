package io.github.elytra.davincisvessels.client.render;

import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.common.object.block.BlockHelm;
import io.github.elytra.davincisvessels.common.tileentity.TileEntityHelm;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;

import io.github.elytra.davincisvessels.common.entity.EntityShip;
import io.github.elytra.movingworld.api.IMovingWorldTileEntity;

public class TileEntityHelmRenderer extends TileEntitySpecialRenderer {

    public ModelHelmWheel wheel = new ModelHelmWheel();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        try {
            renderHelm((TileEntityHelm) te, x, y, z, partialTicks);
        } catch (Exception e) {
            if (e instanceof IOException)
                e.printStackTrace();
            else
                DavincisVesselsMod.modLog.error("Error when rendering helm, ", e);
        }
    }

    private void renderHelm(TileEntityHelm helm, double x, double y, double z, float partialTicks) throws Exception {
        EntityShip ship = null;
        IBlockState blockState = getWorld().getBlockState(helm.getPos());
        EnumFacing blockStateFacing = EnumFacing.UP;

        if (blockState.getBlock() instanceof BlockHelm)
            blockStateFacing = blockState.getValue(BlockHelm.FACING);
        if (((IMovingWorldTileEntity) helm).getParentMovingWorld() != null && ((IMovingWorldTileEntity) helm).getParentMovingWorld() instanceof EntityShip) {
            ship = (EntityShip) ((IMovingWorldTileEntity) helm).getParentMovingWorld();
        }

        GlStateManager.pushMatrix();
        float shipPitch = 0;
        if (ship != null) {
            shipPitch = (ship.rotationPitch * 10);
            if (blockStateFacing == EnumFacing.NORTH || blockStateFacing == EnumFacing.WEST) {
                shipPitch *= -1;
            }
            Vec3d vec3d = new Vec3d(2.5, 1.6, 2.5);

            translate(vec3d);
            GlStateManager.rotate(shipPitch,
                    blockStateFacing.getAxis() == EnumFacing.Axis.X ? 1 : 0,
                    0,
                    blockStateFacing.getAxis() == EnumFacing.Axis.Z ? 1 : 0);
            translate(vec3d.scale(-1));
        }

        wheel.render(x, y, z, blockState, helm, blockStateFacing);
        GlStateManager.popMatrix();

        helm.prevPitch = shipPitch;
    }

    private void translate(Vec3d translate) {
        GlStateManager.translate(translate.xCoord, translate.yCoord, translate.zCoord);
    }
}
