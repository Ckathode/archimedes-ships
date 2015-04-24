package darkevilmac.archimedes.entity;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.blockitem.TileEntityHelm;
import darkevilmac.movingworld.chunk.LocatedBlock;
import darkevilmac.movingworld.chunk.MovingWorldAssemblyInteractor;
import darkevilmac.movingworld.entity.MovingWorldCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ShipAssemblyInteractor extends MovingWorldAssemblyInteractor {

    private int balloonCount;

    public ShipAssemblyInteractor() {
    }

    @Override
    public void toByteBuf(ByteBuf byteBuf) {
        byteBuf.writeInt(getBalloonCount());
        System.out.println("ToBuf");
    }

    @Override
    public MovingWorldAssemblyInteractor fromByteBuf(ByteBuf buf) {
        ShipAssemblyInteractor mov = new ShipAssemblyInteractor();
        mov.setBalloonCount(buf.readInt());
        System.out.println("FromBUf");

        return mov;
    }

    @Override
    public MovingWorldAssemblyInteractor fromNBT(NBTTagCompound tag, World world) {
        ShipAssemblyInteractor mov = new ShipAssemblyInteractor();
        mov.setBalloonCount(tag.getInteger("balloonCount"));
        return mov;
    }

    @Override
    public void blockAssembled(LocatedBlock locatedBlock) {
        if (locatedBlock.block.getUnlocalizedName() == ArchimedesShipMod.blockBalloon.getUnlocalizedName()) {
            balloonCount++;
        }

    }

    @Override
    public boolean isBlockMovingWorldMarker(Block block) {
        if (block != null)
            return block.getUnlocalizedName() == ArchimedesShipMod.blockMarkShip.getUnlocalizedName();
        else
            return false;
    }

    @Override
    public boolean isTileMovingWorldMarker(TileEntity tile) {
        if (tile != null)
            return tile instanceof TileEntityHelm;
        else
            return false;
    }

    public int getBalloonCount() {
        return balloonCount;
    }

    public void setBalloonCount(int balloonCount) {
        this.balloonCount = balloonCount;
    }

    @Override
    public void transferToCapabilities(MovingWorldCapabilities capabilities) {
        if (capabilities != null && capabilities instanceof ShipCapabilities) {
            ShipCapabilities shipCapabilities = (ShipCapabilities) capabilities;
            shipCapabilities.setBalloonCount(balloonCount);
        }
    }

    @Override
    public void writeNBTFully(NBTTagCompound compound) {
        writeNBTMetadata(compound);
    }

    @Override
    public void writeNBTMetadata(NBTTagCompound compound) {
        compound.setInteger("balloonCount", getBalloonCount());
    }
}
