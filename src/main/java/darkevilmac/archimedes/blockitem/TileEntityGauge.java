package darkevilmac.archimedes.blockitem;

import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.tile.IMovingWorldTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public class TileEntityGauge extends TileEntity implements IMovingWorldTileEntity {
    public EntityMovingWorld parentShip;

    public TileEntityGauge() {
        parentShip = null;
    }

    @Override
    public void setParentMovingWorld(BlockPos pos, EntityMovingWorld entityMovingWorld) {
        parentShip = entityMovingWorld;
    }

    @Override
    public EntityMovingWorld getParentMovingWorld() {
        return parentShip;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(new BlockPos(BlockPos.ORIGIN), entityMovingWorld);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("vehicle") && worldObj != null) {
            int id = compound.getInteger("vehicle");
            Entity entity = worldObj.getEntityByID(id);
            if (entity instanceof EntityMovingWorld) {
                parentShip = (EntityMovingWorld) entity;
            }
        }

        if (worldObj != null && worldObj.isRemote) {
            IBlockState state = worldObj.getBlockState(pos);
            if (state != null && state.getBlock().getMetaFromState(state) != compound.getInteger("meta")) {
                worldObj.setBlockState(pos, state.getBlock().getStateFromMeta(compound.getInteger("meta")), 2);
            }
        }
        blockMetadata = compound.getInteger("meta");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("meta", blockMetadata);
        if (parentShip != null && !parentShip.isDead) {
            compound.setInteger("vehicle", parentShip.getEntityId());
        }
    }

}
