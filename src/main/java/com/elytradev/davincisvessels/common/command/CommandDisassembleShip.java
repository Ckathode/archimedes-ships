package com.elytradev.davincisvessels.common.command;

import com.elytradev.davincisvessels.common.entity.EntityShip;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandDisassembleShip extends CommandBase {
    @Override
    public String getName() {
        return "dvdisassemble";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof Entity) {
            Entity player = (Entity) sender;
            if (player.getRidingEntity() instanceof EntityShip) {
                EntityShip ship = (EntityShip) player.getRidingEntity();
                int mode = 0;
                if (args != null && args.length > 2) {
                    if (args[0].equals("overwrite") || args[0].equals("override")) {
                        sender.sendMessage(new TextComponentString("Overwriting existing OBJECTS with ship OBJECTS"));
                        mode = 1;
                    } else if (args[1].equals("drop")) {
                        sender.sendMessage(new TextComponentString("Dropping to items if rejoining ship with the world fails"));
                        mode = 2;
                    }
                } else {
                    sender.sendMessage(new TextComponentString("Trying to add ship OBJECTS to world"));
                }

                if (!ship.disassemble(mode == 1)) {
                    if (mode == 2) {
                        ship.dropAsItems();
                    }
                }
                player.dismountRidingEntity();
                return;
            }
        }
        sender.sendMessage(new TextComponentString("Not steering a ship"));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof Entity;
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "/" + getName() + " [overwrite OR drop]";
    }
}
