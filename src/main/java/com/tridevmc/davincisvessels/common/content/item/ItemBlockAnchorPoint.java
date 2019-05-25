package com.tridevmc.davincisvessels.common.content.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.LanguageEntries;
import com.tridevmc.davincisvessels.common.tileentity.AnchorInstance;
import com.tridevmc.davincisvessels.common.tileentity.BlockLocation;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockAnchorPoint extends ItemBlock {
    public ItemBlockAnchorPoint(Block block) {
        super(block, new Item.Properties().group(DavincisVesselsMod.CONTENT.itemGroup).maxStackSize(1));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        if (stack.getTag() == null || !stack.getTag().contains("INSTANCE"))
            return;

        if (GuiScreen.isShiftKeyDown()) {
            AnchorInstance instance = new AnchorInstance();
            instance.deserializeNBT(stack.getTag().getCompound("INSTANCE"));
            String readablePosition = ((BlockLocation) instance.getRelatedAnchors().values().toArray()[0]).getPos()
                    .toString().substring(9).replace("}", "").replaceAll("=", ":");
            tooltip.add(new TextComponentTranslation(LanguageEntries.GUI_ANCHOR_POS, ChatFormatting.YELLOW + readablePosition));
            tooltip.add(new TextComponentTranslation(LanguageEntries.GUI_ANCHOR_TYPE, ChatFormatting.YELLOW + instance.getType().toString()));
        } else {
            tooltip.add(new TextComponentString(ChatFormatting.BLUE.toString() + ChatFormatting.BOLD.toString() + ChatFormatting.UNDERLINE.toString()
                    + I18n.format(LanguageEntries.GUI_ITEM_TOOLTIP_SHIFT)));
        }
    }

}
