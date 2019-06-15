package com.tridevmc.davincisvessels.common.tileentity;

import com.google.common.collect.Lists;
import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.client.gui.ContainerAnchorPoint;
import com.tridevmc.davincisvessels.client.gui.GuiAnchorPoint;
import com.tridevmc.davincisvessels.common.IElementProvider;
import com.tridevmc.movingworld.api.IMovingTile;
import com.tridevmc.movingworld.common.chunk.mobilechunk.MobileChunk;
import com.tridevmc.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.Objects;

public class TileAnchorPoint extends TileEntity implements IMovingTile, IInventory, ITickable, IElementProvider<ContainerAnchorPoint> {

    public ItemStack content;
    public BlockPos chunkPos;
    private AnchorInstance instance;
    private EntityMovingWorld activeShip;

    public TileAnchorPoint() {
        super(DavincisVesselsMod.CONTENT.tileTypes.get(TileAnchorPoint.class));
        activeShip = null;
        instance = new AnchorInstance();
        content = ItemStack.EMPTY;
    }

    public static boolean isItemAnchor(ItemStack itemstack) {
        return itemstack != ItemStack.EMPTY && Objects.equals(itemstack.getItem(), Item.getItemFromBlock(DavincisVesselsMod.CONTENT.blockAnchorPoint));
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        read(packet.getNbtCompound());
        world.markForRerender(pos);

        if (FMLEnvironment.dist.isClient()) {
            if (Minecraft.getInstance().currentScreen instanceof GuiAnchorPoint) {
                GuiAnchorPoint activeGUI = (GuiAnchorPoint) Minecraft.getInstance().currentScreen;
                if (Objects.equals(activeGUI.anchorPoint.pos, this.pos)) {
                    activeGUI.init();
                }
            }
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (world != null && tag.contains("vehicle") && world != null) {
            int id = tag.getInt("vehicle");
            Entity entity = world.getEntityByID(id);
            if (entity instanceof EntityMovingWorld) {
                activeShip = (EntityMovingWorld) entity;
            }
        }

        CompoundNBT instanceCompound = tag.getCompound("INSTANCE");
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
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);
        if (activeShip != null && !activeShip.isAlive()) {
            tag.putInt("vehicle", activeShip.getEntityId());
        }

        if (instance != null) {
            CompoundNBT instanceCompound = instance.serializeNBT();
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
        setParentMovingWorld(entityMovingWorld, new BlockPos(BlockPos.ZERO));
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
    public boolean isUsableByPlayer(PlayerEntity player) {
        return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(PlayerEntity player) {

    }

    @Override
    public void closeInventory(PlayerEntity player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        boolean accepted = index == 0 &&
                (stack == ItemStack.EMPTY || Objects.equals(stack.getItem(), Item.getItemFromBlock(DavincisVesselsMod.CONTENT.blockAnchorPoint)));
        return accepted;
    }

    @Override
    public void clear() {
        content = ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        if (instance != null && instance.hasChanged()) {
            instance.setChanged(false);

            if (world instanceof ServerWorld) {
                world.getChunk(pos).markDirty();
                markDirty();
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Screen createScreen(ContainerAnchorPoint container, PlayerEntity player) {
        return new GuiAnchorPoint(container);
    }

    @Nullable
    @Override
    public ContainerAnchorPoint createMenu(int window, PlayerInventory playerInv, PlayerEntity player) {
        return new ContainerAnchorPoint(window, this, player);
    }

    public enum AnchorPointAction {
        LINK, SWITCH
    }
}
