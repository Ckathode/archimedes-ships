package darkevilmac.archimedes.blockitem;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemGaugeBlock extends ItemBlock {
    public ItemGaugeBlock(Block block) {
        super(block);
        setHasSubtypes(true);
    }
}
