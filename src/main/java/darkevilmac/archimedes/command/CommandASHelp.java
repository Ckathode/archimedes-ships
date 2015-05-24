package darkevilmac.archimedes.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandASHelp extends CommandBase {
    public static List<CommandBase> asCommands = new ArrayList<CommandBase>();

    @Override
    public String getName() {
        return "ashelp";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUse(ICommandSender icommandsender) {
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("as?");
    }

    @Override
    public void execute(ICommandSender icommandsender, String[] astring) {
        icommandsender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA.toString() + "Archimedes' Ships mod commands:"));
        for (CommandBase cb : asCommands) {
            icommandsender.addChatMessage(new ChatComponentText(cb.getCommandUsage(icommandsender)));
        }
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/ashelp OR /as?";
    }
}
