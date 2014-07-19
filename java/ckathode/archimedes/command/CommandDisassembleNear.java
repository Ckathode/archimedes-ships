package ckathode.archimedes.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import ckathode.archimedes.entity.EntityShip;

public class CommandDisassembleNear extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "asdisassemblenear";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring)
	{
		if (icommandsender instanceof Entity)
		{
			double range = 16D;
			if (astring != null && astring.length > 0)
			{
				try
				{
					range = Integer.parseInt(astring[0]);
				} catch (NumberFormatException e)
				{
					throw new NumberInvalidException();
				}
			}
			double rangesqrd = range * range;
			
			Entity player = (Entity) icommandsender;
			EntityShip ne = null;
			if (player.ridingEntity instanceof EntityShip)
			{
				ne = (EntityShip) player.ridingEntity;
			} else
			{
				double nd = 0D;
				double d;
				for (Entity entity : (List<Entity>) player.worldObj.getLoadedEntityList())
				{
					if (entity instanceof EntityShip)
					{
						d = player.getDistanceSqToEntity(entity);
						if (d < rangesqrd && (ne == null || d < nd))
						{
							ne = (EntityShip) entity;
							nd = d;
						}
					}
				}
			}
			
			if (ne == null)
			{
				icommandsender.addChatMessage(new ChatComponentText("No ship in a " + ((int) range) + " blocks' range"));
				return;
			}
			if (!ne.disassemble(false))
			{
				icommandsender.addChatMessage(new ChatComponentText("Failed to disassemble ship; dropping to items"));
				ne.dropAsItems();
			}
			ne.setDead();
		}
	}
	
	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/" + getCommandName() + " [range]";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 3;
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender)
	{
		return icommandsender instanceof Entity && super.canCommandSenderUseCommand(icommandsender);
	}
}
