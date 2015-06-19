package darkevilmac.archimedes.common.tileentity;

import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.tile.IMovingWorldTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public class TileEntityAnchorPoint extends TileEntity implements IMovingWorldTileEntity {

    public AnchorPointInfo anchorPointInfo;
    private EntityMovingWorld activeShip;

    public TileEntityAnchorPoint() {
        super();
        activeShip = null;
    }

    public void setAnchorPointInfo(BlockPos pos, boolean forShip) {
        if (anchorPointInfo == null)
            anchorPointInfo = new AnchorPointInfo();
        anchorPointInfo.setInfo(pos, forShip);
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
        if (tag.getBoolean("hasAnchorInfo") && anchorPointInfo == null) {
            anchorPointInfo = new AnchorPointInfo(new BlockPos(tag.getInteger("linkX"), tag.getInteger("linkY"), tag.getInteger("linkZ")), tag.getBoolean("forShip"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (anchorPointInfo != null) {
            tag.setInteger("linkX", anchorPointInfo.linkPos.getX());
            tag.setInteger("linkY", anchorPointInfo.linkPos.getY());
            tag.setInteger("linkZ", anchorPointInfo.linkPos.getZ());
            tag.setBoolean("forShip", anchorPointInfo.forShip);
            tag.setBoolean("hasAnchorInfo", true);
        } else {
            tag.setBoolean("hasAnchorInfo", false);
        }
        if (activeShip != null && !activeShip.isDead) {
            tag.setInteger("vehicle", activeShip.getEntityId());
        }
    }

    @Override
    public void setParentMovingWorld(BlockPos pos, EntityMovingWorld entityMovingWorld) {
        activeShip = entityMovingWorld;
    }

    @Override
    public EntityMovingWorld getParentMovingWorld() {
        return activeShip;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(new BlockPos(BlockPos.ORIGIN), entityMovingWorld);
    }

    public class AnchorPointInfo {
        public BlockPos linkPos;
        public boolean forShip;

        public AnchorPointInfo() {
            linkPos = new BlockPos(BlockPos.ORIGIN);
            forShip = false;
        }

        public AnchorPointInfo(BlockPos pos, boolean forShip) {
            this.linkPos = pos;
            this.forShip = forShip;
        }

        public void setInfo(BlockPos linkPos, boolean forShip) {
            this.linkPos = linkPos;
            this.forShip = forShip;
        }

        public AnchorPointInfo clone() {
            return new AnchorPointInfo(new BlockPos(linkPos), forShip);
        }
    }

}
