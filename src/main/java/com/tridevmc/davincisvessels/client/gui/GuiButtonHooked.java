package com.tridevmc.davincisvessels.client.gui;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.List;

public class GuiButtonHooked extends GuiButtonExt {
    private List<IClickHook> hooks = Lists.newArrayList();

    public GuiButtonHooked(int id, int xPos, int yPos, int width, int height, String displayString) {
        super(id, xPos, yPos, width, height, displayString);
        this.hooks = hooks;
    }

    public GuiButtonHooked(int id, int xPos, int yPos, String displayString) {
        super(id, xPos, yPos, displayString);
    }

    public void addHook(IClickHook hook) {
        this.hooks.add(hook);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.hooks.forEach(h -> h.onClick(mouseX, mouseY));
    }
}
