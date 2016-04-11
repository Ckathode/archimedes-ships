package darkevilmac.archimedes.common.command;

import darkevilmac.archimedes.common.entity.EntityShip;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandDisassembleShip extends CommandBase {
    @Override
    public String getCommandName() {
        return "asdisassemble";
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
                        sender.addChatMessage(new TextComponentString("Overwriting existing objects with ship objects"));
                        mode = 1;
                    } else if (args[1].equals("drop")) {
                        sender.addChatMessage(new TextComponentString("Dropping to items if rejoining ship with the world fails"));
                        mode = 2;
                    }
                } else {
                    sender.addChatMessage(new TextComponentString("Trying to add ship objects to world"));
                }

                if (!ship.disassemble(mode == 1)) {
                    if (mode == 2) {
                        ship.dropAsItems();
                    }
                }
                player.startRiding(null);
                return;
            }
        }
        sender.addChatMessage(new TextComponentString("Not steering a ship"));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender != null && sender instanceof Entity;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/" + getCommandName() + " [overwrite OR drop]";
    }
}
