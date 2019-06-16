package com.tridevmc.davincisvessels.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CommandDisassembleVessel {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("dvdisassemble")
                .requires(p -> p.hasPermissionLevel(3))
                .executes(c -> execute(c, false, false))
                .then(Commands.argument("force", BoolArgumentType.bool())).executes(c -> execute(c, BoolArgumentType.getBool(c, "force"), false))
                .then(Commands.argument("force", BoolArgumentType.bool()).then(Commands.argument("drop", BoolArgumentType.bool()))
                        .executes(c -> execute(c, BoolArgumentType.getBool(c, "force"), BoolArgumentType.getBool(c, "drop")))));
    }

    private static int execute(CommandContext<CommandSource> d, boolean force, boolean drop) throws CommandSyntaxException {
        ServerPlayerEntity player = d.getSource().asPlayer();

        if (player.getRidingEntity() instanceof EntityVessel) {
            EntityVessel vessel = (EntityVessel) player.getRidingEntity();

            if (!vessel.disassemble(force)) {
                if (drop) {
                    vessel.dropAsItems();
                    d.getSource().sendFeedback(new StringTextComponent("Unable to disassemble vessel, dropped as items."), true);
                } else {
                    d.getSource().sendErrorMessage(new StringTextComponent("Failed to disassemble vessel, have you tried using force?"));
                }
            } else {
                d.getSource().sendFeedback(new StringTextComponent("Disassembled vessel."), true);
            }
            return 1;
        } else {
            d.getSource().sendErrorMessage(new StringTextComponent("Not steering a vessel, no disassembly possible."));
            return -1;
        }
    }
}
