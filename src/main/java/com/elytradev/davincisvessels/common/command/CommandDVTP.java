package com.elytradev.davincisvessels.common.command;

import com.elytradev.davincisvessels.common.entity.EntityShip;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;


public class CommandDVTP extends CommandBase {

    @Override
    public String getName() {
        return "dvtp";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityShip ship = null;
        if (sender instanceof Entity) {
            Entity player = (Entity) sender;
            if (player.getRidingEntity() instanceof EntityShip) {
                ship = (EntityShip) player.getRidingEntity();
            }
        }
        if (ship != null) {
            Vec3d vec3d = sender.getPositionVector();

            CommandBase.CoordinateArg coordArgX = parseCoordinate(vec3d.x, args[0], true);
            CommandBase.CoordinateArg coordArgY = parseCoordinate(vec3d.y, args[1], -4096, 4096, false);
            CommandBase.CoordinateArg coordArgZ = parseCoordinate(vec3d.z, args[2], true);

            ship.setPosition(coordArgX.getResult(), coordArgY.getResult(), coordArgZ.getResult());
            ship.alignToGrid(false);
            return;
        }
        sender.sendMessage(new TextComponentString("Not steering a ship"));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof Entity;
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "/".concat(getName());
    }
}