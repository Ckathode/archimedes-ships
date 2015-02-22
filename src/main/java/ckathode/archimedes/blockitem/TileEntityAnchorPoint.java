package ckathode.archimedes.blockitem;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAnchorPoint extends TileEntity {

    public AnchorPointInfo anchorPointInfo;

    public class AnchorPointInfo {
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

        public void setInfo(int linkX, int linkY, int linkZ, boolean forShip) {
            this.linkX = linkX;
            this.linkY = linkY;
            this.linkZ = linkZ;
            this.forShip = forShip;
        }

        public AnchorPointInfo clone() {
            return new AnchorPointInfo(linkX, linkY, linkZ, forShip);
        }

    }

    int time;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (time > 20) {
            if (worldObj != null && !worldObj.isRemote) {
                if (anchorPointInfo == null) {
                    anchorPointInfo = new AnchorPointInfo();
                } else {
                    if (anchorPointInfo.forShip) {
                        if (worldObj.getTileEntity(anchorPointInfo.linkX, anchorPointInfo.linkY, anchorPointInfo.linkZ) == null || (worldObj.getTileEntity(anchorPointInfo.linkX, anchorPointInfo.linkY, anchorPointInfo.linkZ) != null && worldObj.getTileEntity(anchorPointInfo.linkX, anchorPointInfo.linkY, anchorPointInfo.linkZ) instanceof TileEntityAnchorPoint == false)) {
                            anchorPointInfo.linkX = anchorPointInfo.linkY = anchorPointInfo.linkZ = 0;
                        }
                    } else {
                        anchorPointInfo.linkX = 0;
                        anchorPointInfo.linkY = 0;
                        anchorPointInfo.linkZ = 0;
                    }
                }
            }
        } else time++;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        anchorPointInfo = new AnchorPointInfo(tag.getInteger("linkX"), tag.getInteger("linkY"), tag.getInteger("linkZ"), tag.getBoolean("forShip"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("linkX", anchorPointInfo.linkX);
        tag.setInteger("linkY", anchorPointInfo.linkY);
        tag.setInteger("linkZ", anchorPointInfo.linkZ);
        tag.setBoolean("forShip", anchorPointInfo.forShip);
    }

}
