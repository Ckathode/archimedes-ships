package darkevilmac.archimedes.common.tileentity;

import darkevilmac.archimedes.common.api.tileentity.ITileEngineModifier;
import darkevilmac.archimedes.common.entity.ShipCapabilities;
import darkevilmac.movingworld.common.chunk.mobilechunk.MobileChunk;
import darkevilmac.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;


public class TileEntityEngine extends TileEntity implements IInventory, ITileEngineModifier {
    public float enginePower;
    public int engineFuelConsumption;
    ItemStack[] itemStacks;
    private int burnTime;
    private boolean running;

    public TileEntityEngine() {
        itemStacks = new ItemStack[getSizeInventory()];
        burnTime = 0;
        running = false;
    }

    public TileEntityEngine(float power, int fuelconsumption) {
        this();

        enginePower = power;
        engineFuelConsumption = fuelconsumption;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        burnTime = compound.getInteger("burn");
        engineFuelConsumption = compound.getShort("fuelCons");
        enginePower = compound.getFloat("power");
        NBTTagList list = compound.getTagList("inv", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound comp = list.getCompoundTagAt(i);
            int j = comp.getByte("i");
            itemStacks[j] = ItemStack.loadItemStackFromNBT(comp);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("burn", burnTime);
        compound.setShort("fuelCons", (short) engineFuelConsumption);
        compound.setFloat("power", enginePower);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            if (itemStacks[i] != null) {
                NBTTagCompound comp = new NBTTagCompound();
                comp.setByte("i", (byte) i);
                itemStacks[i].writeToNBT(comp);
                list.appendTag(comp);
            }
        }
        compound.setTag("inv", list);
    }

    public boolean isRunning() {
        return running;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public boolean consumeFuel(int f) {
        if (burnTime >= f) {
            burnTime -= f;
            return true;
        }

        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack is = decrStackSize(i, 1);
            if (is != null && is.stackSize > 0) {
                burnTime += TileEntityFurnace.getItemBurnTime(is);
                return consumeFuel(f);
            }
        }
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 4;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return i >= 0 && i < 4 ? itemStacks[i] : null;
    }

    @Override
    public ItemStack decrStackSize(int i, int n) {
        if (itemStacks[i] != null) {
            ItemStack itemstack;

            if (itemStacks[i].stackSize <= n) {
                itemstack = itemStacks[i];
                itemStacks[i] = null;
                markDirty();
                return itemstack;
            }

            itemstack = itemStacks[i].splitStack(n);
            if (itemStacks[i].stackSize <= 0) {
                itemStacks[i] = null;
            }

            markDirty();
            return itemstack;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        ItemStack content = itemStacks[i].copy();
        itemStacks[i] = null;
        return content;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack is) {
        if (i >= 0 && i < 4) {
            itemStacks[i] = is;
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d) <= 64d;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack is) {
        return i >= 0 && i < 4 && TileEntityFurnace.isItemFuel(is);
    }

    @Override
    public int getField(int id) {
        return 0;
        // We have none.
    }

    @Override
    public void setField(int id, int value) {
        // We have none.
    }

    @Override
    public int getFieldCount() {
        return 0;
        // We have none.
    }

    @Override
    public void clear() {
        itemStacks = new ItemStack[getSizeInventory()];
    }

    @Override
    public String getName() {
        return "container.shipEngine";
    }

    @Override
    public boolean hasCustomName() {
        return false; //No custom names for this.
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString("Engine Inventory");
    }

    @Override
    public float getPowerIncrement(ShipCapabilities shipCapabilities) {
        return isRunning() ? enginePower : 0;
    }

    @Override
    public void setParentMovingWorld(BlockPos pos, EntityMovingWorld entityMovingWorld) {
        // We don't bother with our parent.
    }

    @Override
    public EntityMovingWorld getParentMovingWorld() {
        return null;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        // We don't bother with our parent.
    }

    @Override
    public void tick(MobileChunk mobileChunk) {
        running = consumeFuel(engineFuelConsumption);
    }
}
