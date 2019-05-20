package com.tridevmc.davincisvessels.common.tileentity;

import com.tridevmc.davincisvessels.client.gui.GuiAnchorPoint;
import com.tridevmc.davincisvessels.common.LanguageEntries;
import com.tridevmc.davincisvessels.common.content.DavincisVesselsContent;
import com.google.common.collect.Lists;
import com.tridevmc.movingworld.api.IMovingTile;
import com.tridevmc.movingworld.common.chunk.mobilechunk.MobileChunk;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.Objects;

public class TileAnchorPoint extends TileEntity implements IMovingTile, IInventory {

    public ItemStack content;
    public BlockPos chunkPos;
    private AnchorInstance instance;
    private EntityMovingWorld activeShip;

    public TileAnchorPoint() {
        super();
        activeShip = null;
        instance = new AnchorInstance();
        content = ItemStack.EMPTY;
    }

    public static boolean isItemAnchor(ItemStack itemstack) {
        return itemstack != ItemStack.EMPTY && Objects.equals(itemstack.getItem(), Item.getItemFromBlock(DavincisVesselsContent.blockAnchorPoint));
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.write(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        read(packet.getNbtCompound());
        world.markBlockRangeForRenderUpdate(pos, pos);

        if (FMLEnvironment.dist.isClient()) {
            if (Minecraft.getInstance().currentScreen instanceof GuiAnchorPoint) {
                GuiAnchorPoint activeGUI = (GuiAnchorPoint) Minecraft.getInstance().currentScreen;
                if (Objects.equals(activeGUI.anchorPoint.pos, this.pos)) {
                    activeGUI.initGui();
                }
            }
        }
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);
        if (world != null && tag.contains("vehicle") && world != null) {
            int id = tag.getInt("vehicle");
            Entity entity = world.getEntityByID(id);
            if (entity instanceof EntityMovingWorld) {
                activeShip = (EntityMovingWorld) entity;
            }
        }

        NBTTagCompound instanceCompound = tag.getCompound("INSTANCE");
        if (instanceCompound.getBoolean("INSTANCE")) {
            instance = new AnchorInstance();
            instance.deserializeNBT(instanceCompound);
        }

        if (tag.contains("item")) {
            content = ItemStack.read(tag.getCompound("item"));
        } else {
            content = ItemStack.EMPTY;
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        tag = super.write(tag);
        if (activeShip != null && !activeShip.isAlive()) {
            tag.putInt("vehicle", activeShip.getEntityId());
        }

        if (instance != null) {
            NBTTagCompound instanceCompound = instance.serializeNBT();
            tag.put("INSTANCE", instanceCompound);
        }

        if (content == ItemStack.EMPTY) {
            tag.remove("item");
        } else {
            content.write(tag.getCompound("item"));
        }

        return tag;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld movingWorld, BlockPos chunkPos) {
        chunkPos = pos;
        activeShip = movingWorld;
    }

    @Override
    public EntityMovingWorld getParentMovingWorld() {
        return activeShip;
    }

    @Override
    public void setParentMovingWorld(EntityMovingWorld entityMovingWorld) {
        setParentMovingWorld(entityMovingWorld, new BlockPos(BlockPos.ORIGIN));
    }

    public AnchorInstance getInstance() {
        return instance;
    }

    public void setInstance(AnchorInstance instance) {
        this.instance = instance;
        this.instance.setChanged(true);
    }

    @Override
    public BlockPos getChunkPos() {
        return chunkPos;
    }

    @Override
    public void setChunkPos(BlockPos chunkPos) {
        this.chunkPos = chunkPos;
    }

    @Override
    public void tick(MobileChunk mobileChunk) {
    }

    @Override
    public ITextComponent getName() {
        return new TextComponentTranslation(LanguageEntries.CONTAINER_ANCHOR);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return new TextComponentTranslation(LanguageEntries.CONTAINER_ANCHOR);
    }

    @Override
    public String toString() {
        return String.format("TileAnchorPoint at {X: %s Y: %s Z: %s} with state {%s} and INSTANCE {%s}", pos.getX(), pos.getY(), pos.getZ(), world.getBlockState(pos), instance.toString());
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Nullable
    @Override
    public ItemStack getStackInSlot(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        } else return content;
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack splitResult = ItemStackHelper.getAndSplit(Lists.newArrayList(content), index, count);
        content = splitResult;
        return splitResult;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack removeResult = ItemStackHelper.getAndRemove(Lists.newArrayList(content), index);
        content = removeResult;
        return removeResult;
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        if (index != 0)
            throw new IndexOutOfBoundsException();

        this.content = stack;
        if (stack != ItemStack.EMPTY && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        boolean accepted = index == 0 &&
                (stack == ItemStack.EMPTY || Objects.equals(stack.getItem(), Item.getItemFromBlock(DavincisVesselsContent.blockAnchorPoint)));
        return accepted;
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
        content = ItemStack.EMPTY;
    }

    @Override
    public void update() {
        if (instance != null && instance.hasChanged()) {
            instance.setChanged(false);

            if (world instanceof WorldServer) {
                ((WorldServer) world).getPlayerChunkMap().markBlockForUpdate(pos);
                markDirty();
            }
        }
    }

    public enum AnchorPointAction {
        LINK, SWITCH
    }
}
