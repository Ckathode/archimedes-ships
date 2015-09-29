package darkevilmac.archimedes.common.object.block;

import darkevilmac.archimedes.common.tileentity.TileEntitySecuredBed;
import net.minecraft.block.BlockBed;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockSecuredBed extends BlockBed implements ITileEntityProvider {

    public BlockSecuredBed() {
        super();
        disableStats();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn == null || (worldIn != null && worldIn.isRemote))
            return true;

        if (state.getValue(PART) != BlockBed.EnumPartType.HEAD) {
            pos = pos.offset((EnumFacing) state.getValue(FACING));
            state = worldIn.getBlockState(pos);

            if (state.getBlock() != this) {
                return true;
            }
        }

        boolean occupiedPre = ((Boolean) state.getValue(OCCUPIED)).booleanValue();
        super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
        boolean occupiedPost = ((Boolean) state.getValue(OCCUPIED)).booleanValue();

        if (!occupiedPre && occupiedPost) {
            // We have a new bed user.
            if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntitySecuredBed) {
                TileEntitySecuredBed bed = (TileEntitySecuredBed) worldIn.getTileEntity(pos);

                bed.setPlayer(playerIn);
            }
        }

        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (this.getStateFromMeta(meta).getValue(BlockBed.PART) == EnumPartType.HEAD)
            return new TileEntitySecuredBed();

        return null;
    }
}
