package ckathode.archimedes.client.renderer.tilenetity;

import org.lwjgl.opengl.GL11;

import ckathode.archimedes.ArchimedesShipMod;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

/*
 * Render an OBJ model as a tile entity.
 * 
 * This model is a variation of a model I supplied
 * some time ago, but since then I have learned a
 * lot more, and realised that the original model
 * was not formatted properly for Minecraft.
 * 
 * This class loads the model and renders it in
 * place of the normal block.
 */
public class TileEntityModelHelmRenderer extends TileEntitySpecialRenderer
{
	private final ResourceLocation	helmModelLoc;
	private final ResourceLocation	helmTexture;
	private final IModelCustom		helmModel;

	public TileEntityModelHelmRenderer()
	{
		helmModelLoc = new ResourceLocation( ArchimedesShipMod.ASSETS, "models/entity/ship_helm.obj" );
		helmTexture = new ResourceLocation( ArchimedesShipMod.ASSETS, "textures/entity/ship_helm.png" );
		helmModel = AdvancedModelLoader.loadModel( helmModelLoc );
	}

	@Override
	public void renderTileEntityAt( TileEntity tileEntity, double x, double y, double z, float f )
	{
		GL11.glPushMatrix();
		GL11.glTranslated( x, y, z );

		// Translate to centre of block
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		
		// Rotate model according to metadata
		float rotate = 0F;
		if( tileEntity.blockMetadata == 1 ) rotate = -90f;
		if( tileEntity.blockMetadata == 2 ) rotate = 180f;
		if( tileEntity.blockMetadata == 3 ) rotate = 90f;

		GL11.glRotatef( rotate, 0F, 1F, 0F);

		// Bind the texture and render the model
		bindTexture( helmTexture );
		helmModel.renderAll();

		GL11.glPopMatrix();
	}

}
