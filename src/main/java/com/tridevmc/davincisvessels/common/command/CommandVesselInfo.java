package com.tridevmc.davincisvessels.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import com.tridevmc.davincisvessels.common.entity.VesselCapabilities;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.Locale;

public class CommandVesselInfo {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("dvinfo").executes(CommandVesselInfo::execute));
    }

    private static int execute(CommandContext<CommandSource> d) throws CommandSyntaxException {
        ServerPlayerEntity player = d.getSource().asPlayer();

        if (player.getRidingEntity() instanceof EntityVessel) {
            EntityVessel vessel = (EntityVessel) player.getRidingEntity();
            float f = 100F * ((VesselCapabilities) vessel.getMovingWorldCapabilities()).getBalloonCount() / vessel.getMovingWorldCapabilities().getBlockCount();
            d.getSource().sendFeedback(new StringTextComponent(TextFormatting.GREEN.toString() + TextFormatting.BOLD.toString() + "Vessel information"), true);
            d.getSource().sendFeedback(new StringTextComponent(String.format(Locale.ENGLISH, "Airvessel: %b", vessel.getMovingWorldCapabilities().canFly())), true);
            d.getSource().sendFeedback(new StringTextComponent(String.format(Locale.ENGLISH, "Position: %.2f, %.2f, %.2f", vessel.posX, vessel.posY, vessel.posZ)), true);
            d.getSource().sendFeedback(new StringTextComponent(String.format(Locale.ENGLISH, "Speed: %.2f km/h", vessel.getHorizontalVelocity() * 20 * 3.6F)), true);
            d.getSource().sendFeedback(new StringTextComponent(String.format(Locale.ENGLISH, "Block count: %d", vessel.getMovingWorldCapabilities().getBlockCount())), true);
            d.getSource().sendFeedback(new StringTextComponent(String.format(Locale.ENGLISH, "Balloon count: %d", ((VesselCapabilities) vessel.getMovingWorldCapabilities()).getBalloonCount())), true);
            d.getSource().sendFeedback(new StringTextComponent(String.format(Locale.ENGLISH, "Balloon percentage: %.0f%%", f)), true);
            return 1;
        } else {
            d.getSource().sendErrorMessage(new StringTextComponent("Not steering a vessel, no information to gather."));
            return -1;
        }
    }
}
