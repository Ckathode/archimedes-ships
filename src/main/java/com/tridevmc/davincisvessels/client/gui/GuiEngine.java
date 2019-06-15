package com.tridevmc.davincisvessels.client.gui;

import com.tridevmc.davincisvessels.common.LanguageEntries;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

public class GuiEngine extends ContainerScreen {
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("davincisvessels", "textures/gui/engine.png");

    public GuiEngine(ContainerEngine container) {
        super(container, container.player.inventory, new StringTextComponent(""));
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mousex, int mousey) {
        int color = 0x404040;
        int row = 8;
        int col0 = 8;

        font.drawString(I18n.format(LanguageEntries.GUI_ENGINE_TITLE), col0, row, color);
        row += 5;

        font.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, color);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        minecraft.textureManager.bindTexture(BACKGROUND_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        blit(x, y, 0, 0, xSize, ySize);
    }

}
