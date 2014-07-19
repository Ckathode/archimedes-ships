package ckathode.archimedes.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import ckathode.archimedes.entity.EntityShip;

public class CommandDisassembleShip extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "asdisassemble";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}
	
	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring)
	{
		if (icommandsender instanceof Entity)
		{
			Entity player = (Entity) icommandsender;
			if (player.ridingEntity instanceof EntityShip)
			{
				EntityShip ship = (EntityShip) player.ridingEntity;
				int mode = 0;
				if (astring != null && astring.length > 2)
				{
					if (astring[0].equals("overwrite") || astring[0].equals("override"))
					{
						icommandsender.addChatMessage(new ChatComponentText("Overwriting existing blocks with ship blocks"));
						mode = 1;
					} else if (astring[1].equals("drop"))
					{
						icommandsender.addChatMessage(new ChatComponentText("Dropping to items if rejoining ship with the world fails"));
						mode = 2;
					}
				} else
				{
					icommandsender.addChatMessage(new ChatComponentText("Trying to add ship blocks to world"));
				}
				
				if (!ship.disassemble(mode == 1))
				{
					if (mode == 2)
					{
						ship.dropAsItems();
					}
				}
				player.mountEntity(null);
				return;
			}
		}
		icommandsender.addChatMessage(new ChatComponentText("Not steering a ship"));
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender)
	{
		return icommandsender instanceof Entity;
	}
	
	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/" + getCommandName() + " [overwrite OR drop]";
	}
}
