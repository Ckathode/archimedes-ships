package ckathode.archimedes.blockitem;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAnchorPoint extends TileEntity {

    public int linkX;
    public int linkY;
    public int linkZ;
    public boolean forShip; // Pretty much just metadata
    public BlockAnchorPoint anchorPoint;

    @Override
    public void updateEntity() {
        if (worldObj != null && !worldObj.isRemote) {
            if (worldObj.getBlock(linkX, linkY, linkZ) != null && worldObj.getBlock(linkX, linkY, linkZ) instanceof BlockAnchorPoint) {
                anchorPoint = (BlockAnchorPoint) worldObj.getBlock(linkX, linkY, linkZ);
            } else {
                anchorPoint = null;
                linkX = 0;
                linkY = 0;
                linkZ = 0;
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        linkX = tag.getInteger("linkX");
        linkY = tag.getInteger("linkY");
        linkZ = tag.getInteger("linkZ");
        forShip = tag.getBoolean("forShip");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("linkX", linkX);
        tag.setInteger("linkY", linkY);
        tag.setInteger("linkZ", linkZ);
        tag.setBoolean("forShip", forShip);
    }

}
