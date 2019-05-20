package com.tridevmc.davincisvessels.common.tileentity;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.client.gui.ContainerHelm;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.entity.ShipAssemblyInteractor;
import com.tridevmc.davincisvessels.common.network.message.AssembleResultMessage;
import com.tridevmc.movingworld.common.chunk.MovingWorldAssemblyInteractor;
import com.tridevmc.movingworld.common.chunk.assembly.AssembleResult;
import com.tridevmc.movingworld.common.chunk.mobilechunk.MobileChunk;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import com.tridevmc.movingworld.common.entity.MovingWorldInfo;
import com.tridevmc.movingworld.common.tile.TileMovingMarkingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TileHelm extends TileMovingMarkingBlock implements IInteractionObject {

    public boolean submerge;
    private ShipAssemblyInteractor interactor;
    private EntityShip activeShip;
    private MovingWorldInfo info;
    private BlockPos chunkPos;

    public TileHelm() {
        super(DavincisVesselsMod.CONTENT.tileTypes.get(TileHelm.class));
        activeShip = null;
    }

    @Override
    public void assembledMovingWorld(EntityPlayer player, boolean returnVal) {
        sendAssembleResult(player, false);
        sendAssembleResult(player, true);

        // TODO: Achievements are gone.
        //player.addStat(getAssembleResult().isOK() ? DavincisVesselsContent.achievementAssembleSuccess : DavincisVesselsContent.achievementAssembleFailure);
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
        return DavincisVesselsMod.CONFIG.maxShipChunkBlocks;
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
                // TODO: Achievements are gone
                // player.addStat(DavincisVesselsContent.achievementAssembleMount);
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

            new AssembleResultMessage(res, sendPrev).sendToAllTracking(this);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        tag = super.write(tag);
        tag.putBoolean("submergeShipOnAssemble", submerge);
        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);
        submerge = tag.getBoolean("submergeShipOnAssemble");
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerHelm(this, playerIn);
    }

    @Override
    public String getGuiID() {
        return DavincisVesselsMod.RESOURCE_DOMAIN + "helm";
    }

    @Override
    public ITextComponent getName() {
        return new TextComponentString("");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return new TextComponentString("");
    }
}
