package com.elytradev.davincisvessels.common.object.item;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.LanguageEntries;
import com.elytradev.davincisvessels.common.tileentity.AnchorInstance;
import com.elytradev.davincisvessels.common.tileentity.BlockLocation;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockAnchorPoint extends ItemBlock {
    public ItemBlockAnchorPoint(Block block) {
        super(block);
        setMaxStackSize(1);
        setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
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
