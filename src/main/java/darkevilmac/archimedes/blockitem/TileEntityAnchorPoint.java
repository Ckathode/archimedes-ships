package darkevilmac.archimedes.blockitem;

import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.tile.IMovingWorldTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public class TileEntityAnchorPoint extends TileEntity implements IMovingWorldTileEntity, IUpdatePlayerListBox {

    public AnchorPointInfo anchorPointInfo;
    int time;
    private EntityMovingWorld activeShip;

    public TileEntityAnchorPoint() {
        super();
        activeShip = null;
    }

    @Override
    public void update() {
        if (time > 20) {
            if (worldObj != null && !worldObj.isRemote) {
                if (anchorPointInfo == null) {
                    anchorPointInfo = new AnchorPointInfo();
                } else {
                    if (anchorPointInfo.forShip) {
                        if (worldObj.getTileEntity(anchorPointInfo.linkPos) == null || (worldObj.getTileEntity(anchorPointInfo.linkPos) != null && worldObj.getTileEntity(anchorPointInfo.linkPos) instanceof TileEntityAnchorPoint == false)) {
                            anchorPointInfo.linkPos = new BlockPos(BlockPos.ORIGIN);
                        }
                    } else {
                        anchorPointInfo.linkPos = new BlockPos(BlockPos.ORIGIN);
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
            anchorPointInfo = new AnchorPointInfo(new BlockPos(tag.getInteger("linkX"), tag.getInteger("linkY"), tag.getInteger("linkZ")), tag.getBoolean("forShip"));
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
            tag.setInteger("linkX", anchorPointInfo.linkPos.getX());
            tag.setInteger("linkY", anchorPointInfo.linkPos.getY());
            tag.setInteger("linkZ", anchorPointInfo.linkPos.getZ());
            tag.setBoolean("forShip", anchorPointInfo.forShip);
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
