package com.elytradev.davincisvessels.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.elytradev.davincisvessels.common.entity.EntitySeat;

public class RenderSeat extends Render<EntitySeat> {
    public RenderSeat(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySeat entity) {
        return new ResourceLocation("");
    }

    @Override
    public void doRender(EntitySeat entity, double x, double y, double z, float entityYaw, float partialTicks) {
        //dont
    }

}
