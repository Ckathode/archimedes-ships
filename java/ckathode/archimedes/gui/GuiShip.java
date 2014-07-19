package ckathode.archimedes.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.entity.EntityShip;
import ckathode.archimedes.network.MsgClientShipAction;

public class GuiShip extends GuiContainer
{
	public static final ResourceLocation	BACKGROUND_TEXTURE	= new ResourceLocation("archimedes", "textures/gui/shipinv.png");
	
	public final EntityShip					ship;
	public final EntityPlayer				player;
	
	private GuiButton						btnDisassemble, btnAlign;
	
	public GuiShip(EntityShip entityship, EntityPlayer entityplayer)
	{
		super(new ContainerShip(entityship, entityplayer));
		ship = entityship;
		player = entityplayer;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		buttonList.clear();
		
		btnDisassemble = new GuiButton(1, guiLeft + 4, guiTop + 20, 100, 20, StatCollector.translateToLocal("gui.shipinv.decompile"));
		btnDisassemble.enabled = ship.getDisassembler().canDisassemble();
		buttonList.add(btnDisassemble);
		
		btnAlign = new GuiButton(2, guiLeft + 4, guiTop + 40, 100, 20, StatCollector.translateToLocal("gui.shipinv.align"));
		buttonList.add(btnAlign);
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		
		btnDisassemble.xPosition = btnAlign.xPosition = guiLeft + 4;
		int y = guiTop + 20;
		btnDisassemble.yPosition = y;
		btnAlign.yPosition = y += 20;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mousex, int mousey)
	{
		int color = 0x404040;
		int row = 8;
		int col0 = 8;
		
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.shipinv.title") + " - " + ship.getInfo().shipName, col0, row, color);
		row += 5;
		
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, color);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button == btnDisassemble)
		{
			MsgClientShipAction msg = new MsgClientShipAction(ship, 1);
			ArchimedesShipMod.instance.pipeline.sendToServer(msg);
			mc.displayGuiScreen(null);
		} else if (button == btnAlign)
		{
			MsgClientShipAction msg = new MsgClientShipAction(ship, 3);
			ArchimedesShipMod.instance.pipeline.sendToServer(msg);
			
			ship.alignToGrid();
		}
	}
}
