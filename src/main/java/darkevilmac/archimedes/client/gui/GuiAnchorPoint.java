package darkevilmac.archimedes.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import darkevilmac.archimedes.client.LanguageEntries;
import darkevilmac.archimedes.common.object.ArchimedesObjects;
import darkevilmac.archimedes.common.tileentity.AnchorInstance;
import darkevilmac.archimedes.common.tileentity.TileEntityAnchorPoint;


public class GuiAnchorPoint extends GuiContainer {

    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("archimedesshipsplus", "textures/gui/anchorPoint.png");

    private GuiButton btnLink, btnSwitch;
    private TileEntityAnchorPoint anchorPoint;

    public GuiAnchorPoint(TileEntityAnchorPoint te, EntityPlayer entityplayer) {
        super(new ContainerAnchorPoint(te, entityplayer));
        this.anchorPoint = te;

        xSize = 256;
        ySize = 206;
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.clear();
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        int linkWidth = fontRenderer.getStringWidth(I18n.format(LanguageEntries.GUI_ANCHOR_LINK)) + 6;
        int switchWidth = fontRenderer.getStringWidth(I18n.format(LanguageEntries.GUI_ANCHOR_SWITCH)) + 6;
        int width = linkWidth > switchWidth ? linkWidth : switchWidth;

        int linkX = guiLeft + 83;
        int linkY = guiTop + 79;

        btnLink = new GuiButton(1, linkX, linkY,
                width, 20, I18n.format(LanguageEntries.GUI_ANCHOR_LINK));

        int switchX = guiLeft + 149;
        int switchY = guiTop + 79;

        btnSwitch = new GuiButton(2, switchX, switchY,
                width, 20, I18n.format(LanguageEntries.GUI_ANCHOR_SWITCH));
        buttonList.add(btnLink);
        buttonList.add(btnSwitch);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(3, 0, 1, 0);
        GlStateManager.scale(2.75, 2.75, 2.75);
        GlStateManager.translate(-.5, 6, 0);
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemIntoGUI(new ItemStack(ArchimedesObjects.blockAnchorPoint, 1), 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        fontRendererObj.drawString(I18n.format(LanguageEntries.GUI_ANCHOR_POS, anchorPoint.getPos()
                .toString().substring(9).replace("}", "").replaceAll("=", ":")), 64, 30, 0);
        fontRendererObj.drawString(I18n.format(LanguageEntries.GUI_ANCHOR_TYPE, anchorPoint.instance.getType()
                .toString()), 64, 42, 0);
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
        if (button == btnLink) {

        } else if (button == btnSwitch) {

        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        btnLink.enabled = anchorPoint.item != null && anchorPoint.instance.getType() == AnchorInstance.InstanceType.FORLAND;
    }

}
