package darkevilmac.archimedes.blockitem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import darkevilmac.archimedes.ArchimedesShipMod;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockEngine extends BlockContainer {
    public float enginePower;
    public int engineFuelConsumption;

    private IIcon frontIcon, backIcon;

    public BlockEngine(Material material, float power, int fuelconsumption) {
        super(material);
        enginePower = power;
        engineFuelConsumption = fuelconsumption;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1 || side == 0)
            return this.backIcon;
        if (side == meta)
            return this.frontIcon;

        if (ForgeDirection.getOrientation(side).getOpposite().ordinal() == meta) {
            return this.backIcon;
        }

        return this.blockIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        super.registerBlockIcons(reg);
        frontIcon = reg.registerIcon(getTextureName() + "_front");
        backIcon = reg.registerIcon(getTextureName() + "_back");
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileEntityEngine(enginePower, engineFuelConsumption);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
        if (!player.isSneaking()) {
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if (tileentity != null) {
                player.openGui(ArchimedesShipMod.instance, 3, world, x, y, z);
                return true;
            }
        }
        return false;
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int l = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }

        if (l == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5, 2);
        }

        if (l == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }

        if (l == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4, 2);
        }
    }
}
