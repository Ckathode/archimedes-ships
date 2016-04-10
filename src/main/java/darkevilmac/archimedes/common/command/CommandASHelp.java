package darkevilmac.archimedes.common.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandASHelp extends CommandBase {
    public static List<CommandBase> asCommands = new ArrayList<CommandBase>();

    @Override
    public String getCommandName() {
        return "ashelp";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("as?");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        sender.addChatMessage(new TextComponentString(ChatFormatting.AQUA.toString() + "Archimedes' Ships mod commands:"));
        for (CommandBase cb : asCommands) {
            sender.addChatMessage(new TextComponentString(cb.getCommandUsage(sender)));
        }
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/ashelp OR /as?";
    }
}
