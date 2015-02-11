package ckathode.archimedes.client.renderer.item;

import org.lwjgl.opengl.GL11;

import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.blockitem.TileEntityEngine;
import ckathode.archimedes.client.renderer.tilenetity.TileEntityModelEngineRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class ItemModelEngineRenderer implements IItemRenderer
{
	private final ResourceLocation	engineModelLoc;
	private final ResourceLocation	engineTexture;
	private final IModelCustom		engineModel;

	public ItemModelEngineRenderer( TileEntityModelEngineRenderer tileEntityModelEngineRenderer, TileEntityEngine tileEntityEngine )
	{
		engineModelLoc = new ResourceLocation( ArchimedesShipMod.ASSETS, "models/entity/ship_engine.obj" );
		engineTexture = new ResourceLocation( ArchimedesShipMod.ASSETS, "textures/entity/ship_engine.png" );
		engineModel = AdvancedModelLoader.loadModel( engineModelLoc );
	}

	@Override
	public boolean handleRenderType( ItemStack item, ItemRenderType type )
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper( ItemRenderType type, ItemStack item, ItemRendererHelper helper )
	{
		return true;
	}

	@Override
	public void renderItem( ItemRenderType type, ItemStack item, Object... data )
	{
		GL11.glPushMatrix();
		GL11.glRotatef( 180f, 0F, 1F, 0F );

		Minecraft.getMinecraft().renderEngine.bindTexture( engineTexture );
		engineModel.renderAll();

		GL11.glPopMatrix();
	}
}
