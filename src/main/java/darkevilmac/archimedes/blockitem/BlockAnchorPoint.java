package darkevilmac.archimedes.blockitem;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.network.TranslatedChatMessage;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockAnchorPoint extends BlockContainer {

    public BlockAnchorPoint(Material material) {
        super(material);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world != null && player != null && !world.isRemote) {
            if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityAnchorPoint) {
                TileEntityAnchorPoint tile = (TileEntityAnchorPoint) world.getTileEntity(pos);
                if (tile.anchorPointInfo == null)
                    return false;
                if (player.isSneaking()) {
                    tile.anchorPointInfo.forShip = !tile.anchorPointInfo.forShip;
                    ArchimedesShipMod.instance.network.sendTo(new TranslatedChatMessage("TR:" + (tile.anchorPointInfo.forShip ? "common.tile.anchor.changeModeShip" : "common.tile.anchor.changeModeGround") + "~ "), (EntityPlayerMP) player);
                } else {
                    if (tile.anchorPointInfo.forShip) {
                        if (player.getEntityData().getBoolean("SelectedShipData")) {
                            int[] selectedShipPos = player.getEntityData().getIntArray("SelectedShipAnchorPos");
                            tile.anchorPointInfo.setInfo(new BlockPos(selectedShipPos[0], selectedShipPos[1], selectedShipPos[2]), tile.anchorPointInfo.forShip);
                            ArchimedesShipMod.instance.network.sendTo(new TranslatedChatMessage("TR:" + "common.tile.anchor.activateShip" + "~ X:" + selectedShipPos[0] + " Y:" + selectedShipPos[1] + " Z:" + selectedShipPos[2]), (EntityPlayerMP) player);
                        } else {
                            ArchimedesShipMod.instance.network.sendTo(new TranslatedChatMessage("TR:" + "common.tile.anchor.noGroundLink"), (EntityPlayerMP) player);
                        }
                    } else {
                        player.getEntityData().setIntArray("SelectedShipAnchorPos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                        player.getEntityData().setBoolean("SelectedShipData", true);
                        ArchimedesShipMod.instance.network.sendTo(new TranslatedChatMessage("TR:" + "common.tile.anchor.activateGround"), (EntityPlayerMP) player);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.SOLID;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAnchorPoint();
    }
}
