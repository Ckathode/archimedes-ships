package com.tridevmc.davincisvessels.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;

import java.util.List;

public class CommandDisassembleNear {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("dvtp")
                .requires(p -> p.hasPermissionLevel(3))
                .then(Commands.argument("range", DoubleArgumentType.doubleArg(1D, 64D))
                        .executes((c) -> execute(c, DoubleArgumentType.getDouble(c, "range"))))
                .executes((c) -> execute(c, 16D)));
    }

    private static int execute(CommandContext<CommandSource> d, double range) throws CommandSyntaxException {
        WorldServer world = d.getSource().getWorld();
        AxisAlignedBB area = new AxisAlignedBB(d.getSource().getPos(), d.getSource().getPos()).expand(range, 256, range);
        List<EntityShip> ships = world.getEntitiesWithinAABB(EntityShip.class, area);

        if (ships.isEmpty()) {
            d.getSource().sendErrorMessage(new TextComponentString("Found no ships within range to disassemble."));
        } else {
            for (EntityShip ship : ships) {
                if (!ship.disassemble(false)) {
                    d.getSource().sendErrorMessage(new TextComponentString("Failed to disassemble ship, dropping as items."));
                    ship.dropAsItems();
                }
            }
            d.getSource().sendFeedback(new TextComponentString(String.format("Disassembled %s ships.", ships.size())), true);
        }

        return ships.size();
    }

}
