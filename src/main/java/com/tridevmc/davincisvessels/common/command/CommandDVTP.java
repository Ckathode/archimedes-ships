package com.tridevmc.davincisvessels.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;


public class CommandDVTP {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("dvtp")
                .requires(p -> p.hasPermissionLevel(2))
                .then(Commands.argument("location", Vec3Argument.vec3())
                        .executes(CommandDVTP::execute)));
    }

    private static int execute(CommandContext<CommandSource> d) throws CommandSyntaxException {
        EntityPlayerMP player = d.getSource().asPlayer();
        if (player.getRidingEntity() instanceof EntityShip) {
            EntityShip ship = (EntityShip) player.getRidingEntity();
            Vec3d location = Vec3Argument.getVec3(d, "location");
            ship.setPosition(location.x, location.y, location.z);
            ship.alignToGrid(false);
            return 1;
        } else {
            d.getSource().sendErrorMessage(new TextComponentString("Not steering a ship, unable to teleport."));
            return -1;
        }
    }

}