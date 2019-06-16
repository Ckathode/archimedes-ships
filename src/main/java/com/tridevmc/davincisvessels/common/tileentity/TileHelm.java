package com.tridevmc.davincisvessels.common.tileentity;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.client.gui.ContainerHelm;
import com.tridevmc.davincisvessels.client.gui.GuiHelm;
import com.tridevmc.davincisvessels.common.IElementProvider;
import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import com.tridevmc.davincisvessels.common.entity.VesselAssemblyInteractor;
import com.tridevmc.davincisvessels.common.network.message.AssembleResultMessage;
import com.tridevmc.movingworld.common.chunk.MovingWorldAssemblyInteractor;
import com.tridevmc.movingworld.common.chunk.assembly.AssembleResult;
import com.tridevmc.movingworld.common.chunk.mobilechunk.MobileChunk;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import com.tridevmc.movingworld.common.entity.MovingWorldInfo;
import com.tridevmc.movingworld.common.tile.TileMovingMarkingBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileHelm extends TileMovingMarkingBlock implements IElementProvider<ContainerHelm> {

    public boolean submerge;
    private VesselAssemblyInteractor interactor;
    private EntityVessel activeVessel;
    private MovingWorldInfo info;
    private BlockPos chunkPos;

    public TileHelm() {
        super(DavincisVesselsMod.CONTENT.tileTypes.get(TileHelm.class));
        activeVessel = null;
    }

    @Override
    public void assembledMovingWorld(PlayerEntity player, boolean returnVal) {
        sendAssembleResult(player, false);
        sendAssembleResult(player, true);

        // TODO: Achievements are gone.
        //player.addStat(getAssembleResult().isOK() ? DavincisVesselsContent.achievementAssembleSuccess : DavincisVesselsContent.achievementAssembleFailure);
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld movingWorld, BlockPos chunkPos) {
        chunkPos = pos;
        activeVessel = (EntityVessel) movingWorld;
    }

    @Override
    public EntityVessel getParentMovingWorld() {
        return activeVessel;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(entityMovingWorld, new BlockPos(BlockPos.ZERO));
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
            interactor = new VesselAssemblyInteractor();
        }
        return interactor;
    }

    @Override
    public void setInteractor(MovingWorldAssemblyInteractor interactor) {
        this.interactor = (VesselAssemblyInteractor) interactor;
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
        return DavincisVesselsMod.CONFIG.maxVesselChunkBlocks;
    }

    @Override
    public EntityMovingWorld getMovingWorld(World worldObj) {
        return new EntityVessel(worldObj);
    }

    @Override
    public void mountedMovingWorld(PlayerEntity player, EntityMovingWorld movingWorld, MountStage stage) {
        switch (stage) {
            case PREMSG: {
                sendAssembleResult(player, false);
            }
            case PRERIDE: {
            }
            case POSTRIDE: {
                // TODO: Achievements are gone
                // player.addStat(DavincisVesselsContent.achievementAssembleMount);
            }
        }
    }

    @Override
    public void undoCompilation(PlayerEntity player) {
        super.undoCompilation(player);
        sendAssembleResult(player, false);
        sendAssembleResult(player, true);
    }

    @Override
    public MovingWorldAssemblyInteractor getNewAssemblyInteractor() {
        return new VesselAssemblyInteractor();
    }

    public void sendAssembleResult(PlayerEntity player, boolean sendPrev) {
        if (!world.isRemote) {
            AssembleResult res;
            if (sendPrev) {
                res = getPrevAssembleResult();
            } else {
                res = getAssembleResult();
            }

            if (res == null) {
                res = new AssembleResult(AssembleResult.ResultType.RESULT_NONE, null);
                res.assemblyInteractor = this.getNewAssemblyInteractor();
            }

            new AssembleResultMessage(res, sendPrev).sendToAllTracking(this);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);
        tag.putBoolean("submergeVesselOnAssemble", submerge);
        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        submerge = tag.getBoolean("submergeVesselOnAssemble");
    }

    @Override
    public Container createMenu(int window, PlayerInventory playerInventory, PlayerEntity player) {
        return new ContainerHelm(window, this, player);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Screen createScreen(ContainerHelm container, PlayerEntity player) {
        return new GuiHelm(container);
    }
}
