package com.tridevmc.davincisvessels.common.tileentity;

import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.client.gui.ContainerEngine;
import com.tridevmc.davincisvessels.client.gui.GuiEngine;
import com.tridevmc.davincisvessels.common.IElementProvider;
import com.tridevmc.davincisvessels.common.api.tileentity.ITileEngineModifier;
import com.tridevmc.davincisvessels.common.entity.ShipCapabilities;
import com.tridevmc.movingworld.common.chunk.mobilechunk.MobileChunk;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class TileEngine extends TileEntity implements IInventory, ITileEngineModifier, IElementProvider<ContainerEngine> {
    public float enginePower;
    public int engineFuelConsumption;
    ItemStack[] itemStacks;
    private int burnTime;
    private boolean running;
    private BlockPos chunkPos;

    public TileEngine() {
        super(DavincisVesselsMod.CONTENT.tileTypes.get(TileEngine.class));
        itemStacks = new ItemStack[getSizeInventory()];
        for (int i = 0; i < itemStacks.length; i++) {
            itemStacks[i] = ItemStack.EMPTY;
        }
        burnTime = 0;
        running = false;
    }

    public TileEngine(float power, int fuelconsumption) {
        this();

        enginePower = power;
        engineFuelConsumption = fuelconsumption;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (!tag.contains("fuelConsumption"))
            tag.putInt("fuelConsumption", DavincisVesselsMod.CONFIG.engineConsumptionRate);

        burnTime = tag.getInt("burn");
        engineFuelConsumption = tag.getInt("fuelConsumption");
        enginePower = tag.getFloat("power");
        ListNBT list = tag.getList("inv", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT comp = list.getCompound(i);
            int j = comp.getByte("i");
            itemStacks[j] = ItemStack.read(comp);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);
        tag.putInt("burn", burnTime);
        tag.putInt("fuelConsumption", (short) engineFuelConsumption);
        tag.putFloat("power", enginePower);
        ListNBT list = new ListNBT();
        for (int i = 0; i < getSizeInventory(); i++) {
            if (itemStacks[i] != ItemStack.EMPTY) {
                CompoundNBT comp = new CompoundNBT();
                comp.putByte("i", (byte) i);
                itemStacks[i].write(comp);
                list.add(comp);
            }
        }
        tag.put("inv", list);
        return tag;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT compound = new CompoundNBT();
        write(compound);
        return new SUpdateTileEntityPacket(pos, 1, compound);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Engine Inventory");
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        read(packet.getNbtCompound());
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
            if (is != ItemStack.EMPTY && is.getCount() > 0) {
                burnTime += FurnaceTileEntity.getBurnTimes().get(is);
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
    public boolean isEmpty() {
        for (ItemStack itemstack : this.itemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return i >= 0 && i < 4 ? itemStacks[i] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int i, int n) {
        if (itemStacks[i] != ItemStack.EMPTY) {
            ItemStack itemstack;

            if (itemStacks[i].getCount() <= n) {
                itemstack = itemStacks[i];
                itemStacks[i] = ItemStack.EMPTY;
                markDirty();
                return itemstack;
            }

            itemstack = itemStacks[i].split(n);
            if (itemStacks[i].getCount() <= 0) {
                itemStacks[i] = ItemStack.EMPTY;
            }

            markDirty();
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        ItemStack content = itemStacks[i].copy();
        itemStacks[i] = ItemStack.EMPTY;
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
    public boolean isUsableByPlayer(PlayerEntity player) {
        return world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d) <= 64d;
    }

    @Override
    public void openInventory(PlayerEntity player) {
    }

    @Override
    public void closeInventory(PlayerEntity player) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack is) {
        return i >= 0 && i < 4 && FurnaceTileEntity.func_213991_b(is);
    }

    @Override
    public void clear() {
        itemStacks = new ItemStack[getSizeInventory()];
    }

    @Override
    public float getPowerIncrement(ShipCapabilities shipCapabilities) {
        return isRunning() ? enginePower : 0;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld movingWorld, BlockPos chunkPos) {
        // We don't bother with our parent.

        this.chunkPos = pos;
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
    public BlockPos getChunkPos() {
        return chunkPos;
    }

    @Override
    public void setChunkPos(BlockPos chunkPos) {

    }

    @Override
    public void tick(MobileChunk mobileChunk) {
        running = consumeFuel(engineFuelConsumption);
    }

    @Override
    public Container createMenu(int window, PlayerInventory playerInventory, PlayerEntity player) {
        return new ContainerEngine(window, this, player);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Screen createScreen(ContainerEngine container, PlayerEntity player) {
        return new GuiEngine(container);
    }
}
