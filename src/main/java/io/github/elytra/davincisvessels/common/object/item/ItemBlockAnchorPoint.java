package io.github.elytra.davincisvessels.common.object.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.common.LanguageEntries;
import io.github.elytra.davincisvessels.common.tileentity.AnchorInstance;
import io.github.elytra.davincisvessels.common.tileentity.BlockLocation;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockAnchorPoint extends ItemBlock {
    public ItemBlockAnchorPoint(Block block) {
        super(block);
        setMaxStackSize(1);
        setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("INSTANCE"))
            return;

        if (GuiScreen.isShiftKeyDown()) {
            AnchorInstance instance = new AnchorInstance();
            instance.deserializeNBT(stack.getTagCompound().getCompoundTag("INSTANCE"));
            String readablePosition = ((BlockLocation) instance.getRelatedAnchors().values().toArray()[0]).pos
                    .toString().substring(9).replace("}", "").replaceAll("=", ":");
            String pos = I18n.format(LanguageEntries.GUI_ANCHOR_POS, (ChatFormatting.YELLOW + readablePosition).toString());
            String type = I18n.format(LanguageEntries.GUI_ANCHOR_TYPE, (ChatFormatting.YELLOW + instance.getType().toString()).toString());
            tooltip.add(pos);
            tooltip.add(type);
        } else {
            tooltip.add(ChatFormatting.BLUE.toString() + ChatFormatting.BOLD.toString() + ChatFormatting.UNDERLINE.toString()
                    + I18n.format(LanguageEntries.GUI_ITEM_TOOLTIP_SHIFT));
        }
    }
}
