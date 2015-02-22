package ckathode.archimedes.blockitem;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
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
                    //Switch Mode from Ship/Ground to Ground/Ship.
                    tile.anchorPointInfo.forShip = !tile.anchorPointInfo.forShip;
                    player.addChatComponentMessage(new ChatComponentText("This Anchor is now set for use on " + (tile.anchorPointInfo.forShip ? "ships" : "the ground.")));
                } else {
                    if (tile.anchorPointInfo.forShip) {
                        if (player.getEntityData().getBoolean("SelectedShipData")) {
                            int[] selectedShipPos = player.getEntityData().getIntArray("SelectedShipAnchorPos");
                            tile.anchorPointInfo.setInfo(selectedShipPos[0], selectedShipPos[1], selectedShipPos[2], tile.anchorPointInfo.forShip);
                            player.addChatComponentMessage(new ChatComponentText("Linked ship anchor with ground anchor" + (" X:" + selectedShipPos[0] + " Y:" + selectedShipPos[1] + " Z:" + selectedShipPos[2])));
                        } else {
                            player.addChatComponentMessage(new ChatComponentText("You must activate a ground anchor to link with this ship anchor."));
                        }
                    } else {
                        player.getEntityData().setIntArray("SelectedShipAnchorPos", new int[]{x, y, z});
                        player.getEntityData().setBoolean("SelectedShipData", true);
                        player.addChatComponentMessage(new ChatComponentText("You have activated an anchor for use on the ground, activate any anchors for ships you want this anchor to be associated with."));
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
