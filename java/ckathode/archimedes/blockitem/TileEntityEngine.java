package ckathode.archimedes.blockitem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityEngine extends TileEntity implements IInventory
{
	ItemStack[]		itemstacks;
	private int		burnTime;
	private boolean	running;
	
	public float	enginePower;
	public int		engineFuelConsumption;
	
	public TileEntityEngine()
	{
		itemstacks = new ItemStack[getSizeInventory()];
		burnTime = 0;
		running = false;
	}
	
	public TileEntityEngine(float power, int fuelconsumption)
	{
		this();
		
		enginePower = power;
		engineFuelConsumption = fuelconsumption;
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound compound = new NBTTagCompound();
		writeToNBT(compound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
		readFromNBT(packet.func_148857_g());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		burnTime = compound.getInteger("burn");
		engineFuelConsumption = compound.getShort("fuelCons");
		enginePower = compound.getFloat("power");
		NBTTagList list = compound.getTagList("inv", 10);
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound comp = list.getCompoundTagAt(i);
			int j = comp.getByte("i");
			itemstacks[j] = ItemStack.loadItemStackFromNBT(comp);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		compound.setInteger("burn", burnTime);
		compound.setShort("fuelCons", (short) engineFuelConsumption);
		compound.setFloat("power", enginePower);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++)
		{
			if (itemstacks[i] != null)
			{
				NBTTagCompound comp = new NBTTagCompound();
				comp.setByte("i", (byte) i);
				itemstacks[i].writeToNBT(comp);
				list.appendTag(comp);
			}
		}
		compound.setTag("inv", list);
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public int getBurnTime()
	{
		return burnTime;
	}
	
	public void updateRunning()
	{
		running = consumeFuel(engineFuelConsumption);
	}
	
	public boolean consumeFuel(int f)
	{
		if (burnTime >= f)
		{
			burnTime -= f;
			return true;
		}
		
		for (int i = 0; i < getSizeInventory(); i++)
		{
			ItemStack is = decrStackSize(i, 1);
			if (is != null && is.stackSize > 0)
			{
				burnTime += TileEntityFurnace.getItemBurnTime(is);
				return consumeFuel(f);
			}
		}
		return false;
	}
	
	@Override
	public boolean canUpdate()
	{
		return false;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 4;
	}
	
	@Override
	public ItemStack getStackInSlot(int i)
	{
		return i >= 0 && i < 4 ? itemstacks[i] : null;
	}
	
	@Override
	public ItemStack decrStackSize(int i, int n)
	{
		if (itemstacks[i] != null)
		{
			ItemStack itemstack;
			
			if (itemstacks[i].stackSize <= n)
			{
				itemstack = itemstacks[i];
				itemstacks[i] = null;
				markDirty();
				return itemstack;
			}
			
			itemstack = itemstacks[i].splitStack(n);
			if (itemstacks[i].stackSize <= 0)
			{
				itemstacks[i] = null;
			}
			
			markDirty();
			return itemstack;
		}
		return null;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		return getStackInSlot(i);
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack is)
	{
		if (i >= 0 && i < 4)
		{
			itemstacks[i] = is;
		}
	}
	
	@Override
	public String getInventoryName()
	{
		return "Engine Inventory";
	}
	
	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public void markDirty()
	{
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5d, yCoord + 0.5d, zCoord + 0.5d) <= 64d;
	}
	
	@Override
	public void openInventory()
	{
	}
	
	@Override
	public void closeInventory()
	{
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack is)
	{
		return i >= 0 && i < 4 && TileEntityFurnace.isItemFuel(is);
	}
}
