package darkevilmac.archimedes.common.tileentity;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import darkevilmac.archimedes.client.LanguageEntries;
import darkevilmac.archimedes.common.object.ArchimedesObjects;
import darkevilmac.archimedes.common.util.NBTTagUtils;
import darkevilmac.movingworld.common.chunk.mobilechunk.MobileChunk;
import darkevilmac.movingworld.common.entity.EntityMovingWorld;
import darkevilmac.movingworld.common.tile.IMovingWorldTileEntity;

public class TileEntityAnchorPoint extends TileEntity implements IMovingWorldTileEntity, IInventory {

    public Instance instance;
    public ItemStack item;
    private EntityMovingWorld activeShip;

    public TileEntityAnchorPoint() {
        super();
        activeShip = null;
        instance = new Instance();
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
        if (instanceCompound.getBoolean("anchorInstance")) {
            instance = new Instance();
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

    public enum InstanceType {
        FORSHIP, FORLAND;

        @Override
        public String toString() {
            if (FMLLaunchHandler.side().isClient())
                return this == FORSHIP ? I18n.format(LanguageEntries.GUI_ANCHOR_MODE_SHIP) : I18n.format(LanguageEntries.GUI_ANCHOR_MODE_WORLD);
            else return super.toString();
        }

        public InstanceType opposite() {
            return this == FORSHIP ? FORLAND : FORSHIP;
        }
    }

    public class Instance implements INBTSerializable<NBTTagCompound> {
        /**
         * A unique identifier for this anchor, essentially a verification check for dimensions.
         */
        private UUID identifier;
        private InstanceType type;
        /**
         * The anchors related to our instance, stores their position in world as well as their
         * UUID, used for checking if we're in range as well as notifying an anchor if one is
         * removed from the world.
         **/
        private Map<UUID, BlockPos> relatedAnchors;

        public Instance() {
            this.identifier = UUID.randomUUID();
            this.type = InstanceType.FORLAND;
            this.relatedAnchors = new HashMap<UUID, BlockPos>();
        }

        public UUID getIdentifier() {
            return identifier;
        }

        public void setIdentifier(UUID identifier) {
            this.identifier = identifier;
        }

        public InstanceType getType() {
            return type;
        }

        public void setType(InstanceType type) {
            this.type = type;
        }

        public Map<UUID, BlockPos> getRelatedAnchors() {
            return relatedAnchors;
        }

        public void setRelatedAnchors(Map<UUID, BlockPos> relatedAnchors) {
            this.relatedAnchors = relatedAnchors;
        }


        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setBoolean("anchorInstance", true);
            tag.setBoolean("type", type == InstanceType.FORSHIP);
            tag.setUniqueId("identifier", identifier);


            if (!relatedAnchors.isEmpty()) {
                NBTTagCompound relatedAnchorsCompound = new NBTTagCompound();
                relatedAnchorsCompound.setInteger("size", relatedAnchors.size());

                int curEntry = 0;
                for (HashMap.Entry<UUID, BlockPos> e : relatedAnchors.entrySet()) {
                    NBTTagCompound entry = new NBTTagCompound();
                    entry.setUniqueId("identifier", e.getKey());
                    NBTTagUtils.writeVec3iToNBT(entry, "related", e.getValue());
                    relatedAnchorsCompound.setTag(String.valueOf(curEntry), entry);
                    curEntry++;
                }

                tag.setTag("relatedAnchorsCompound", relatedAnchorsCompound);
            }

            return tag;
        }

        @Override
        public void deserializeNBT(NBTTagCompound tag) {
            if (!tag.getBoolean("anchorInstance"))
                throw new IllegalArgumentException("NBT provided for deserialization is not valid for an anchor point! " + tag.toString());

            this.type = tag.getBoolean("type") ? InstanceType.FORSHIP : InstanceType.FORLAND;
            this.identifier = tag.getUniqueId("identifier");

            if (tag.hasKey("relatedAnchorsCompound")) {
                NBTTagCompound relatedAnchorsCompound = tag.getCompoundTag("relatedAnchorsCompound");
                int size = relatedAnchorsCompound.getInteger("size");

                for (int entry = 0; entry < size; entry++) {
                    NBTTagCompound entryCompound = relatedAnchorsCompound.getCompoundTag(String.valueOf(entry));
                    BlockPos entryPos = new BlockPos(NBTTagUtils.readVec3iFromNBT(entryCompound, "related"));
                    UUID entryIdentifier = entryCompound.getUniqueId("identifier");

                    relatedAnchors.put(entryIdentifier, entryPos);
                }
            }
        }
    }

}
