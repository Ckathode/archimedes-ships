package com.elytradev.davincisvessels.common.command;

import com.elytradev.davincisvessels.common.entity.EntityShip;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandDisassembleNear extends CommandBase {
    @Override
    public String getName() {
        return "dvdisassemblenear";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof Entity) {
            double range = 16D;
            if (args != null && args.length > 0) {
                try {
                    range = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    try {
                        throw new NumberInvalidException();
                    } catch (NumberInvalidException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            double rangesqrd = range * range;

            Entity player = (Entity) sender;
            EntityShip ne = null;
            if (player.getRidingEntity() instanceof EntityShip) {
                ne = (EntityShip) player.getRidingEntity();
            } else {
                double nd = 0D;
                double d;
                for (Entity entity : player.world.getEntities(EntityShip.class, input -> true)) {
                    if (entity instanceof EntityShip) {
                        d = player.getDistanceSq(entity);
                        if (d < rangesqrd && (ne == null || d < nd)) {
                            ne = (EntityShip) entity;
                            nd = d;
                        }
                    }
                }
            }

            if (ne == null) {
                sender.sendMessage(new TextComponentString("No ship in a " + ((int) range) + " OBJECTS' range"));
                return;
            }
            if (!ne.disassemble(false)) {
                sender.sendMessage(new TextComponentString("Failed to disassemble ship; dropping to items"));
                ne.dropAsItems();
            }
            ne.setDead();
        }
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "/" + getName() + " [range]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof Entity && super.checkPermission(server, sender);
    }
}
