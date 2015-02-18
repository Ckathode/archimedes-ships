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
        if (world != null && player != null) {
            if (world.getBlock(x, y, z).getUnlocalizedName().contains("ground")) {
                player.getEntityData().setBoolean("HasSelectedAnchor", true);
                if (!world.isRemote) {
                    player.getEntityData().setInteger("SelectedShipAnchorX", x);
                    player.getEntityData().setInteger("SelectedShipAnchorY", y);
                    player.getEntityData().setInteger("SelectedShipAnchorZ", z);
                } else {
                    player.addChatComponentMessage(new ChatComponentText("Right click the anchor on your ship to link it to this point."));
                }
            } else {
                if (player.getEntityData().getBoolean("HasSelectedAnchor")) {
                    if (!world.isRemote) {
                        TileEntityAnchorPoint tile = (TileEntityAnchorPoint) world.getTileEntity(x, y, z);

                        tile.linkX = player.getEntityData().getInteger("SelectedShipAnchorX");
                        tile.linkY = player.getEntityData().getInteger("SelectedShipAnchorY");
                        tile.linkZ = player.getEntityData().getInteger("SelectedShipAnchorZ");
                    } else {
                        player.addChatComponentMessage(new ChatComponentText("Linked to the last anchor you clicked."));
                    }
                } else {
                    if (world.isRemote)
                        player.addChatComponentMessage(new ChatComponentText("You haven't right clicked an anchor to link to yet!"));
                }
            }
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (!world.isRemote) {
            return new TileEntityAnchorPoint();
        }
        return null;
    }
}
