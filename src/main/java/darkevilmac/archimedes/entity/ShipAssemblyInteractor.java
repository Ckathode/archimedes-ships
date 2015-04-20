package darkevilmac.archimedes.entity;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.movingworld.chunk.LocatedBlock;
import darkevilmac.movingworld.chunk.MovingWorldAssemblyInteractor;
import darkevilmac.movingworld.entity.MovingWorldCapabilities;

public class ShipAssemblyInteractor extends MovingWorldAssemblyInteractor {

    private int balloonCount;

    @Override
    public void blockAssembled(LocatedBlock locatedBlock) {
        if (locatedBlock.block.getUnlocalizedName() == ArchimedesShipMod.blockBalloon.getUnlocalizedName()) {
            balloonCount++;
        }

    }

    public int getBalloonCount() {
        return balloonCount;
    }

    public void setBalloonCount(int balloonCount) {
        this.balloonCount = balloonCount;
    }

    @Override
    public void transferToCapabilities(MovingWorldCapabilities capabilities) {
        if(capabilities != null && capabilities instanceof ShipCapabilities){
            ShipCapabilities shipCapabilities = (ShipCapabilities) capabilities;
            shipCapabilities.setBalloonCount(balloonCount);
        }
    }
}
