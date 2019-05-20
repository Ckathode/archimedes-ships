package com.tridevmc.davincisvessels.client.gui;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class DavincisUIHandler {

    public static final ResourceLocation HELM_ID = new ResourceLocation(DavincisVesselsMod.MOD_ID, "helm");

    public static GuiScreen openGui(FMLPlayMessages.OpenContainer openContainer) {
        Minecraft instance = Minecraft.getInstance();
        WorldClient world = instance.world;
        if (openContainer.getId().equals(HELM_ID)) {
            BlockPos pos = openContainer.getAdditionalData().readBlockPos();
            return new GuiHelm((TileHelm) world.getTileEntity(pos), instance.player);
        }
        return null;
    }

}
