package darkevilmac.archimedes.common.command;

import darkevilmac.archimedes.common.entity.EntitySeat;
import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.entity.ShipCapabilities;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Locale;

public class CommandShipInfo extends CommandBase {

    @Override
    public String getCommandName() {
        return "asinfo";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        EntityShip ship = null;
        if (icommandsender instanceof Entity) {
            Entity player = (Entity) icommandsender;
            if (player.ridingEntity instanceof EntityShip) {
                ship = (EntityShip) player.ridingEntity;
            } else if (player.ridingEntity instanceof EntitySeat) {
                ship = ((EntitySeat) player.ridingEntity).getParentShip();
            }
        }
        if (ship != null) {
            icommandsender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD.toString() + "Ship information"));
            icommandsender.addChatMessage(new ChatComponentText(String.format(Locale.ENGLISH, "Airship: %b", ship.getCapabilities().canFly())));
            icommandsender.addChatMessage(new ChatComponentText(String.format(Locale.ENGLISH, "Position: %.2f, %.2f, %.2f", ship.posX, ship.posY, ship.posZ)));
            icommandsender.addChatMessage(new ChatComponentText(String.format(Locale.ENGLISH, "Speed: %.2f km/h", ship.getHorizontalVelocity() * 20 * 3.6F)));
            float f = 100F * ((ShipCapabilities) ship.getCapabilities()).getBalloonCount() / ship.getCapabilities().getBlockCount();
            icommandsender.addChatMessage(new ChatComponentText(String.format(Locale.ENGLISH, "Block count: %d", ship.getCapabilities().getBlockCount())));
            icommandsender.addChatMessage(new ChatComponentText(String.format(Locale.ENGLISH, "Balloon count: %d", ((ShipCapabilities) ship.getCapabilities()).getBalloonCount())));
            icommandsender.addChatMessage(new ChatComponentText(String.format(Locale.ENGLISH, "Balloon percentage: %.0f%%", f)));
            icommandsender.addChatMessage(new ChatComponentText(""));
            return;
        }
        icommandsender.addChatMessage(new ChatComponentText("Not steering a ship"));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
        return icommandsender instanceof Entity;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/".concat(getCommandName());
    }
}
