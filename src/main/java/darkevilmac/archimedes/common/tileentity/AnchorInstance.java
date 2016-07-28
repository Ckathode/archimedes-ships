package darkevilmac.archimedes.common.tileentity;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import darkevilmac.archimedes.client.LanguageEntries;
import darkevilmac.archimedes.common.util.NBTTagUtils;

public class AnchorInstance implements INBTSerializable<NBTTagCompound> {
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

    public AnchorInstance() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorInstance that = (AnchorInstance) o;
        return Objects.equals(getIdentifier(), that.getIdentifier()) &&
                getType() == that.getType() &&
                Objects.equals(getRelatedAnchors(), that.getRelatedAnchors());
    }


    public void setRelatedAnchors(Map<UUID, BlockPos> relatedAnchors) {
        this.relatedAnchors = relatedAnchors;
    }

    public void addRelation(UUID identifier, BlockPos position) {
        relatedAnchors.put(identifier, position);
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
    }

    public void removeRelation(UUID identifier) {
        relatedAnchors.remove(identifier);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setBoolean("instance", true);
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
        if (!tag.getBoolean("instance"))
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
}
