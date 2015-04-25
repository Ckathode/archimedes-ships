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
    private ShipAssemblyInteractor interactor;
    private EntityShip activeShip;
    private MovingWorldInfo info;

    public TileEntityHelm() {
        super();
        activeShip = null;
    }

    @Override
    public void assembledMovingWorld(EntityPlayer player, boolean returnVal) {
        sendAssembleResult(player, false);
        sendAssembleResult(player, true);
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
    public void setInteractor(MovingWorldAssemblyInteractor interactor) {
        this.interactor = (ShipAssemblyInteractor) interactor;
    }

    @Override
    public MovingWorldInfo getInfo() {
        if (this.info == null)
            this.info = new MovingWorldInfo();
        return info;
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
        switch (stage) {
            case 1: {
                sendAssembleResult(player, false);
            }
        }
    }

    @Override
    public void undoCompilation(EntityPlayer player) {
        super.undoCompilation(player);
        sendAssembleResult(player, false);
        sendAssembleResult(player, true);
    }

    @Override
    public MovingWorldAssemblyInteractor getNewAssemblyInteractor() {
        return new ShipAssemblyInteractor();
    }

    public void sendAssembleResult(EntityPlayer player, boolean prev) {
        if (!worldObj.isRemote) {
            AssembleResult res;
            if (prev) {
                res = getPrevAssembleResult();
            } else {
                res = getAssembleResult();
            }
            AssembleResultMessage message = new AssembleResultMessage(res, prev);
            ArchimedesShipMod.instance.network.sendTo(message, (EntityPlayerMP) player);
        }
    }
}
