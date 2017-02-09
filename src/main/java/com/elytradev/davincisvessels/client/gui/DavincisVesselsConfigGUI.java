package com.elytradev.davincisvessels.client.gui;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class DavincisVesselsConfigGUI extends GuiConfig {

    public DavincisVesselsConfigGUI(GuiScreen parentScreen) {
        super(parentScreen, generateConfigList(), DavincisVesselsMod.MOD_ID,
                false, false, GuiConfig.getAbridgedConfigPath(DavincisVesselsMod.INSTANCE.getNetworkConfig().getConfig().toString()));
    }

    public static List<IConfigElement> generateConfigList() {

        ArrayList<IConfigElement> elements = new ArrayList<IConfigElement>();

        for (String name : DavincisVesselsMod.INSTANCE.getNetworkConfig().getConfig().getCategoryNames())
            elements.add(new ConfigElement(DavincisVesselsMod.INSTANCE.getNetworkConfig().getConfig().getCategory(name)));

        return elements;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
    }

}
