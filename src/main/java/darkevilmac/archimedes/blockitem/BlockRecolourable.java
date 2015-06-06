package darkevilmac.archimedes.blockitem;

import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockRecolourable extends BlockColored {

    public BlockRecolourable(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world != null && playerIn != null && !playerIn.isSneaking()) {
            IBlockState blockState = world.getBlockState(pos);
            ItemStack heldItem = playerIn.getHeldItem();
            if (blockState == null || heldItem == null) return false;

            if (heldItem.getItem() != null && heldItem.getItem() instanceof ItemDye) {
                if (EnumDyeColor.byDyeDamage(heldItem.getItemDamage()).getMapColor() != ((EnumDyeColor) blockState.getValue(COLOR)).getMapColor()) {
                    if (!world.isRemote) {
                        blockState = blockState.withProperty(BlockColored.COLOR, EnumDyeColor.byDyeDamage(playerIn.getHeldItem().getItemDamage()));
                        world.setBlockState(pos, blockState);
                        if (!playerIn.capabilities.isCreativeMode)
                            playerIn.getHeldItem().stackSize -= 1;
                    } else {
                        playerIn.swingItem();
                    }
                    return true;
                }
            }
        }

        return false;
    }


}
