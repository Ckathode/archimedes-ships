package darkevilmac.archimedes.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;

import darkevilmac.archimedes.common.entity.EntityShip;


public class CommandASTP extends CommandBase {

    @Override
    public String getCommandName() {
        return "astp";
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

            CommandBase.CoordinateArg coordArgX = parseCoordinate(vec3d.xCoord, args[0], true);
            CommandBase.CoordinateArg coordArgY = parseCoordinate(vec3d.yCoord, args[1], -4096, 4096, false);
            CommandBase.CoordinateArg coordArgZ = parseCoordinate(vec3d.zCoord, args[2], true);

            ship.setPosition(coordArgX.getResult(), coordArgY.getResult(), coordArgZ.getResult());
            ship.alignToGrid(false);
            return;
        }
        sender.addChatMessage(new TextComponentString("Not steering a ship"));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender != null && sender instanceof Entity;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/".concat(getCommandName());
    }
}