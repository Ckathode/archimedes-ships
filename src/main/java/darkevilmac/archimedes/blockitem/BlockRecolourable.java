package darkevilmac.archimedes.blockitem;

import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockRecolourable extends BlockColored {

    public BlockRecolourable(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world != null && !world.isRemote && playerIn != null && !playerIn.isSneaking()) {
            if (playerIn.getItemInUse() != null && playerIn.getItemInUse().getItem() != null && playerIn.getItemInUse().getItem() instanceof ItemDye) {
                world.setBlockState(pos, state.withProperty(BlockColored.COLOR, EnumDyeColor.byDyeDamage(playerIn.getItemInUse().getItemDamage()).getMetadata()));
            }
        }

        return false;
    }


}
