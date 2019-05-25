package com.tridevmc.davincisvessels.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.entity.ShipCapabilities;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

import java.util.Locale;

public class CommandShipInfo {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("dvinfo").executes(CommandShipInfo::execute));
    }

    private static int execute(CommandContext<CommandSource> d) throws CommandSyntaxException {
        EntityPlayerMP player = d.getSource().asPlayer();

        if (player.getRidingEntity() instanceof EntityShip) {
            EntityShip ship = (EntityShip) player.getRidingEntity();
            float f = 100F * ((ShipCapabilities) ship.getMovingWorldCapabilities()).getBalloonCount() / ship.getMovingWorldCapabilities().getBlockCount();
            d.getSource().sendFeedback(new TextComponentString(ChatFormatting.GREEN.toString() + ChatFormatting.BOLD.toString() + "Ship information"), true);
            d.getSource().sendFeedback(new TextComponentString(String.format(Locale.ENGLISH, "Airship: %b", ship.getMovingWorldCapabilities().canFly())), true);
            d.getSource().sendFeedback(new TextComponentString(String.format(Locale.ENGLISH, "Position: %.2f, %.2f, %.2f", ship.posX, ship.posY, ship.posZ)), true);
            d.getSource().sendFeedback(new TextComponentString(String.format(Locale.ENGLISH, "Speed: %.2f km/h", ship.getHorizontalVelocity() * 20 * 3.6F)), true);
            d.getSource().sendFeedback(new TextComponentString(String.format(Locale.ENGLISH, "Block count: %d", ship.getMovingWorldCapabilities().getBlockCount())), true);
            d.getSource().sendFeedback(new TextComponentString(String.format(Locale.ENGLISH, "Balloon count: %d", ((ShipCapabilities) ship.getMovingWorldCapabilities()).getBalloonCount())), true);
            d.getSource().sendFeedback(new TextComponentString(String.format(Locale.ENGLISH, "Balloon percentage: %.0f%%", f)), true);
            return 1;
        } else {
            d.getSource().sendErrorMessage(new TextComponentString("Not steering a ship, no information to gather."));
            return -1;
        }
    }
}
