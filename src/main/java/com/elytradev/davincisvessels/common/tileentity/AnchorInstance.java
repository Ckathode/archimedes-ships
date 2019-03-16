package com.elytradev.davincisvessels.common.tileentity;

import com.elytradev.davincisvessels.common.LanguageEntries;
import com.elytradev.davincisvessels.common.util.NBTTagUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AnchorInstance implements INBTSerializable<NBTTagCompound> {
    /**
     * A unique identifier for this anchor, essentially a verification check for dimensions.
     */
    private UUID identifier;
    private InstanceType type;
    private boolean changed;
    /**
     * The anchors related to our INSTANCE, stores their position in world as well as their
     * UUID, used for checking if we're in range as well as notifying an anchor if one is
     * removed from the world.
     **/
    private Map<UUID, BlockLocation> relatedAnchors;

    public AnchorInstance() {
        this.identifier = UUID.randomUUID();
        this.type = InstanceType.LAND;
        this.relatedAnchors = new HashMap<>();
        changed = false;
    }

    public boolean hasChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
        changed = true;
    }

    public InstanceType getType() {
        return type;
    }

    public void setType(InstanceType type) {
        this.type = type;
        changed = true;
    }

    public Map<UUID, BlockLocation> getRelatedAnchors() {
        return relatedAnchors;
    }

    public void setRelatedAnchors(Map<UUID, BlockLocation> relatedAnchors) {
        this.relatedAnchors = relatedAnchors;
        changed = true;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorInstance that = (AnchorInstance) o;
        return Objects.equals(getIdentifier(), that.getIdentifier()) &&
                getType() == that.getType() &&
                Objects.equals(getRelatedAnchors(), that.getRelatedAnchors());
    }

    public void addRelation(UUID identifier, BlockLocation location) {
        relatedAnchors.put(identifier, location);
        changed = true;

    }

    @Override
    public String toString() {
        return "AnchorInstance{" +
                "identifier=" + identifier +
                ", type=" + type +
                ", relatedAnchors=" + relatedAnchors +
                '}';
    }

    public void clearRelations() {
        relatedAnchors.clear();
        changed = true;

    }

    public void removeRelation(UUID identifier) {
        relatedAnchors.remove(identifier);
        changed = true;

    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.putBoolean("INSTANCE", true);
        tag.putBoolean("type", type == InstanceType.SHIP);
        tag.putUniqueId("identifier", identifier);

        if (!relatedAnchors.isEmpty()) {
            NBTTagList relatedAnchorsTagList = tag.getList("relatedAnchorsTagList", 10);

            for (HashMap.Entry<UUID, BlockLocation> e : relatedAnchors.entrySet()) {
                NBTTagCompound entry = new NBTTagCompound();
                entry.putUniqueId("identifier", e.getKey());
                entry.putInt("dim", e.getValue().getDim());
                NBTTagUtils.writeVec3iToNBT(entry, "related", e.getValue().getPos());
                relatedAnchorsTagList.add(entry);
            }

            tag.put("relatedAnchorsTagList", relatedAnchorsTagList);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        if (!tag.contains("INSTANCE"))
            throw new IllegalArgumentException("NBT provided for deserialization is not valid for an anchor point! " + tag.toString());

        this.type = tag.getBoolean("type") ? InstanceType.SHIP : InstanceType.LAND;
        this.identifier = tag.getUniqueId("identifier");

        if (tag.contains("relatedAnchorsTagList")) {
            NBTTagList relatedAnchorsTagList = tag.getList("relatedAnchorsTagList", 10);
            int size = relatedAnchorsTagList.size();

            for (int entry = 0; entry < size; entry++) {
                NBTTagCompound entryCompound = relatedAnchorsTagList.getCompound(entry);
                int entryDimID = entryCompound.getInt("dim");
                BlockPos entryPos = new BlockPos(NBTTagUtils.readVec3iFromNBT(entryCompound, "related"));
                UUID entryIdentifier = entryCompound.getUniqueId("identifier");

                relatedAnchors.put(entryIdentifier, new BlockLocation(entryPos, entryDimID));
            }
        }
    }


    public enum InstanceType {
        SHIP, LAND;

        @Override
        public String toString() {
            if (EffectiveSide.get() == LogicalSide.CLIENT)
                return this == SHIP ? I18n.format(LanguageEntries.GUI_ANCHOR_MODE_SHIP) : I18n.format(LanguageEntries.GUI_ANCHOR_MODE_WORLD);
            else return super.toString();
        }

        public InstanceType opposite() {
            switch (this) {
                case LAND: {
                    return SHIP;
                }
                case SHIP: {
                    return LAND;
                }
            }

            return this;
        }
    }
}
