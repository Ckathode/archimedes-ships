package com.tridevmc.davincisvessels.common;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;

import javax.annotation.Nullable;

public interface IElementProvider extends IInteractionObject {

    @OnlyIn(Dist.CLIENT)
    GuiScreen createGui(FMLPlayMessages.OpenContainer openContainer);

    @Override
    default String getGuiID() {
        if (this instanceof TileEntity) {
            return DavincisVesselsMod.RESOURCE_DOMAIN + "tile";
        } else if (this instanceof Entity) {
            return DavincisVesselsMod.RESOURCE_DOMAIN + "entity";
        }

        return DavincisVesselsMod.RESOURCE_DOMAIN + "other";
    }

    @Override
    default ITextComponent getName() {
        return new TextComponentString("");
    }

    @Override
    default boolean hasCustomName() {
        return false;
    }

    @Nullable
    @Override
    default ITextComponent getCustomName() {
        return new TextComponentString("");
    }

    @Override
    default ITextComponent getDisplayName() {
        return new TextComponentString("");
    }
}
