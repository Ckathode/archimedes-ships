package darkevilmac.archimedes.blockitem;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.network.TranslatedChatMessage;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAnchorPoint extends BlockContainer {

    public BlockAnchorPoint(Material material) {
        super(material);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p6, float p7, float p8, float p9) {
        if (world != null && player != null && !world.isRemote) {
            if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityAnchorPoint) {
                TileEntityAnchorPoint tile = (TileEntityAnchorPoint) world.getTileEntity(x, y, z);
                if (tile.anchorPointInfo == null)
                    return false;
                if (player.isSneaking()) {
                    tile.anchorPointInfo.forShip = !tile.anchorPointInfo.forShip;
                    ArchimedesShipMod.instance.network.sendTo(new TranslatedChatMessage("TR:" + (tile.anchorPointInfo.forShip ? "common.tile.anchor.changeModeShip" : "common.tile.anchor.changeModeGround") + "~ "), (EntityPlayerMP) player);
                } else {
                    if (tile.anchorPointInfo.forShip) {
                        if (player.getEntityData().getBoolean("SelectedShipData")) {
                            int[] selectedShipPos = player.getEntityData().getIntArray("SelectedShipAnchorPos");
                            tile.anchorPointInfo.setInfo(selectedShipPos[0], selectedShipPos[1], selectedShipPos[2], tile.anchorPointInfo.forShip);
                            ArchimedesShipMod.instance.network.sendTo(new TranslatedChatMessage("TR:" + "common.tile.anchor.activateShip" + "~ X:" + selectedShipPos[0] + " Y:" + selectedShipPos[1] + " Z:" + selectedShipPos[2]), (EntityPlayerMP) player);
                        } else {
                            ArchimedesShipMod.instance.network.sendTo(new TranslatedChatMessage("TR:" + "common.tile.anchor.noGroundLink"), (EntityPlayerMP) player);
                        }
                    } else {
                        player.getEntityData().setIntArray("SelectedShipAnchorPos", new int[]{x, y, z});
                        player.getEntityData().setBoolean("SelectedShipData", true);
                        ArchimedesShipMod.instance.network.sendTo(new TranslatedChatMessage("TR:" + "common.tile.anchor.activateGround"), (EntityPlayerMP) player);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAnchorPoint();
    }
}
