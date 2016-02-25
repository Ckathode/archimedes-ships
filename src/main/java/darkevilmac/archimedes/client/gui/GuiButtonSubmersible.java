package darkevilmac.archimedes.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.List;

public class GuiButtonSubmersible extends GuiButton {

    public boolean submerse = false;

    public boolean canDo = true;

    public GuiButtonSubmersible(int buttonId, int x, int y) {
        super(buttonId, x, y, 32, 32, "");
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(new ResourceLocation("archimedesshipsplus", "textures/gui/submerse.png"));
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean mouseOver = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int yOffset = 0;
            int xOffset = 0;

            if (canDo) {
                if (mouseOver) {
                    yOffset += 32;
                }

                if (!submerse) {
                    xOffset += 32;
                }
            } else {
                yOffset = 64;
                xOffset = 0;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, xOffset, yOffset, this.width, this.height);

            if (mouseOver) {
                String message = !canDo ? "Can't Submerse" : (submerse ? "Submerse Ship" : "Don't Submerse Ship");
                int stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(message);
                drawHoveringText(Lists.newArrayList(message),
                        mouseX + (stringWidth / 2) + 32, mouseY - 12, Minecraft.getMinecraft().fontRendererObj);
            }
        }
    }

    protected void drawHoveringText(List textLines, int x, int y, FontRenderer font) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int k = 0;
            Iterator iterator = textLines.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                int l = font.getStringWidth(s);

                if (l > k) {
                    k = l;
                }
            }

            int j2 = x;
            int k2 = y;
            int i1 = 8;

            if (textLines.size() > 1) {
                i1 += 2 + (textLines.size() - 1) * 10;
            }

            if (j2 + k > this.width) {
                j2 -= 28 + k;
            }

            this.zLevel = 300.0F;
            int j1 = -267386864;
            this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            for (int i2 = 0; i2 < textLines.size(); ++i2) {
                String s1 = (String) textLines.get(i2);
                font.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0) {
                    k2 += 2;
                }

                k2 += 10;
            }

            this.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

}
