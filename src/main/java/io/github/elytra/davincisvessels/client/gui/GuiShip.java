package io.github.elytra.davincisvessels.client.gui;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.List;

import io.github.elytra.davincisvessels.common.LanguageEntries;
import io.github.elytra.davincisvessels.common.entity.EntityShip;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;
import io.github.elytra.movingworld.common.network.MovingWorldClientAction;
import io.github.elytra.movingworld.common.network.MovingWorldNetworking;

public class GuiShip extends GuiContainer {
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("davincisvessels", "textures/gui/shipinv.png");

    public final EntityShip ship;
    public final EntityPlayer player;

    private GuiButton btnDisassemble, btnAlign, btnSubmersible;

    public GuiShip(EntityShip entityship, EntityPlayer entityplayer) {
        super(new ContainerShip(entityship, entityplayer));
        ship = entityship;
        player = entityplayer;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        btnDisassemble = new GuiButton(1, guiLeft + 4, guiTop + 20, 100, 20, I18n.format(LanguageEntries.GUI_SHIPINV_DECOMPILE));
        btnDisassemble.enabled = ship.getDisassembler().canDisassemble(ship.getAssemblyInteractor());
        buttonList.add(btnDisassemble);

        btnAlign = new GuiButton(2, guiLeft + 4, guiTop + 40, 100, 20, I18n.format(LanguageEntries.GUI_SHIPINV_ALIGN));
        buttonList.add(btnAlign);


        btnSubmersible = new GuiButtonSubmersible(3, guiLeft + xSize + 2, guiTop);
        ((GuiButtonSubmersible) btnSubmersible).canDo = ship.canSubmerge();
        if (ship.canSubmerge())
            ((GuiButtonSubmersible) btnSubmersible).submerse = ship.getSubmerge();
        else
            ((GuiButtonSubmersible) btnSubmersible).submerse = false;
        buttonList.add(btnSubmersible);

    }

    @Override
    public void updateScreen() {
        if (this.mc != null && this.mc.thePlayer != null) super.updateScreen();

        if (btnDisassemble == null || btnAlign == null) {
            return;
        }

        btnDisassemble.xPosition = btnAlign.xPosition = guiLeft + 4;
        int y = guiTop + 20;
        btnDisassemble.yPosition = y;
        btnAlign.yPosition = y += 20;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mousex, int mousey) {
        int color = 0x404040;
        int row = 8;
        int col0 = 8;

        fontRendererObj.drawString(I18n.format(LanguageEntries.GUI_SHIPINV_TITLE) + " - " + ship.getInfo().getName(), col0, row, color);
        row += 5;

        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, color);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == btnDisassemble) {
            MovingWorldNetworking.NETWORK.send().packet("MovingWorldClientActionMessage")
                    .with("dimID", ship.worldObj.provider.getDimension())
                    .with("entityID", ship.getEntityId())
                    .with("action", MovingWorldClientAction.DISASSEMBLE.toByte())
                    .toServer();
            mc.displayGuiScreen(null);
        } else if (button == btnAlign) {
            MovingWorldNetworking.NETWORK.send().packet("MovingWorldClientActionMessage")
                    .with("dimID", ship.worldObj.provider.getDimension())
                    .with("entityID", ship.getEntityId())
                    .with("action", MovingWorldClientAction.ALIGN.toByte())
                    .toServer();

            ship.alignToGrid(true);
        } else if (button == btnSubmersible && ((GuiButtonSubmersible) btnSubmersible).canDo) {
            GuiButtonSubmersible subButton = (GuiButtonSubmersible) button;

            DavincisVesselsNetworking.NETWORK.send().packet("ClientRequestSubmerseMessage")
                    .with("dimID", ship.worldObj.provider.getDimension())
                    .with("entityID", ship.getEntityId())
                    .with("submerse", !subButton.submerse).toServer();

            subButton.submerse = !subButton.submerse;
        }
    }

    public static class GuiButtonSubmersible extends GuiButton {

        public boolean submerse = false;

        public boolean canDo = true;

        public GuiButtonSubmersible(int buttonId, int x, int y) {
            super(buttonId, x, y, 32, 32, "");
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                mc.getTextureManager().bindTexture(new ResourceLocation("davincisvessels", "textures/gui/submerse.png"));
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
}
