package ckathode.archimedes.client.renderer.item;

import org.lwjgl.opengl.GL11;

import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.blockitem.TileEntityHelm;
import ckathode.archimedes.client.renderer.tilenetity.TileEntityModelHelmRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class ItemModelHelmRenderer implements IItemRenderer
{
	private final ResourceLocation	helmModelLoc;
	private final ResourceLocation	helmTexture;
	private final IModelCustom		helmModel;

	public ItemModelHelmRenderer( TileEntityModelHelmRenderer tileEntityRenderer, TileEntityHelm tileEntity )
	{
		helmModelLoc = new ResourceLocation( ArchimedesShipMod.ASSETS, "models/entity/ship_helm.obj" );
		helmTexture = new ResourceLocation( ArchimedesShipMod.ASSETS, "textures/entity/ship_helm.png" );
		helmModel = AdvancedModelLoader.loadModel( helmModelLoc );
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

		Minecraft.getMinecraft().renderEngine.bindTexture( helmTexture );
		helmModel.renderAll();

		GL11.glPopMatrix();
	}
}
