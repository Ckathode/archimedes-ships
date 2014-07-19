package ckathode.archimedes.blockitem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import ckathode.archimedes.ArchimedesShipMod;
import ckathode.archimedes.entity.EntityEntityAttachment;
import ckathode.archimedes.entity.EntityParachute;
import ckathode.archimedes.entity.EntityShip;
import ckathode.archimedes.util.RotationHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockHelm extends BlockDirectional implements IEntitySelector, ITileEntityProvider
{
	private IIcon	frontIcon;
	
	public BlockHelm()
	{
		super(Material.wood);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		meta &= 3;
		if (side == 2)
		{
			return meta == 0 ? frontIcon : blockIcon;
		} else if (side == 3)
		{
			return meta == 2 ? frontIcon : blockIcon;
		} else if (side == 4)
		{
			return meta == 3 ? frontIcon : blockIcon;
		} else if (side == 5)
		{
			return meta == 1 ? frontIcon : blockIcon;
		}
		return blockIcon;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister reg)
	{
		super.registerBlockIcons(reg);
		frontIcon = reg.registerIcon(getTextureName() + "_front");
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemstack)
	{
		int dir = Math.round(entityliving.rotationYaw / 90F) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 3);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are)
	{
		if (!player.isSneaking())
		{
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity != null)
			{
				player.openGui(ArchimedesShipMod.instance, 1, world, x, y, z);
				return true;
			}
		}
		return false;
	}
	
	/*
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
	{
		if (world.isRemote)
		{
			return true;
		}
		
		ChunkBuilder builder = new ChunkBuilder(world, x, y, z);
		builder.doFilling();
		if (builder.getResult() == ChunkBuilder.RESULT_OK)
		{
			EntityShip entity = builder.getEntity(world);
			if (entity != null)
			{
				if (world.spawnEntityInWorld(entity))
				{
					@SuppressWarnings("unchecked")
					List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(entity, entity.getBoundingBox(), this);
					for (Entity e : list)
					{
						EntityEntityAttachment a = new EntityEntityAttachment(world, MathHelper.floor_double(e.posX) - builder.xOffset, MathHelper.floor_double(e.posY) - builder.yOffset, MathHelper.floor_double(e.posZ) - builder.zOffset);
						e.mountEntity(a);
						ShipMod.modLog.info("Adding attachment at: " + a.getChunkPosition().chunkPosX + ", " + a.getChunkPosition().chunkPosY + ", " + a.getChunkPosition().chunkPosZ);
						entity.getCapabilities().addAttachments(a);
					}
					entity.getCapabilities().spawnSeatEntities();
					entityplayer.mountEntity(entity);
					//entity.getCapabilities().mountEntity(entityplayer);
					return true;
				}
			}
		}// !NOT! else if
		if (builder.getResult() == ChunkBuilder.RESULT_BLOCK_OVERFLOW)
		{
			ChatComponentText c = new ChatComponentText("Cannot create vehicle with more than " + ShipMod.instance.modConfig.maxShipChunkBlocks + " blocks");
			((EntityPlayerMP) entityplayer).addChatMessage(c);
		} else if (builder.getResult() == ChunkBuilder.RESULT_MISSING_MARKER)
		{
			ChatComponentText c = new ChatComponentText("Cannot create vehicle with no vehicle marker");
			((EntityPlayerMP) entityplayer).addChatMessage(c);
		} else if (builder.getResult() == ChunkBuilder.RESULT_ERROR_OCCURED)
		{
			ChatComponentText c = new ChatComponentText("An error occured while compiling ship. See console log for details.");
			((EntityPlayerMP) entityplayer).addChatMessage(c);
		}
		return false;
	}
	*/
	
	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis)
	{
		return RotationHelper.rotateArchimedesBlock(this, world, x, y, z, axis);
	}
	
	@Override
	public boolean isEntityApplicable(Entity entity)
	{
		return !(entity instanceof EntityLivingBase) && !(entity instanceof EntityShip) && !(entity instanceof EntityEntityAttachment) && !(entity instanceof EntityParachute);
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TileEntityHelm();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		super.breakBlock(world, x, y, z, block, meta);
		world.removeTileEntity(x, y, z);
	}
	
	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int p_149696_5_, int p_149696_6_)
	{
		super.onBlockEventReceived(world, x, y, z, p_149696_5_, p_149696_6_);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		return tileentity != null && tileentity.receiveClientEvent(p_149696_5_, p_149696_6_);
	}
}
