package com.elytradev.davincisvessels.common.tileentity;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.elytradev.davincisvessels.common.object.DavincisVesselsObjects;
import com.elytradev.davincisvessels.common.entity.ShipAssemblyInteractor;
import com.elytradev.davincisvessels.common.network.message.AssembleResultMessage;
import com.elytradev.movingworld.common.chunk.MovingWorldAssemblyInteractor;
import com.elytradev.movingworld.common.chunk.assembly.AssembleResult;
import com.elytradev.movingworld.common.chunk.mobilechunk.MobileChunk;
import com.elytradev.movingworld.common.entity.EntityMovingWorld;
import com.elytradev.movingworld.common.entity.MovingWorldInfo;
import com.elytradev.movingworld.common.tile.TileMovingMarkingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileHelm extends TileMovingMarkingBlock {

    public boolean submerge;
    private ShipAssemblyInteractor interactor;
    private EntityShip activeShip;
    private MovingWorldInfo info;
    private BlockPos chunkPos;

    public TileHelm() {
        super();
        activeShip = null;
    }

    @Override
    public void assembledMovingWorld(EntityPlayer player, boolean returnVal) {
        sendAssembleResult(player, false);
        sendAssembleResult(player, true);

        player.addStat(getAssembleResult().isOK() ? DavincisVesselsObjects.achievementAssembleSuccess : DavincisVesselsObjects.achievementAssembleFailure);
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld movingWorld, BlockPos chunkPos) {
        chunkPos = pos;
        activeShip = (EntityShip) movingWorld;
    }

    @Override
    public EntityShip getParentMovingWorld() {
        return activeShip;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(entityMovingWorld, new BlockPos(BlockPos.ORIGIN));
    }

    @Override
    public BlockPos getChunkPos() {

        return chunkPos;
    }

    @Override
    public void setChunkPos(BlockPos chunkPos) {
        this.chunkPos = chunkPos;
    }

    @Override
    public void tick(MobileChunk mobileChunk) {
        // No implementation
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
        return DavincisVesselsMod.INSTANCE.
                getNetworkConfig().
                getShared()
                .maxShipChunkBlocks;
    }

    @Override
    public EntityMovingWorld getMovingWorld(World worldObj) {
        return new EntityShip(worldObj);
    }

    @Override
    public void mountedMovingWorld(EntityPlayer player, EntityMovingWorld movingWorld, MountStage stage) {
        switch (stage) {
            case PREMSG: {
                sendAssembleResult(player, false);
            }
            case PRERIDE: {
            }
            case POSTRIDE: {
                player.addStat(DavincisVesselsObjects.achievementAssembleMount);
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

    public void sendAssembleResult(EntityPlayer player, boolean sendPrev) {
        if (!world.isRemote) {
            AssembleResult res;
            if (sendPrev) {
                res = getPrevAssembleResult();
            } else {
                res = getAssembleResult();
            }

            if (res == null) {
                res = new AssembleResult(AssembleResult.ResultType.RESULT_NONE, null);
            }

            new AssembleResultMessage(res, sendPrev).sendToAllWatching(this);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        tag.setBoolean("submergeShipOnAssemble", submerge);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        submerge = tag.getBoolean("submergeShipOnAssemble");
    }

}
