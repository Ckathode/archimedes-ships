package ckathode.archimedes.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandASHelp extends CommandBase
{
	public static List<CommandBase>	asCommands	= new ArrayList<CommandBase>();
	
	@Override
	public String getCommandName()
	{
		return "ashelp";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender)
	{
		return true;
	}
	
	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList(new String[] { "as?" });
	}
	
	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring)
	{
		icommandsender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA.toString() + "Archimedes' Ships mod commands:"));
		for (CommandBase cb : asCommands)
		{
			icommandsender.addChatMessage(new ChatComponentText(cb.getCommandUsage(icommandsender)));
		}
	}
	
	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/ashelp OR /as?";
	}
}
