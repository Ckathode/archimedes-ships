package elytra.davincisvessels.common.tileentity;

import elytra.davincisvessels.client.LanguageEntries;
import elytra.davincisvessels.client.gui.GuiAnchorPoint;
import elytra.davincisvessels.common.object.DavincisVesselsObjects;
import darkevilmac.movingworld.api.IMovingWorldTileEntity;
import darkevilmac.movingworld.common.chunk.mobilechunk.MobileChunk;
import darkevilmac.movingworld.common.entity.EntityMovingWorld;
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
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import javax.annotation.Nullable;
import java.util.Objects;

public class TileEntityAnchorPoint extends TileEntity implements IMovingWorldTileEntity, IInventory, ITickable {


    private AnchorInstance instance;
    public ItemStack content;

    public BlockPos chunkPos;
    private EntityMovingWorld activeShip;

    public TileEntityAnchorPoint() {
        super();
        activeShip = null;
        instance = new AnchorInstance();
        content = null;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        worldObj.markBlockRangeForRenderUpdate(pos, pos);

        if (FMLLaunchHandler.side().isClient()) {
            if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiAnchorPoint) {
                GuiAnchorPoint activeGUI = (GuiAnchorPoint) Minecraft.getMinecraft().currentScreen;
                if (Objects.equals(activeGUI.anchorPoint.pos, this.pos)) {
                    activeGUI.initGui();
                }
            }
        }
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
            content = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item"));
        } else {
            content = null;
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

        if (content == null) {
            tag.removeTag("item");
        } else {
            content.writeToNBT(tag.getCompoundTag("item"));
        }

        return tag;
    }

    @Override
    public void setParentMovingWorld(BlockPos pos, EntityMovingWorld entityMovingWorld) {
        chunkPos = pos;
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
    public void tick(MobileChunk mobileChunk) {
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
        return String.format("TileEntityAnchorPoint at {X: %s Y: %s Z: %s} with state {%s} and instance {%s}", pos.getX(), pos.getY(), pos.getZ(), worldObj.getBlockState(pos), instance.toString());
    }

    @Override
    public int getSizeInventory() {
        return 1;
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
        ItemStack splitResult = ItemStackHelper.getAndSplit(new ItemStack[]{content}, index, count);
        content = splitResult;
        return splitResult;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack removeResult = ItemStackHelper.getAndRemove(new ItemStack[]{content}, index);
        content = removeResult;
        return removeResult;
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        if (index != 0)
            throw new IndexOutOfBoundsException();

        this.content = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
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
        boolean accepted = index == 0 &&
                (stack == null || Objects.equals(stack.getItem(), Item.getItemFromBlock(DavincisVesselsObjects.blockAnchorPoint)));
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
        content = null;
    }

    public static boolean isItemAnchor(ItemStack itemstack) {
        return itemstack != null && Objects.equals(itemstack.getItem(), Item.getItemFromBlock(DavincisVesselsObjects.blockAnchorPoint));
    }

    @Override
    public void update() {
        if (instance != null && instance.hasChanged()) {
            instance.setChanged(false);

            if (worldObj instanceof WorldServer) {
                ((WorldServer) worldObj).getPlayerChunkMap().markBlockForUpdate(pos);
                markDirty();
            }
        }
    }
}
