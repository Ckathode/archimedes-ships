package darkevilmac.archimedes.common.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import darkevilmac.archimedes.client.LanguageEntries;
import darkevilmac.archimedes.common.object.ArchimedesObjects;
import darkevilmac.movingworld.common.chunk.mobilechunk.MobileChunk;
import darkevilmac.movingworld.common.entity.EntityMovingWorld;
import darkevilmac.movingworld.common.tile.IMovingWorldTileEntity;

public class TileEntityAnchorPoint extends TileEntity implements IMovingWorldTileEntity, IInventory {

    public AnchorInstance instance;
    public ItemStack item;
    private EntityMovingWorld activeShip;

    public TileEntityAnchorPoint() {
        super();
        activeShip = null;
        instance = new AnchorInstance();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (worldObj != null && tag.hasKey("vehicle") && worldObj != null) {
            int id = tag.getInteger("vehicle");
            Entity entity = worldObj.getEntityByID(id);
            if (entity != null && entity instanceof EntityMovingWorld) {
                activeShip = (EntityMovingWorld) entity;
            }
        }

        NBTTagCompound instanceCompound = tag.getCompoundTag("instance");
        if (instanceCompound.getBoolean("instance")) {
            instance = new AnchorInstance();
            instance.deserializeNBT(instanceCompound);
        }

        if (tag.hasKey("item")) {
            item = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item"));
        } else {
            item = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        if (activeShip != null && !activeShip.isDead) {
            tag.setInteger("vehicle", activeShip.getEntityId());
        }

        if (instance != null) {
            NBTTagCompound instanceCompound = instance.serializeNBT();
            tag.setTag("instance", instanceCompound);
        }

        if (item == null) {
            tag.removeTag("item");
        } else {
            item.writeToNBT(tag.getCompoundTag("item"));
        }

        return tag;
    }

    @Override
    public void setParentMovingWorld(BlockPos pos, EntityMovingWorld entityMovingWorld) {
        activeShip = entityMovingWorld;
    }

    @Override
    public EntityMovingWorld getParentMovingWorld() {
        return activeShip;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(new BlockPos(BlockPos.ORIGIN), entityMovingWorld);
    }

    @Override
    public void tick(MobileChunk mobileChunk) {
        // No implementation
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }


    @Override
    public ItemStack getStackInSlot(int index) {
        if (index == 0) return item;
        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index == 0 && item != null) {
            item.stackSize -= count;
            return item;
        }
        return null;
    }


    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index == 0 && item != null) {
            ItemStack oldStack = item.copy();
            item = null;
            return oldStack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 0) {
            item = stack;
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && stack != null && stack.getItem() != null &&
                stack.getItem().equals(Item.getItemFromBlock(ArchimedesObjects.blockAnchorPoint));
    }

    @Override
    public int getField(int id) {
        // Very useless methods that were implemented because mojang I guess.
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        // Very useless methods that were implemented because mojang I guess.
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        item = null;
    }

    @Override
    public String getName() {
        return LanguageEntries.CONTAINER_ANCHOR;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("TileEntityAnchorPoint at {X: %i Y: %i Z: %i} with state {%s}", pos.getX(), pos.getY(), pos.getZ(), worldObj.getBlockState(pos));
    }

}
