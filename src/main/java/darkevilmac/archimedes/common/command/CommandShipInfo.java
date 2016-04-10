package darkevilmac.archimedes.common.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import darkevilmac.archimedes.common.entity.EntitySeat;
import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.entity.ShipCapabilities;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.Locale;

public class CommandShipInfo extends CommandBase {

    @Override
    public String getCommandName() {
        return "asinfo";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityShip ship = null;
        if (sender instanceof Entity) {
            Entity player = (Entity) sender;
            if (player.getRidingEntity() instanceof EntityShip) {
                ship = (EntityShip) player.getRidingEntity();
            } else if (player.getRidingEntity() instanceof EntitySeat) {
                ship = ((EntitySeat) player.getRidingEntity()).getParentShip();
            }
        }
        if (ship != null) {
            sender.addChatMessage(new TextComponentString(ChatFormatting.GREEN.toString() + ChatFormatting.BOLD.toString() + "Ship information"));
            sender.addChatMessage(new TextComponentString(String.format(Locale.ENGLISH, "Airship: %b", ship.getCapabilities().canFly())));
            sender.addChatMessage(new TextComponentString(String.format(Locale.ENGLISH, "Position: %.2f, %.2f, %.2f", ship.posX, ship.posY, ship.posZ)));
            sender.addChatMessage(new TextComponentString(String.format(Locale.ENGLISH, "Speed: %.2f km/h", ship.getHorizontalVelocity() * 20 * 3.6F)));
            float f = 100F * ((ShipCapabilities) ship.getCapabilities()).getBalloonCount() / ship.getCapabilities().getBlockCount();
            sender.addChatMessage(new TextComponentString(String.format(Locale.ENGLISH, "Block count: %d", ship.getCapabilities().getBlockCount())));
            sender.addChatMessage(new TextComponentString(String.format(Locale.ENGLISH, "Balloon count: %d", ((ShipCapabilities) ship.getCapabilities()).getBalloonCount())));
            sender.addChatMessage(new TextComponentString(String.format(Locale.ENGLISH, "Balloon percentage: %.0f%%", f)));
            sender.addChatMessage(new TextComponentString(""));
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
