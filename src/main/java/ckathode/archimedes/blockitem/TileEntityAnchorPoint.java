package ckathode.archimedes.blockitem;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAnchorPoint extends TileEntity {

    public BlockAnchorPoint anchorPoint;
    public AnchorPointInfo anchorPointInfo;

    public class AnchorPointInfo {

        public int shipOffsetX;
        public int shipOffsetY;
        public int shipOffsetZ;

        public int linkX;
        public int linkY;
        public int linkZ;
        public boolean forShip;

        public AnchorPointInfo() {
            linkX = 0;
            linkY = 0;
            linkZ = 0;
            forShip = false;
        }

        public AnchorPointInfo(int x, int y, int z, boolean forShip) {
            this.linkX = x;
            this.linkY = y;
            this.linkZ = z;
            this.forShip = forShip;
        }

        public void readFromNBT(NBTTagCompound tag) {
            NBTTagCompound anchorTag = tag.hasKey("anchorTag") ? (NBTTagCompound) tag.getTag("anchorTag") : new NBTTagCompound();
            linkX = anchorTag.getInteger("linkX");
            linkY = anchorTag.getInteger("linkY");
            linkZ = anchorTag.getInteger("linkZ");
            forShip = anchorTag.getBoolean("forShip");
        }

        public void writeToNBT(NBTTagCompound tag) {
            NBTTagCompound anchorTag = tag.hasKey("anchorTag") ? (NBTTagCompound) tag.getTag("anchorTag") : new NBTTagCompound();

            anchorTag.setInteger("linkX", linkX);
            anchorTag.setInteger("linkY", linkY);
            anchorTag.setInteger("linkZ", linkZ);
            anchorTag.setBoolean("forShip", forShip);

            tag.setTag("anchorTag", anchorTag);
        }

        public AnchorPointInfo clone(){
            return new AnchorPointInfo(linkX, linkY, linkZ, forShip);
        }

    }

    @Override
    public void validate() {
        if (worldObj == null)
            return;
        if (worldObj.isRemote)
            return;
        if (anchorPointInfo == null)
            anchorPointInfo = new AnchorPointInfo();
    }

    @Override
    public void updateEntity() {
        if (worldObj != null && !worldObj.isRemote) {
            if (worldObj.getBlock(anchorPointInfo.linkX, anchorPointInfo.linkY, anchorPointInfo.linkZ) != null && worldObj.getBlock(anchorPointInfo.linkX, anchorPointInfo.linkY, anchorPointInfo.linkZ) instanceof BlockAnchorPoint) {
                anchorPoint = (BlockAnchorPoint) worldObj.getBlock(anchorPointInfo.linkX, anchorPointInfo.linkY, anchorPointInfo.linkZ);
            } else {
                anchorPoint = null;
                anchorPointInfo.linkX = 0;
                anchorPointInfo.linkY = 0;
                anchorPointInfo.linkZ = 0;
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (anchorPointInfo != null) {
            anchorPointInfo.readFromNBT(tag);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if (anchorPointInfo != null) {
            anchorPointInfo.writeToNBT(tag);
        }
    }

}
