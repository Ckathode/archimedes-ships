package com.tridevmc.davincisvessels.client.gui;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.IElementProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class DavincisUIHandler {

    public static final ResourceLocation TILE_ID = new ResourceLocation(DavincisVesselsMod.MOD_ID, "tile");
    public static final ResourceLocation ENTITY_ID = new ResourceLocation(DavincisVesselsMod.MOD_ID, "entity");
    public static final ResourceLocation OTHER_ID = new ResourceLocation(DavincisVesselsMod.MOD_ID, "other");

    public static GuiScreen openGui(FMLPlayMessages.OpenContainer openContainer) {
        Minecraft instance = Minecraft.getInstance();
        WorldClient world = instance.world;
        if (openContainer.getId().equals(TILE_ID)) {
            return ((IElementProvider) world.getTileEntity(openContainer.getAdditionalData().readBlockPos())).createGui(openContainer);
        } else if (openContainer.getId().equals(ENTITY_ID)) {
            return ((IElementProvider) world.getEntityByID(openContainer.getAdditionalData().readVarInt())).createGui(openContainer);
        } else {
            return null;
        }
    }

}
