package darkevilmac.archimedes.common.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class FuelInventory implements IInventory {

    private EntityShip ship;
    private ItemStack[] itemstacks;

    public FuelInventory(EntityShip entityship) {
        ship = entityship;
        itemstacks = new ItemStack[getSizeInventory()];
    }

    @Override
    public int getSizeInventory() {
        return 4;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return i >= 0 && i < 4 ? itemstacks[i] : null;
    }

    @Override
    public ItemStack decrStackSize(int i, int n) {
        if (itemstacks[i] != null) {
            ItemStack itemstack;

            if (itemstacks[i].stackSize <= n) {
                itemstack = itemstacks[i];
                itemstacks[i] = null;
                markDirty();
                return itemstack;
            }

            itemstack = itemstacks[i].splitStack(n);
            if (itemstacks[i].stackSize <= 0) {
                itemstacks[i] = null;
            }

            markDirty();
            return itemstack;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        ItemStack content = itemstacks[i].copy();
        itemstacks[i] = null;

        return content;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack is) {
        if (i >= 0 && i < 4) {
            itemstacks[i] = is;
        }
    }


    @Override
    public String getName() {
        return "Engine Inventory";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString("Engine Inventory");
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
        return player.getRidingEntity() == ship;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer playe) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack is) {
        return i >= 0 && i < 4 && TileEntityFurnace.isItemFuel(is);
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

}
