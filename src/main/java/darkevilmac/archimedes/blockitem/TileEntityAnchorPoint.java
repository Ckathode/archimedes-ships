package darkevilmac.archimedes.blockitem;

import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.tile.IMovingWorldTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public class TileEntityAnchorPoint extends TileEntity implements IMovingWorldTileEntity {

    public AnchorPointInfo anchorPointInfo;
    int time;
    private EntityMovingWorld activeShip;

    public TileEntityAnchorPoint() {
        super();
        activeShip = null;
    }

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
        if (worldObj != null && tag.hasKey("vehicle") && worldObj != null) {
            int id = tag.getInteger("vehicle");
            Entity entity = worldObj.getEntityByID(id);
            if (entity != null && entity instanceof EntityMovingWorld) {
                activeShip = (EntityMovingWorld) entity;
            }
        }
        if (tag.getBoolean("hasAnchorInfo")) {
            anchorPointInfo = new AnchorPointInfo(tag.getInteger("linkX"), tag.getInteger("linkY"), tag.getInteger("linkZ"), tag.getBoolean("forShip"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (activeShip != null && !activeShip.isDead) {
            tag.setInteger("vehicle", activeShip.getEntityId());
        }
        if (anchorPointInfo != null) {
            tag.setBoolean("hasAnchorInfo", true);
            tag.setInteger("linkX", anchorPointInfo.linkX);
            tag.setInteger("linkY", anchorPointInfo.linkY);
            tag.setInteger("linkZ", anchorPointInfo.linkZ);
            tag.setBoolean("forShip", anchorPointInfo.forShip);
        }
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld, int x, int y, int z) {
        activeShip = entityMovingWorld;
    }

    @Override
    public EntityMovingWorld getParentMovingWorld() {
        return activeShip;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(entityMovingWorld, 0, 0, 0);
    }

    public class AnchorPointInfo {
        public BlockPos linkPos;
        public boolean forShip;

        public AnchorPointInfo() {
            linkPos = new BlockPos(BlockPos.ORIGIN);
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

}
