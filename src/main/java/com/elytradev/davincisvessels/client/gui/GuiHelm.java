package com.elytradev.davincisvessels.client.gui;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.LanguageEntries;
import com.elytradev.davincisvessels.common.entity.ShipAssemblyInteractor;
import com.elytradev.davincisvessels.common.network.HelmClientAction;
import com.elytradev.davincisvessels.common.network.message.HelmActionMessage;
import com.elytradev.davincisvessels.common.network.message.RenameShipMessage;
import com.elytradev.davincisvessels.common.tileentity.TileHelm;
import com.elytradev.movingworld.common.chunk.assembly.AssembleResult;
import com.elytradev.movingworld.common.chunk.assembly.AssembleResult.ResultType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Locale;

import static com.elytradev.movingworld.common.chunk.assembly.AssembleResult.ResultType.*;

public class GuiHelm extends GuiContainer {
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("davincisvessels", "textures/gui/shipstatus.png");

    public final TileHelm tileEntity;
    public final EntityPlayer player;

    private GuiButton btnRename, btnAssemble, btnUndo, btnMount;
    private GuiTextField txtShipName;
    private boolean busyCompiling;

    public GuiHelm(TileHelm tileentity, EntityPlayer entityplayer) {
        super(new ContainerHelm(tileentity, entityplayer));
        tileEntity = tileentity;
        player = entityplayer;

        xSize = 256;
        ySize = 256;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);

        int btnx = guiLeft - 100;
        int btny = guiTop + 20;
        buttonList.clear();

        btnRename = new GuiButton(4, btnx, btny, 100, 20, I18n.format(LanguageEntries.GUI_STATUS_RENAME));
        buttonList.add(btnRename);

        btnAssemble = new GuiButton(1, btnx, btny += 20, 100, 20, I18n.format(LanguageEntries.GUI_STATUS_COMPILE));
        buttonList.add(btnAssemble);

        btnUndo = new GuiButton(2, btnx, btny += 20, 100, 20, I18n.format(LanguageEntries.GUI_STATUS_UNDO));
        btnUndo.enabled = tileEntity.getPrevAssembleResult() != null && tileEntity.getPrevAssembleResult().getType() != RESULT_NONE;
        buttonList.add(btnUndo);

        btnMount = new GuiButton(3, btnx, btny += 20, 100, 20, I18n.format(LanguageEntries.GUI_STATUS_MOUNT));
        btnMount.enabled = tileEntity.getAssembleResult() != null && tileEntity.getAssembleResult().getType() == RESULT_OK;
        buttonList.add(btnMount);

        txtShipName = new GuiTextField(0, fontRenderer, guiLeft + 8 + xSize / 2, guiTop + 21, 120, 10); // TODO: Might be incorrect not sure about 0 in GuiTextField()
        txtShipName.setMaxStringLength(127);
        txtShipName.setEnableBackgroundDrawing(false);
        txtShipName.setVisible(true);
        txtShipName.setCanLoseFocus(false);
        txtShipName.setTextColor(0xFFFFFF);
        txtShipName.setText(tileEntity.getInfo().getName());
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        btnUndo.enabled = tileEntity.getPrevAssembleResult() != null && tileEntity.getPrevAssembleResult().getType() != RESULT_NONE;
        btnMount.enabled = tileEntity.getAssembleResult() != null && tileEntity.getAssembleResult().getType() == RESULT_OK;

        btnRename.x = btnAssemble.x = btnUndo.x = btnMount.x = guiLeft - 100;

        int y = guiTop + 20;
        btnRename.y = y;
        btnAssemble.y = y += 20;
        btnUndo.y = y += 20;
        btnMount.y = y += 20;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mousex, int mousey) {
        AssembleResult result = tileEntity.getAssembleResult();

        int colorTitle = 0x404040;
        int row = 8;
        int col0 = 8;
        int col1 = col0 + xSize / 2;

        fontRenderer.drawString(I18n.format(LanguageEntries.GUI_STATUS_TITLE), col0, row, colorTitle);
        row += 5;
        fontRenderer.drawString(I18n.format(LanguageEntries.GUI_STATUS_NAME), col0, row += 10, colorTitle);

        ResultType rType;
        int rblocks;
        int rballoons;
        int rtes;
        float rmass;

        if (result == null || (result != null && result.assemblyInteractor == null) || (result != null && result.assemblyInteractor != null && !(result.assemblyInteractor instanceof ShipAssemblyInteractor))) {
            rType = busyCompiling ? RESULT_BUSY_COMPILING : RESULT_NONE;
            rblocks = rballoons = rtes = 0;
            rmass = 0f;
        } else {
            rType = result.getType();
            rblocks = result.getBlockCount();
            rballoons = ((ShipAssemblyInteractor) result.assemblyInteractor).getBalloonCount();
            rtes = result.getTileEntityCount();
            rmass = result.getMass();
            if (rType != RESULT_NONE) {
                busyCompiling = false;
            }
        }

        String rcodename;
        int valueColor;
        switch (rType) {
            case RESULT_NONE:
                valueColor = colorTitle;
                rcodename = LanguageEntries.GUI_STATUS_RESULT_NONE;
                break;
            case RESULT_OK:
                valueColor = 0x40A000;
                rcodename = LanguageEntries.GUI_STATUS_RESULT_OKAY;
                break;
            case RESULT_OK_WITH_WARNINGS:
                valueColor = 0xFFAA00;
                rcodename = LanguageEntries.GUI_STATUS_RESULT_OKAYWARN;
                break;
            case RESULT_MISSING_MARKER:
                valueColor = 0xB00000;
                rcodename = LanguageEntries.GUI_STATUS_RESULT_MISSINGMARKER;
                break;
            case RESULT_BLOCK_OVERFLOW:
                valueColor = 0xB00000;
                rcodename = LanguageEntries.GUI_STATUS_RESULT_OVERFLOW;
                break;
            case RESULT_ERROR_OCCURED:
                valueColor = 0xB00000;
                rcodename = LanguageEntries.GUI_STATUS_RESULT_ERROR;
                break;
            case RESULT_BUSY_COMPILING:
                valueColor = colorTitle;
                rcodename = LanguageEntries.GUI_STATUS_RESULT_BUSY;
                break;
            case RESULT_INCONSISTENT:
                valueColor = 0xB00000;
                rcodename = LanguageEntries.GUI_STATUS_RESULT_INCONSISTENT;
                break;
            default:
                valueColor = colorTitle;
                rcodename = LanguageEntries.GUI_STATUS_RESULT_NONE;
                break;
        }

        fontRenderer.drawString(I18n.format(LanguageEntries.GUI_STATUS_COMPILERESULT), col0, row += 10, colorTitle);
        fontRenderer.drawString(I18n.format(rcodename), col1, row, valueColor);

        float balloonratio = (float) rballoons / rblocks;
        fontRenderer.drawString(I18n.format(LanguageEntries.GUI_STATUS_SHIPTYPE), col0, row += 10, colorTitle);
        if (rblocks == 0) {
            fontRenderer.drawString(I18n.format(LanguageEntries.GUI_STATUS_TYPEUNKNOWN), col1, row, colorTitle);
        } else {
            fontRenderer.drawString(I18n.format(balloonratio > DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().flyBalloonRatio ? LanguageEntries.GUI_STATUS_TYPEAIRSHIP : LanguageEntries.GUI_STATUS_TYPEBOAT), col1, row, colorTitle);
        }

        fontRenderer.drawString(I18n.format(LanguageEntries.GUI_STATUS_COUNTBLOCK), col0, row += 10, colorTitle);
        fontRenderer.drawString(String.valueOf(rblocks), col1, row, colorTitle);

        fontRenderer.drawString(I18n.format(LanguageEntries.GUI_STATUS_COUNTBALLOON), col0, row += 10, colorTitle);
        fontRenderer.drawString(String.valueOf(rballoons) + " (" + (int) (balloonratio * 100f) + "%)", col1, row, colorTitle);

        fontRenderer.drawString(I18n.format(LanguageEntries.GUI_STATUS_COUNTTILE), col0, row += 10, colorTitle);
        fontRenderer.drawString(String.valueOf(rtes), col1, row, colorTitle);

        fontRenderer.drawString(I18n.format(LanguageEntries.GUI_STATUS_MASS), col0, row += 10, colorTitle);
        fontRenderer.drawString(String.format(Locale.ROOT, "%.1f %s", rmass, I18n.format(LanguageEntries.GUI_STATUS_MASSUNIT)), col1, row, colorTitle);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        txtShipName.drawTextBox();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button == btnRename) {
            if (txtShipName.isFocused()) {
                btnRename.displayString = I18n.format(LanguageEntries.GUI_STATUS_RENAME);
                tileEntity.getInfo().setName(txtShipName.getText());
                txtShipName.setFocused(false);
                new RenameShipMessage(tileEntity, tileEntity.getInfo().getName()).sendToServer();
            } else {
                btnRename.displayString = I18n.format(LanguageEntries.GUI_STATUS_DONE);
                txtShipName.setFocused(true);
            }
        } else if (button == btnAssemble) {
            new HelmActionMessage(tileEntity, HelmClientAction.ASSEMBLE).sendToServer();
            tileEntity.setAssembleResult(null);
            busyCompiling = true;
        } else if (button == btnMount) {
            new HelmActionMessage(tileEntity, HelmClientAction.MOUNT).sendToServer();
        } else if (button == btnUndo) {
            new HelmActionMessage(tileEntity, HelmClientAction.UNDOCOMPILE).sendToServer();
        }
    }

    @Override
    protected void keyTyped(char c, int k) {
        if (!checkHotbarKeys(k)) {
            if (k == Keyboard.KEY_RETURN && txtShipName.isFocused()) {
                actionPerformed(btnRename);
            } else if (txtShipName.textboxKeyTyped(c, k)) {
            } else {
                try {
                    super.keyTyped(c, k);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
