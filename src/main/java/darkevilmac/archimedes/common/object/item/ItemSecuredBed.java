package darkevilmac.archimedes.common.object.item;


import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.object.ArchimedesObjects;
import darkevilmac.archimedes.common.object.block.BlockSecuredBed;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSecuredBed extends Item {

    public ItemSecuredBed() {
        this.setCreativeTab(ArchimedesShipMod.creativeTab);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *
     * @param pos  The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else if (side != EnumFacing.UP) {
            return false;
        } else {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            boolean flag = block.isReplaceable(worldIn, pos);

            if (!flag) {
                pos = pos.up();
            }

            int i = MathHelper.floor_double((double) (playerIn.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            EnumFacing enumfacing1 = EnumFacing.getHorizontal(i);
            BlockPos blockpos1 = pos.offset(enumfacing1);
            boolean flag1 = block.isReplaceable(worldIn, blockpos1);
            boolean flag2 = worldIn.isAirBlock(pos) || flag;
            boolean flag3 = worldIn.isAirBlock(blockpos1) || flag1;

            if (playerIn.canPlayerEdit(pos, side, stack) && playerIn.canPlayerEdit(blockpos1, side, stack)) {
                if (flag2 && flag3 && World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && World.doesBlockHaveSolidTopSurface(worldIn, blockpos1.down())) {
                    int j = enumfacing1.getHorizontalIndex();
                    IBlockState iblockstate1 = ArchimedesObjects.blockSecuredBed.getDefaultState().withProperty(BlockSecuredBed.OCCUPIED, Boolean.valueOf(false)).withProperty(BlockSecuredBed.FACING, enumfacing1).withProperty(BlockSecuredBed.PART, BlockSecuredBed.EnumPartType.FOOT);

                    if (worldIn.setBlockState(pos, iblockstate1, 3)) {
                        IBlockState iblockstate2 = iblockstate1.withProperty(BlockSecuredBed.PART, BlockSecuredBed.EnumPartType.HEAD);
                        worldIn.setBlockState(blockpos1, iblockstate2, 3);
                    }

                    --stack.stackSize;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

}
