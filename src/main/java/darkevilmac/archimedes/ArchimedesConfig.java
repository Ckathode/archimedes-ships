package darkevilmac.archimedes;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import darkevilmac.movingworld.MaterialDensity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import java.util.regex.Pattern;

public class ArchimedesConfig {
    public static final int CONTROL_TYPE_VANILLA = 0, CONTROL_TYPE_ARCHIMEDES = 1;
    //Settings
    public boolean enableAirShips;
    public int shipEntitySyncRate;
    //Mobile Chunk
    public int maxShipChunkBlocks;
    public float flyBalloonRatio;
    public boolean connectDiagonalBlocks;
    public boolean useNewAlgorithm;
    //Control
    public int shipControlType;
    public float turnSpeed;
    public float speedLimit;
    public float bankingMultiplier;
    @SideOnly(Side.CLIENT)
    public KeyBinding kbUp, kbDown, kbBrake, kbAlign, kbDisassemble, kbShipInv;
    public boolean disassembleOnDismount;
    public boolean enginesMandatory;
    private Configuration config;
    private String[] loadedBlockDensities;
    private String[] loadedMaterialDensities;

    public ArchimedesConfig(Configuration configuration) {
        config = configuration;
    }

    public void loadAndSave() {
        String[] defaultMaterialDensities = {"\"minecraft:air=0.0\"", "\"minecraft:wool=0.1\""};
        String[] defaultBlockDensities = {"\"ArchimedesShips:floater=0.04\"", "\"ArchimedesShips:balloon=0.02\""};
        config.load();

        shipEntitySyncRate = config.get("settings", "sync_rate", 20, "The amount of ticks between a server-client synchronization. Higher numbers reduce network traffic. Lower numbers increase multiplayer experience. 20 ticks = 1 second").getInt();
        enableAirShips = config.get("settings", "enable_air_ships", true, "Enable or disable air ships.").getBoolean(true);
        useNewAlgorithm = config.get("settings", "use_iterative_assemble_algorithm", false, "New assemble algorithm implemented in v1.6.2. Allows for larger ships but is a heavier load for CPU.").getBoolean(false);
        bankingMultiplier = (float) config.get("settings", "banking_multiplier", 3d, "A multiplier for how much ships bank while making turns. Set a positive value for passive banking or a negative value for active banking. 0 disables banking.").getDouble(3d);
        enginesMandatory = config.get("settings", "mandatory_engines", false, "Are engines required for a ship to move?").getBoolean();

        shipControlType = config.get("control", "control_type", CONTROL_TYPE_ARCHIMEDES, "Set to 0 to use vanilla boat controls, set to 1 to use the new Archimedes controls.").getInt();
        turnSpeed = (float) config.get("control", "turn_speed", 1D, "A multiplier of the ship's turn speed.").getDouble(1D);
        speedLimit = (float) config.get("control", "speed_limit", 30D, "The maximum velocity a ship can have, in blocks per second. This does not affect acceleration.").getDouble(30D);
        speedLimit /= 20F;
        disassembleOnDismount = config.get("control", "decompile_on_dismount", false).getBoolean(false);

        maxShipChunkBlocks = config.get("mobile_chunk", "max_chunk_blocks", 2048, "The maximum amount of blocks that a mobile ship chunk may contain.").getInt();
        //maxShipChunkBlocks = Math.min(maxShipChunkBlocks, 3400);
        flyBalloonRatio = (float) config.get("mobile_chunk", "airship_balloon_ratio", 0.4D, "The part of the total amount of blocks that should be balloon blocks in order to make an airship.").getDouble(0.4D);

        boolean connectDiagonalLegacy = config.get("mobile_chunk", "connect_diagonal_blocks_1", false, "Blocks connected diagonally on one axis will also be added to the ship if this value is set to 'true'.").getBoolean(false);
        connectDiagonalBlocks = config.get("mobile_chunk", "connect_diagonal_blocks", connectDiagonalLegacy, "Blocks connected diagonally on one axis will also be added to the ship if this value is set to 'true'.").getBoolean(connectDiagonalLegacy);

        loadedBlockDensities = config.get("mobile_chunk", "block_densities", defaultBlockDensities, "A list of pairs of a block with a density value. This list overrides the 'material_densities' list.").getStringList();
        loadedMaterialDensities = config.get("mobile_chunk", "material_densities", defaultMaterialDensities, "A list of pairs of a material with a density value. The first value is the name of a block. All blocks with the same material will get this density value, unless overridden.").getStringList();

        if (FMLCommonHandler.instance().getSide().isClient()) {
            loadKeybindings();
        }

        config.save();
    }

    public void postLoad() {
        Pattern splitpattern = Pattern.compile("=");
        for (int i = 0; i < loadedBlockDensities.length; i++) {
            String s = loadedBlockDensities[i];
            s = s.replace("\"", "");
            String[] pair = splitpattern.split(s);
            if (pair.length != 2) {
                ArchimedesShipMod.modLog.warn("Invalid key-value pair at block_densities[" + i + "]");
                continue;
            }
            String key = pair[0];
            float density;
            try {
                density = Float.parseFloat(pair[1]);
            } catch (NumberFormatException e) {
                ArchimedesShipMod.modLog.warn("Cannot parse value " + pair[1] + " to floating point at block_densities[" + i + "]");
                continue;
            }
            Block block = Block.getBlockFromName(key);
            if (block == null) {
                ArchimedesShipMod.modLog.warn("No block found for " + key + " at block_densities[" + i + "]");
                continue;
            }

            MaterialDensity.addDensity(block, density);
        }

        for (int i = 0; i < loadedMaterialDensities.length; i++) {
            String s = loadedMaterialDensities[i];
            s = s.replace("\"", "");
            String[] pair = splitpattern.split(s);
            if (pair.length != 2) {
                ArchimedesShipMod.modLog.warn("Invalid key-value pair at material_densities[" + i + "]");
                continue;
            }
            String key = pair[0];
            float density;
            try {
                density = Float.parseFloat(pair[1]);
            } catch (NumberFormatException e) {
                ArchimedesShipMod.modLog.warn("Cannot parse value " + pair[1] + " to floating point at material_densities[" + i + "]");
                continue;
            }
            Block block = Block.getBlockFromName(key);
            if (block == null) {
                ArchimedesShipMod.modLog.warn("No block found for " + key + " at material_densities[" + i + "]");
                continue;
            }

            MaterialDensity.addDensity(block.getMaterial(), density);
        }
    }

    @SideOnly(Side.CLIENT)
    private void loadKeybindings() {
        kbUp = new KeyBinding("key.archimedes.up", getKeyIndex(config, "key_ascent", Keyboard.KEY_X), "Archimedes");
        kbDown = new KeyBinding("key.archimedes.down", getKeyIndex(config, "key_descent", Keyboard.KEY_Z), "Archimedes");
        kbBrake = new KeyBinding("key.archimedes.brake", getKeyIndex(config, "key_brake", Keyboard.KEY_C), "Archimedes");
        kbAlign = new KeyBinding("key.archimedes.align", getKeyIndex(config, "key_align", Keyboard.KEY_EQUALS), "Archimedes");
        kbDisassemble = new KeyBinding("key.archimedes.decompile", getKeyIndex(config, "key_decompile", Keyboard.KEY_BACKSLASH), "Archimedes");
        kbShipInv = new KeyBinding("key.archimedes.shipinv", getKeyIndex(config, "key_shipinv", Keyboard.KEY_K), "Archimedes");
        Minecraft mc = Minecraft.getMinecraft();
        mc.gameSettings.keyBindings = ArrayUtils.addAll(mc.gameSettings.keyBindings, kbUp, kbDown, kbBrake, kbAlign, kbDisassemble, kbShipInv);
    }

    @SideOnly(Side.CLIENT)
    private int getKeyIndex(Configuration config, String name, int defaultkey) {
        return Keyboard.getKeyIndex(config.get("control", name, Keyboard.getKeyName(defaultkey)).getString());
    }
}
