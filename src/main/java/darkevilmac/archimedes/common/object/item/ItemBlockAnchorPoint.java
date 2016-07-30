package darkevilmac.archimedes.common.object.item;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.client.LanguageEntries;
import darkevilmac.archimedes.common.tileentity.AnchorInstance;
import darkevilmac.archimedes.common.tileentity.BlockLocation;

public class ItemBlockAnchorPoint extends ItemBlock {
    public ItemBlockAnchorPoint(Block block) {
        super(block);
        setMaxStackSize(1);
        setCreativeTab(ArchimedesShipMod.creativeTab);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("instance"))
            return;

        if (GuiScreen.isShiftKeyDown()) {
            AnchorInstance instance = new AnchorInstance();
            instance.deserializeNBT(stack.getTagCompound().getCompoundTag("instance"));
            String readablePosition = ((BlockLocation) instance.getRelatedAnchors().values().toArray()[0]).pos
                    .toString().substring(9).replace("}", "").replaceAll("=", ":");
            String pos = I18n.format(LanguageEntries.GUI_ANCHOR_POS, (ChatFormatting.YELLOW + readablePosition).toString());
            String shortenedID = I18n.format(LanguageEntries.GUI_ANCHOR_ID, (ChatFormatting.YELLOW + instance.getIdentifier().toString().substring(0, 8)
                    + "-" + instance.getIdentifier().toString().substring(24)).toUpperCase());

            tooltip.add(pos);
            tooltip.add(shortenedID);
        } else {
            tooltip.add(ChatFormatting.BLUE.toString() + ChatFormatting.BOLD.toString() + ChatFormatting.UNDERLINE.toString()
                    + I18n.format(LanguageEntries.GUI_ITEM_TOOLTIP_SHIFT));
        }
    }
}
