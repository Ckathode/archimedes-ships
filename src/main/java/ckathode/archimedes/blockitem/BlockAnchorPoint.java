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
            if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityAnchorPoint && ((TileEntityAnchorPoint)world.getTileEntity(x,y,z)).anchorPointInfo != null) {
                TileEntityAnchorPoint tile = (TileEntityAnchorPoint) world.getTileEntity(x, y, z);
                if (player.isSneaking()) {
                    tile.anchorPointInfo.forShip = !tile.anchorPointInfo.forShip;
                    player.addChatComponentMessage(new ChatComponentText("This anchor is now set for use with " + (tile.anchorPointInfo.forShip ? "ships" : "the ground")));
                } else {
                    if (tile.anchorPointInfo.forShip) {
                        if (player.getEntityData().getBoolean("HasShipData")) {
                            int shipDataX = player.getEntityData().getInteger("ShipDataX");
                            int shipDataY = player.getEntityData().getInteger("ShipDataY");
                            int shipDataZ = player.getEntityData().getInteger("ShipDataZ");
                            tile.anchorPointInfo.linkX = shipDataX;
                            tile.anchorPointInfo.linkY = shipDataY;
                            tile.anchorPointInfo.linkZ = shipDataZ;
                            player.addChatComponentMessage(new ChatComponentText("Linked with anchor at X: " + shipDataX + " Y: " + shipDataY + " Z: " + shipDataZ));
                        } else {
                            player.addChatComponentMessage(new ChatComponentText("You need to activate a ground anchor to link with this ship anchor!"));
                        }
                    } else {
                        player.getEntityData().setBoolean("HasShipData", true);
                        player.getEntityData().setInteger("ShipDataX", x);
                        player.getEntityData().setInteger("ShipDataY", y);
                        player.getEntityData().setInteger("ShipDataZ", z);
                        player.addChatComponentMessage(new ChatComponentText("You've activated a ground anchor for a ship. Now right click a ship anchor to link it with this location."));
                        player.addChatComponentMessage(new ChatComponentText("X: " + x + " Y: " + y + " Z: " + z));
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
