package com.tridevmc.davincisvessels.client.render;

import com.tridevmc.davincisvessels.common.entity.EntitySeat;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class RenderSeat extends EntityRenderer<EntitySeat> {
    public RenderSeat(EntityRendererManager renderManager) {
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
