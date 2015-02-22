package ckathode.archimedes.event;

import ckathode.archimedes.chunk.LocatedBlock;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Created by DarkEvilMac on 2/22/2015.
 */

public class AssembleBlockEvent extends Event {

    public LocatedBlock block;

    public AssembleBlockEvent(LocatedBlock block) {
        this.block = block;
    }

}