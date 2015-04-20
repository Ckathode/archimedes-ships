package darkevilmac.archimedes.blockitem;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.movingworld.block.TileMovingWorldMarkingBlock;
import darkevilmac.movingworld.chunk.AssembleResult;
import darkevilmac.movingworld.chunk.MovingWorldAssemblyInteractor;
import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.entity.MovingWorldInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;

public class TileEntityHelm extends TileMovingWorldMarkingBlock {
    private EntityShip activeShip;
    private MovingWorldInfo info;
    private AssembleResult assembleResult, prevResult;

    public TileEntityHelm() {
        super();
        info = new MovingWorldInfo();
        activeShip = null;
        assembleResult = prevResult = null;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }



    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld, int x, int y, int z) {
        activeShip = (EntityShip) entityMovingWorld;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {

    }

    @Override
    public EntityShip getParentMovingWorld() {
        return activeShip;
    }

    @Override
    public MovingWorldAssemblyInteractor getInteractor() {
        return null;
    }

    @Override
    public MovingWorldInfo getInfo() {
        return null;
    }

    @Override
    public int getMaxBlocks() {
        return 0;
    }

    @Override
    public MovingWorldInfo getMovingWorldInfo() {
        return null;
    }

    @Override
    public void setMovingWorldInfo(MovingWorldInfo movingWorldInfo) {
    }

    @Override
    public EntityMovingWorld getMovingWorld(World worldObj) {
        return new EntityShip(worldObj);
    }

    @Override
    public void mountedMovingWorld(EntityPlayer player, EntityMovingWorld movingWorld, int stage) {
        if (stage == 1) {
            sendAssembleResult(player, false);
        }
    }

    @Override
    public void undoCompilation(EntityPlayer player) {
        super.undoCompilation(player);
        sendAssembleResult(player, false);
        sendAssembleResult(player, true);
    }

    public void sendAssembleResult(EntityPlayer player, boolean prev) {
        if (!worldObj.isRemote) {
            AssembleResult res;
            if (prev) {
                res = prevResult;
            } else {
                res = assembleResult;
            }
            MsgAssembleResult message = new MsgAssembleResult(res, prev);
            ArchimedesShipMod.instance.pipeline.sendTo(message, (EntityPlayerMP) player);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    }

}
