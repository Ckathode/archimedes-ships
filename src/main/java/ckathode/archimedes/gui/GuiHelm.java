package ckathode.archimedes.gui;

import java.util.Locale;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.blockitem.TileEntityHelm;
import ckathode.archimedes.chunk.AssembleResult;
import ckathode.archimedes.network.MsgClientHelmAction;
import ckathode.archimedes.network.MsgClientRenameShip;

public class GuiHelm extends GuiContainer
{
	public static final ResourceLocation	BACKGROUND_TEXTURE	= new ResourceLocation("archimedes", "textures/gui/shipstatus.png");
	
	public final TileEntityHelm				tileEntity;
	public final EntityPlayer				player;
	
	private GuiButton						btnRename, btnAssemble, btnUndo, btnMount;
	private GuiTextField					txtShipName;
	private boolean							busyCompiling;
	
	public GuiHelm(TileEntityHelm tileentity, EntityPlayer entityplayer)
	{
		super(new ContainerHelm(tileentity, entityplayer));
		tileEntity = tileentity;
		player = entityplayer;
		
		xSize = 256;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		
		Keyboard.enableRepeatEvents(true);
		
		int btnx = guiLeft - 100;
		int btny = guiTop + 20;
		buttonList.clear();
		
		btnRename = new GuiButton(4, btnx, btny, 100, 20, StatCollector.translateToLocal("gui.shipstatus.rename"));
		buttonList.add(btnRename);
		
		btnAssemble = new GuiButton(1, btnx, btny += 20, 100, 20, StatCollector.translateToLocal("gui.shipstatus.compile"));
		buttonList.add(btnAssemble);
		
		btnUndo = new GuiButton(2, btnx, btny += 20, 100, 20, StatCollector.translateToLocal("gui.shipstatus.undo"));
		btnUndo.enabled = tileEntity.getPrevAssembleResult() != null && tileEntity.getPrevAssembleResult().getCode() != AssembleResult.RESULT_NONE;
		buttonList.add(btnUndo);
		
		btnMount = new GuiButton(3, btnx, btny += 20, 100, 20, StatCollector.translateToLocal("gui.shipstatus.mount"));
		btnMount.enabled = tileEntity.getAssembleResult() != null && tileEntity.getAssembleResult().getCode() == AssembleResult.RESULT_OK;
		buttonList.add(btnMount);
		
		txtShipName = new GuiTextField(fontRendererObj, guiLeft + 8 + xSize / 2, guiTop + 21, 120, 10);
		txtShipName.setMaxStringLength(127);
		txtShipName.setEnableBackgroundDrawing(false);
		txtShipName.setVisible(true);
		txtShipName.setCanLoseFocus(false);
		txtShipName.setTextColor(0xFFFFFF);
		txtShipName.setText(tileEntity.getShipInfo().shipName);
	}
	
	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		btnUndo.enabled = tileEntity.getPrevAssembleResult() != null && tileEntity.getPrevAssembleResult().getCode() != AssembleResult.RESULT_NONE;
		btnMount.enabled = tileEntity.getAssembleResult() != null && tileEntity.getAssembleResult().getCode() == AssembleResult.RESULT_OK;
		
		btnRename.xPosition = btnAssemble.xPosition = btnUndo.xPosition = btnMount.xPosition = guiLeft - 100;
		
		int y = guiTop + 20;
		btnRename.yPosition = y;
		btnAssemble.yPosition = y += 20;
		btnUndo.yPosition = y += 20;
		btnMount.yPosition = y += 20;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mousex, int mousey)
	{
		AssembleResult result = tileEntity.getAssembleResult();
		
		int color = 0x404040;
		int row = 8;
		int col0 = 8;
		int col1 = col0 + xSize / 2;
		
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.shipstatus.title"), col0, row, color);
		row += 5;
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.shipstatus.name"), col0, row += 10, color);
		
		int rcode;
		int rblocks;
		int rballoons;
		int rtes;
		float rmass;
		
		if (result == null)
		{
			rcode = busyCompiling ? AssembleResult.RESULT_BUSY_COMPILING : AssembleResult.RESULT_NONE;
			rblocks = rballoons = rtes = 0;
			rmass = 0f;
		} else
		{
			rcode = result.getCode();
			rblocks = result.getBlockCount();
			rballoons = result.getBalloonCount();
			rtes = result.getTileEntityCount();
			rmass = result.getMass();
			if (rcode != AssembleResult.RESULT_NONE)
			{
				busyCompiling = false;
			}
		}
		
		String rcodename;
		int color1;
		switch (rcode)
		{
		case AssembleResult.RESULT_NONE:
			color1 = color;
			rcodename = "gui.shipstatus.result.none";
			break;
		case AssembleResult.RESULT_OK:
			color1 = 0x40A000;
			rcodename = "gui.shipstatus.result.ok";
			break;
		case AssembleResult.RESULT_OK_WITH_WARNINGS:
			color1 = 0xFFAA00;
			rcodename = "gui.shipstatus.result.okwarn";
			break;
		case AssembleResult.RESULT_MISSING_MARKER:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.missingmarker";
			break;
		case AssembleResult.RESULT_BLOCK_OVERFLOW:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.overflow";
			break;
		case AssembleResult.RESULT_ERROR_OCCURED:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.error";
			break;
		case AssembleResult.RESULT_BUSY_COMPILING:
			color1 = color;
			rcodename = "gui.shipstatus.result.busy";
			break;
		case AssembleResult.RESULT_INCONSISTENT:
			color1 = 0xB00000;
			rcodename = "gui.shipstatus.result.inconsistent";
			break;
		default:
			color1 = color;
			rcodename = "gui.shipstatus.result.none";
			break;
		}
		
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.shipstatus.compilerresult"), col0, row += 10, color);
		fontRendererObj.drawString(StatCollector.translateToLocal(rcodename), col1, row, color1);
		
		float balloonratio = (float) rballoons / rblocks;
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.shipstatus.shiptype"), col0, row += 10, color);
		if (rblocks == 0)
		{
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.shipstatus.type.unknown"), col1, row, color);
		} else
		{
			fontRendererObj.drawString(StatCollector.translateToLocal(balloonratio > ArchimedesShipMod.instance.modConfig.flyBalloonRatio ? "gui.shipstatus.type.airship" : "gui.shipstatus.type.boat"), col1, row, color);
		}
		
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.shipstatus.count.block"), col0, row += 10, color);
		fontRendererObj.drawString(String.valueOf(rblocks), col1, row, color);
		
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.shipstatus.count.balloon"), col0, row += 10, color);
		fontRendererObj.drawString(String.valueOf(rballoons) + " (" + (int) (balloonratio * 100f) + "%)", col1, row, color);
		
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.shipstatus.count.tileentity"), col0, row += 10, color);
		fontRendererObj.drawString(String.valueOf(rtes), col1, row, color);
		
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.shipstatus.mass"), col0, row += 10, color);
		fontRendererObj.drawString(String.format(Locale.ROOT, "%.1f %s", rmass, StatCollector.translateToLocal("gui.shipstatus.massunit")), col1, row, color);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		txtShipName.drawTextBox();
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if (button == btnRename)
		{
			if (txtShipName.isFocused())
			{
				btnRename.displayString = StatCollector.translateToLocal("gui.shipstatus.rename");
				tileEntity.getShipInfo().shipName = txtShipName.getText();
				txtShipName.setFocused(false);
				//txtShipName.setEnableBackgroundDrawing(false);
				
				MsgClientRenameShip msg = new MsgClientRenameShip(tileEntity, tileEntity.getShipInfo().shipName);
				ArchimedesShipMod.instance.pipeline.sendToServer(msg);
			} else
			{
				btnRename.displayString = StatCollector.translateToLocal("gui.shipstatus.done");
				txtShipName.setFocused(true);
				//txtShipName.setEnableBackgroundDrawing(true);
			}
		} else if (button == btnAssemble)
		{
			MsgClientHelmAction msg = new MsgClientHelmAction(tileEntity, 0);
			ArchimedesShipMod.instance.pipeline.sendToServer(msg);
			tileEntity.setAssembleResult(null);
			busyCompiling = true;
		} else if (button == btnUndo)
		{
			MsgClientHelmAction msg = new MsgClientHelmAction(tileEntity, 2);
			ArchimedesShipMod.instance.pipeline.sendToServer(msg);
		} else if (button == btnMount)
		{
			MsgClientHelmAction msg = new MsgClientHelmAction(tileEntity, 1);
			ArchimedesShipMod.instance.pipeline.sendToServer(msg);
		}
	}
	
	@Override
	protected void keyTyped(char c, int k)
	{
		if (!checkHotbarKeys(k))
		{
			if (k == Keyboard.KEY_RETURN && txtShipName.isFocused())
			{
				actionPerformed(btnRename);
			} else if (txtShipName.textboxKeyTyped(c, k))
			{} else
			{
				super.keyTyped(c, k);
			}
		}
	}
}
