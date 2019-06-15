package com.tridevmc.davincisvessels.common.content.item;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.LanguageEntries;
import com.tridevmc.davincisvessels.common.tileentity.AnchorInstance;
import com.tridevmc.davincisvessels.common.tileentity.BlockLocation;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockAnchorPoint extends BlockItem {
    public ItemBlockAnchorPoint(Block block) {
        super(block, new Item.Properties().group(DavincisVesselsMod.CONTENT.itemGroup).maxStackSize(1));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        if (stack.getTag() == null || !stack.getTag().contains("INSTANCE"))
            return;

        if (Screen.hasShiftDown()) {
            AnchorInstance instance = new AnchorInstance();
            instance.deserializeNBT(stack.getTag().getCompound("INSTANCE"));
            String readablePosition = ((BlockLocation) instance.getRelatedAnchors().values().toArray()[0]).getPos()
                    .toString().substring(9).replace("}", "").replaceAll("=", ":");
            tooltip.add(new TranslationTextComponent(LanguageEntries.GUI_ANCHOR_POS, TextFormatting.YELLOW + readablePosition));
            tooltip.add(new TranslationTextComponent(LanguageEntries.GUI_ANCHOR_TYPE, TextFormatting.YELLOW + instance.getType().toString()));
        } else {
            tooltip.add(new StringTextComponent(TextFormatting.BLUE.toString() + TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE.toString()
                    + I18n.format(LanguageEntries.GUI_ITEM_TOOLTIP_SHIFT)));
        }
    }

}
