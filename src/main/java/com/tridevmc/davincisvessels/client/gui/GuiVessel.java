package com.tridevmc.davincisvessels.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.tridevmc.davincisvessels.common.LanguageEntries;
import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import com.tridevmc.davincisvessels.common.network.message.RequestSubmerseMessage;
import com.tridevmc.movingworld.common.network.MovingWorldClientAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.List;

public class GuiVessel extends ContainerScreen<ContainerVessel> {
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("davincisvessels", "textures/gui/vesselinv.png");

    public final EntityVessel vessel;
    public final PlayerEntity player;

    private GuiButtonHooked btnDisassemble, btnAlign, btnSubmersible;

    public GuiVessel(ContainerVessel container) {
        super(container, container.player.inventory, new StringTextComponent(""));
        vessel = container.vessel;
        player = container.player;
    }

    @Override
    public void init() {
        super.init();
        buttons.clear();

        btnDisassemble = new GuiButtonHooked(guiLeft + 4, guiTop + 20, 100, 20, I18n.format(LanguageEntries.GUI_VESSELINV_DECOMPILE));
        btnDisassemble.addHook(((mX, mY) -> {
            MovingWorldClientAction.DISASSEMBLE.sendToServer(vessel);
            minecraft.displayGuiScreen(null);
        }));
        btnDisassemble.active = vessel.getDisassembler().canDisassemble(vessel.getAssemblyInteractor());
        addButton(btnDisassemble);

        btnAlign = new GuiButtonHooked(guiLeft + 4, guiTop + 40, 100, 20, I18n.format(LanguageEntries.GUI_VESSELINV_ALIGN));
        btnAlign.addHook(((mX, mY) -> {
            MovingWorldClientAction.ALIGN.sendToServer(vessel);
            vessel.alignToGrid(true);
        }));
        addButton(btnAlign);

        btnSubmersible = new GuiButtonSubmersible(guiLeft + xSize + 2, guiTop);
        btnSubmersible.addHook(((mX, mY) -> {
            if (((GuiButtonSubmersible) btnSubmersible).canDo) {
                GuiButtonSubmersible subButton = (GuiButtonSubmersible) btnSubmersible;
                new RequestSubmerseMessage(vessel, !subButton.submerse).sendToServer();
                subButton.submerse = !subButton.submerse;
            }
        }));
        ((GuiButtonSubmersible) btnSubmersible).canDo = vessel.canSubmerge();
        if (vessel.canSubmerge())
            ((GuiButtonSubmersible) btnSubmersible).submerse = vessel.getSubmerge();
        else
            ((GuiButtonSubmersible) btnSubmersible).submerse = false;
        addButton(btnSubmersible);

    }

    @Override
    public void tick() {
        if (this.minecraft != null && this.minecraft.player != null) super.tick();

        if (btnDisassemble == null || btnAlign == null) {
            return;
        }

        btnDisassemble.x = btnAlign.x = guiLeft + 4;
        int y = guiTop + 20;
        btnDisassemble.y = y;
        btnAlign.y = y += 20;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mousex, int mousey) {
        int color = 0x404040;
        int row = 8;
        int col0 = 8;

        font.drawString(I18n.format(LanguageEntries.GUI_VESSELINV_TITLE) + " - " + vessel.getInfo().getName(), col0, row, color);
        row += 5;

        font.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, color);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GlStateManager.pushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        minecraft.textureManager.bindTexture(BACKGROUND_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        blit(x, y, 0, 0, xSize, ySize);
        GlStateManager.popMatrix();
    }

    public static class GuiButtonSubmersible extends GuiButtonHooked {

        public boolean submerse = false;

        public boolean canDo = true;

        public GuiButtonSubmersible(int x, int y) {
            super(x, y, 32, 32, "");
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            Minecraft mc = Minecraft.getInstance();
            if (this.visible) {
                mc.getTextureManager().bindTexture(new ResourceLocation("davincisvessels", "textures/gui/submerse.png"));
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                boolean mouseOver = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
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

                this.blit(this.x, this.y, xOffset, yOffset, this.width, this.height);

                if (mouseOver) {
                    String message = !canDo ? "Can't Submerse" : (submerse ? "Submerse Vessel" : "Don't Submerse Vessel");
                    int stringWidth = Minecraft.getInstance().fontRenderer.getStringWidth(message);
                    drawHoveringText(Lists.newArrayList(message),
                            mouseX + (stringWidth / 2) + 32, mouseY - 12, Minecraft.getInstance().fontRenderer);
                }
            }
        }

        protected void drawHoveringText(List textLines, int x, int y, FontRenderer font) {
            if (!textLines.isEmpty()) {
                GlStateManager.disableRescaleNormal();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableLighting();
                GlStateManager.disableDepthTest();
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

                this.blitOffset = 300;
                int j1 = -267386864;
                this.blit(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
                this.blit(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
                this.blit(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
                this.blit(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
                this.blit(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
                int k1 = 1347420415;
                int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
                this.blit(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
                this.blit(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
                this.blit(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
                this.blit(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

                for (int i2 = 0; i2 < textLines.size(); ++i2) {
                    String s1 = (String) textLines.get(i2);
                    font.drawStringWithShadow(s1, j2, k2, -1);

                    if (i2 == 0) {
                        k2 += 2;
                    }

                    k2 += 10;
                }

                this.blitOffset = 0;
                GlStateManager.enableLighting();
                GlStateManager.enableDepthTest();
                RenderHelper.enableStandardItemLighting();
                GlStateManager.enableRescaleNormal();
            }
        }

    }
}
