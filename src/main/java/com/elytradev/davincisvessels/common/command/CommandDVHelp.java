package com.elytradev.davincisvessels.common.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandDVHelp extends CommandBase {
    public static List<CommandBase> asCommands = new ArrayList<CommandBase>();

    @Override
    public String getName() {
        return "dvhelp";
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
    public List<String> getAliases() {
        return Arrays.asList("dv?");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(new TextComponentString(ChatFormatting.AQUA.toString() + "Archimedes' Ships mod commands:"));
        for (CommandBase cb : asCommands) {
            sender.sendMessage(new TextComponentString(cb.getUsage(sender)));
        }
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "/dvhelp OR /dv?";
    }
}
