package com.tridevmc.davincisvessels.common;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IElementProvider<T extends Container> extends INamedContainerProvider {

    @OnlyIn(Dist.CLIENT)
    Screen createScreen(T container, PlayerEntity player);

    @Override
    default ITextComponent getDisplayName() {
        return new StringTextComponent("");
    }
}
