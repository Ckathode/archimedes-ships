package com.tridevmc.davincisvessels.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.ServerWorld;

import java.util.List;

public class CommandDisassembleNear {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("dvdisassemblenear")
                .requires(p -> p.hasPermissionLevel(3))
                .then(Commands.argument("range", DoubleArgumentType.doubleArg(1D, 64D))
                        .executes((c) -> execute(c, DoubleArgumentType.getDouble(c, "range"))))
                .executes((c) -> execute(c, 16D)));
    }

    private static int execute(CommandContext<CommandSource> d, double range) throws CommandSyntaxException {
        ServerWorld world = d.getSource().getWorld();
        AxisAlignedBB area = new AxisAlignedBB(d.getSource().getPos(), d.getSource().getPos()).expand(range, 256, range);
        List<EntityVessel> vessels = world.getEntitiesWithinAABB(EntityVessel.class, area);

        if (vessels.isEmpty()) {
            d.getSource().sendErrorMessage(new StringTextComponent("Found no vessels within range to disassemble."));
        } else {
            for (EntityVessel vessel : vessels) {
                if (!vessel.disassemble(false)) {
                    d.getSource().sendErrorMessage(new StringTextComponent("Failed to disassemble vessel, dropping as items."));
                    vessel.dropAsItems();
                }
            }
            d.getSource().sendFeedback(new StringTextComponent(String.format("Disassembled %s vessels.", vessels.size())), true);
        }

        return vessels.size();
    }

}
