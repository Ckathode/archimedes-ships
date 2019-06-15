package com.tridevmc.davincisvessels.client.gui;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.LanguageEntries;
import com.tridevmc.davincisvessels.common.entity.ShipAssemblyInteractor;
import com.tridevmc.davincisvessels.common.network.HelmClientAction;
import com.tridevmc.davincisvessels.common.network.message.HelmActionMessage;
import com.tridevmc.davincisvessels.common.network.message.RenameShipMessage;
import com.tridevmc.davincisvessels.common.tileentity.TileHelm;
import com.tridevmc.movingworld.common.chunk.assembly.AssembleResult;
import com.tridevmc.movingworld.common.chunk.assembly.AssembleResult.ResultType;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.Locale;

import static com.tridevmc.movingworld.common.chunk.assembly.AssembleResult.ResultType.*;

public class GuiHelm extends ContainerScreen {
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("davincisvessels", "textures/gui/shipstatus.png");

    public final TileHelm helm;
    public final PlayerEntity player;

    private GuiButtonHooked btnRename, btnAssemble, btnUndo, btnMount;
    private TextFieldWidget txtShipName;
    private boolean busyCompiling;

    public GuiHelm(ContainerHelm container) {
        super(container, container.player.inventory, new StringTextComponent(""));
        helm = container.helm;
        player = container.player;

        xSize = 256;
        ySize = 256;
    }

    @Override
    public void init() {
        super.init();

        //Keyboard.enableRepeatEvents(true);

        int btnx = guiLeft - 100;
        int btny = guiTop + 20;
        buttons.clear();

        btnRename = new GuiButtonHooked(btnx, btny, 100, 20, I18n.format(LanguageEntries.GUI_STATUS_RENAME));
        btnRename.addHook((mX, mY) -> {
            if (txtShipName.isFocused()) {
                btnRename.setMessage(I18n.format(LanguageEntries.GUI_STATUS_RENAME));
                helm.getInfo().setName(txtShipName.getText());
                txtShipName.changeFocus(false);
                new RenameShipMessage(helm, helm.getInfo().getName()).sendToServer();
            } else {
                btnRename.setMessage(I18n.format(LanguageEntries.GUI_STATUS_DONE));
                txtShipName.changeFocus(true);
            }
        });
        addButton(btnRename);

        btnAssemble = new GuiButtonHooked(btnx, btny += 20, 100, 20, I18n.format(LanguageEntries.GUI_STATUS_COMPILE));
        btnAssemble.addHook(((mX, mY) -> {
            new HelmActionMessage(helm, HelmClientAction.ASSEMBLE).sendToServer();
            helm.setAssembleResult(null);
            busyCompiling = true;
        }));
        addButton(btnAssemble);

        btnUndo = new GuiButtonHooked(btnx, btny += 20, 100, 20, I18n.format(LanguageEntries.GUI_STATUS_UNDO));
        btnUndo.active = helm.getPrevAssembleResult() != null && helm.getPrevAssembleResult().getType() != RESULT_NONE;
        btnUndo.addHook((mX, mY) -> new HelmActionMessage(helm, HelmClientAction.UNDOCOMPILE).sendToServer());
        addButton(btnUndo);

        btnMount = new GuiButtonHooked(btnx, btny += 20, 100, 20, I18n.format(LanguageEntries.GUI_STATUS_MOUNT));
        btnMount.active = helm.getAssembleResult() != null && helm.getAssembleResult().getType() == RESULT_OK;
        btnMount.addHook((mX, mY) -> new HelmActionMessage(helm, HelmClientAction.MOUNT).sendToServer());
        addButton(btnMount);

        txtShipName = new TextFieldWidget(font, guiLeft + 8 + xSize / 2, guiTop + 21, 120, 10, "");
        txtShipName.setMaxStringLength(127);
        txtShipName.setEnableBackgroundDrawing(false);
        txtShipName.setVisible(true);
        txtShipName.setCanLoseFocus(false);
        txtShipName.setTextColor(0xFFFFFF);
        txtShipName.setText(helm.getInfo().getName());
    }


    @Override
    public void tick() {
        super.tick();
        btnUndo.active = helm.getPrevAssembleResult() != null && helm.getPrevAssembleResult().getType() != RESULT_NONE;
        btnMount.active = helm.getAssembleResult() != null && helm.getAssembleResult().getType() == RESULT_OK;

        btnRename.x = btnAssemble.x = btnUndo.x = btnMount.x = guiLeft - 100;

        int y = guiTop + 20;
        btnRename.y = y;
        btnAssemble.y = y += 20;
        btnUndo.y = y += 20;
        btnMount.y = y += 20;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mousex, int mousey) {
        AssembleResult result = helm.getAssembleResult();

        int colorTitle = 0x404040;
        int row = 8;
        int col0 = 8;
        int col1 = col0 + xSize / 2;

        font.drawString(I18n.format(LanguageEntries.GUI_STATUS_TITLE), col0, row, colorTitle);
        row += 5;
        font.drawString(I18n.format(LanguageEntries.GUI_STATUS_NAME), col0, row += 10, colorTitle);

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

        String resultName;
        int valueColor;
        switch (rType) {
            case RESULT_NONE:
                valueColor = colorTitle;
                resultName = LanguageEntries.GUI_STATUS_RESULT_NONE;
                break;
            case RESULT_OK:
                valueColor = 0x40A000;
                resultName = LanguageEntries.GUI_STATUS_RESULT_OKAY;
                break;
            case RESULT_OK_WITH_WARNINGS:
                valueColor = 0xFFAA00;
                resultName = LanguageEntries.GUI_STATUS_RESULT_OKAYWARN;
                break;
            case RESULT_MISSING_MARKER:
                valueColor = 0xB00000;
                resultName = LanguageEntries.GUI_STATUS_RESULT_MISSINGMARKER;
                break;
            case RESULT_BLOCK_OVERFLOW:
                valueColor = 0xB00000;
                resultName = LanguageEntries.GUI_STATUS_RESULT_OVERFLOW;
                break;
            case RESULT_ERROR_OCCURED:
                valueColor = 0xB00000;
                resultName = LanguageEntries.GUI_STATUS_RESULT_ERROR;
                break;
            case RESULT_BUSY_COMPILING:
                valueColor = colorTitle;
                resultName = LanguageEntries.GUI_STATUS_RESULT_BUSY;
                break;
            case RESULT_INCONSISTENT:
                valueColor = 0xB00000;
                resultName = LanguageEntries.GUI_STATUS_RESULT_INCONSISTENT;
                break;
            default:
                valueColor = colorTitle;
                resultName = LanguageEntries.GUI_STATUS_RESULT_NONE;
                break;
        }

        font.drawString(I18n.format(LanguageEntries.GUI_STATUS_COMPILERESULT), col0, row += 10, colorTitle);
        font.drawString(I18n.format(resultName), col1, row, valueColor);

        float balloonratio = (float) rballoons / rblocks;
        font.drawString(I18n.format(LanguageEntries.GUI_STATUS_SHIPTYPE), col0, row += 10, colorTitle);
        if (rblocks == 0) {
            font.drawString(I18n.format(LanguageEntries.GUI_STATUS_TYPEUNKNOWN), col1, row, colorTitle);
        } else {
            font.drawString(I18n.format(balloonratio > DavincisVesselsMod.CONFIG.flyBalloonRatio ? LanguageEntries.GUI_STATUS_TYPEAIRSHIP : LanguageEntries.GUI_STATUS_TYPEBOAT), col1, row, colorTitle);
        }

        font.drawString(I18n.format(LanguageEntries.GUI_STATUS_COUNTBLOCK), col0, row += 10, colorTitle);
        font.drawString(String.valueOf(rblocks), col1, row, colorTitle);

        font.drawString(I18n.format(LanguageEntries.GUI_STATUS_COUNTBALLOON), col0, row += 10, colorTitle);
        font.drawString(String.valueOf(rballoons) + " (" + (int) (balloonratio * 100f) + "%)", col1, row, colorTitle);

        font.drawString(I18n.format(LanguageEntries.GUI_STATUS_COUNTTILE), col0, row += 10, colorTitle);
        font.drawString(String.valueOf(rtes), col1, row, colorTitle);

        font.drawString(I18n.format(LanguageEntries.GUI_STATUS_MASS), col0, row += 10, colorTitle);
        font.drawString(String.format(Locale.ROOT, "%.1f %s", rmass, I18n.format(LanguageEntries.GUI_STATUS_MASSUNIT)), col1, row, colorTitle);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mX, int mY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        minecraft.textureManager.bindTexture(BACKGROUND_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        blit(x, y, 0, 0, xSize, ySize);

        txtShipName.render(mX, mY, partialTicks);
    }

    @Override
    public boolean charTyped(char c, int k) {
        if (!super.charTyped(c, k)) {
            if (k == 28 && txtShipName.isFocused()) {
                btnRename.onClick(0, 0);
                return true;
            }
        }
        return false;
    }
}
