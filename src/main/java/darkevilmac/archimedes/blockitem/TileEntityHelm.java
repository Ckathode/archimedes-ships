package darkevilmac.archimedes.blockitem;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.archimedes.entity.ShipAssemblyInteractor;
import darkevilmac.archimedes.network.AssembleResultMessage;
import darkevilmac.movingworld.block.TileMovingWorldMarkingBlock;
import darkevilmac.movingworld.chunk.AssembleResult;
import darkevilmac.movingworld.chunk.MovingWorldAssemblyInteractor;
import darkevilmac.movingworld.entity.EntityMovingWorld;
import darkevilmac.movingworld.entity.MovingWorldInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class TileEntityHelm extends TileMovingWorldMarkingBlock {
    private EntityShip activeShip;
    private AssembleResult assembleResult, prevResult;
    private MovingWorldInfo info;
    private ShipAssemblyInteractor interactor;

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
    public EntityShip getParentMovingWorld() {
        return activeShip;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(entityMovingWorld, 0, 0, 0);
    }

    @Override
    public MovingWorldAssemblyInteractor getInteractor() {
        if (interactor == null) {
            interactor = new ShipAssemblyInteractor();
        }
        return interactor;
    }

    @Override
    public MovingWorldInfo getInfo() {
        return this.info;
    }

    @Override
    public void setInfo(MovingWorldInfo info) {
        this.info = info;
    }

    @Override
    public int getMaxBlocks() {
        return ArchimedesShipMod.instance.modConfig.maxShipChunkBlocks;
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
            AssembleResultMessage message = new AssembleResultMessage(res, prev);
            ArchimedesShipMod.instance.network.sendTo(message, (EntityPlayerMP) player);
        }
    }
}
