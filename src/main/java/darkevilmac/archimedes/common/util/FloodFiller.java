package darkevilmac.archimedes.common.util;

import darkevilmac.movingworld.chunk.LocatedBlock;
import darkevilmac.movingworld.chunk.mobilechunk.MobileChunk;
import darkevilmac.movingworld.util.LocatedBlockList;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;

import java.util.Iterator;

/**
 * Flood fill algorithm for finding if something is water tight.
 */

public class FloodFiller {

    private LocatedBlockList lbList = new LocatedBlockList();

    /**
     * @return amount of blocks that were filled in.
     */
    public LocatedBlockList floodFillMobileChunk(MobileChunk mobileChunk) {
        lbList = new LocatedBlockList();

        fillCoord(mobileChunk, mobileChunk.maxX() / 2, mobileChunk.maxY() + 1, mobileChunk.maxZ() / 2); // We start just outside of the bounds, this is so we don't start filling inside a room or something.
        cleanList(mobileChunk); // Clean the list of any out of bounds stuff.

        return lbList;
    }

    /**
     * Cleans the list of all the blocks outside of bounds.
     */
    private void cleanList(MobileChunk mobileChunk) {
        Iterator<LocatedBlock> lbIterator = lbList.iterator();

        while (lbIterator.hasNext()) {
            BlockPos lbPos = lbIterator.next().blockPos;

            if (lbPos.getX() > (mobileChunk.maxX() - 1) || lbPos.getX() < mobileChunk.minX() ||
                    lbPos.getY() > mobileChunk.maxY() || lbPos.getY() < mobileChunk.minY() ||
                    lbPos.getZ() > (mobileChunk.maxZ() - 1) || lbPos.getZ() < mobileChunk.minZ()
                    ) {
                lbIterator.remove();
            }
        }
    }

    private void fillCoord(MobileChunk mobileChunk, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);

        if (x > mobileChunk.maxX() || x < mobileChunk.minX() - 1 ||
                y > mobileChunk.maxY() + 1 || y < mobileChunk.minY() - 1 ||
                z > mobileChunk.maxZ() || z < mobileChunk.minZ() - 1
                ) {
            return;
        }

        if (mobileChunk.getBlock(pos) == null || mobileChunk.getBlock(pos) instanceof BlockAir) {
            // Consider this a block that's fill able.
            if (lbList.containsLBOfPos(pos))
                return;

            lbList.add(new LocatedBlock(mobileChunk.getBlockState(pos), pos));

            fillCoord(mobileChunk, x + 1, y, z);
            fillCoord(mobileChunk, x - 1, y, z);
            fillCoord(mobileChunk, x, y + 1, z);
            fillCoord(mobileChunk, x, y - 1, z);
            fillCoord(mobileChunk, x, y, z + 1);
            fillCoord(mobileChunk, x, y, z - 1);
        }
    }

}
