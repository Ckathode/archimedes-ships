package ckathode.archimedes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArchimedesConfig
{
	public static final int	CONTROL_TYPE_VANILLA	= 0, CONTROL_TYPE_ARCHIMEDES = 1;
	
	private Configuration	config;
	private String[]		loadedBlockDensities;
	private String[]		loadedMaterialDensities;
	
	//Settings
	public boolean			enableAirShips;
	public int				shipEntitySyncRate;
	
	//Mobile Chunk
	public int				maxShipChunkBlocks;
	public float			flyBalloonRatio;
	public boolean			connectDiagonalBlocks1, connectDiagonalBlocks2;
	public boolean			useWhitelist;
	public Set<String>		blockBlacklist;
	public Set<String>		blockWhitelist;
	public Set<String>		overwritableBlocks;
	public boolean			useNewAlgorithm;
	
	//Control
	public int				shipControlType;
	public float			turnSpeed;
	public float			speedLimit;
	public float			bankingMultiplier;
	
	@SideOnly(Side.CLIENT)
	public KeyBinding		kbUp, kbDown, kbBrake, kbAlign, kbDisassemble, kbShipInv;
	public boolean			disassembleOnDismount;
	
	public ArchimedesConfig(Configuration configuration)
	{
		config = configuration;
		
		blockBlacklist = new HashSet<String>();
		blockWhitelist = new HashSet<String>();
		overwritableBlocks = new HashSet<String>();
	}
	
	public void loadAndSave()
	{
		Block[] defaultBlockBlacklist = { (Blocks.dirt), (Blocks.grass), (Blocks.sand), (Blocks.gravel), (Blocks.clay), (Blocks.ice), (Blocks.water), (Blocks.flowing_water), (Blocks.flowing_lava), (Blocks.lava), (Blocks.snow), Blocks.snow_layer, (Blocks.waterlily), (Blocks.netherrack), (Blocks.soul_sand), Blocks.tallgrass };
		Block[] defaultBlocksWhitelist = { Blocks.acacia_stairs, Blocks.activator_rail, Blocks.anvil, Blocks.bed, Blocks.birch_stairs, Blocks.bookshelf, Blocks.brewing_stand, Blocks.brick_block, Blocks.brick_stairs, Blocks.cactus, Blocks.cake, Blocks.carpet, Blocks.coal_block, Blocks.cobblestone, Blocks.cobblestone_wall, Blocks.command_block, Blocks.crafting_table, Blocks.dark_oak_stairs, Blocks.detector_rail, Blocks.diamond_block, Blocks.dispenser, Blocks.dropper, Blocks.daylight_detector, Blocks.double_stone_slab, Blocks.double_wooden_slab, Blocks.emerald_block, Blocks.enchanting_table, Blocks.end_stone, Blocks.ender_chest, Blocks.fence, Blocks.fence_gate, Blocks.flower_pot, Blocks.furnace, Blocks.fire, Blocks.glass, Blocks.glass_pane, Blocks.glowstone, Blocks.gold_block, Blocks.golden_rail, Blocks.hardened_clay, Blocks.hay_block, Blocks.heavy_weighted_pressure_plate, Blocks.hopper, Blocks.iron_bars, Blocks.iron_block, Blocks.iron_door, Blocks.jukebox, Blocks.jungle_stairs, Blocks.ladder, Blocks.lapis_block, Blocks.lever, Blocks.light_weighted_pressure_plate, Blocks.lit_furnace, Blocks.lit_pumpkin, Blocks.lit_redstone_lamp, Blocks.log, Blocks.log2, Blocks.melon_block, Blocks.mob_spawner, Blocks.monster_egg, Blocks.mossy_cobblestone, Blocks.nether_brick, Blocks.nether_brick_fence, Blocks.nether_brick_stairs, Blocks.noteblock, Blocks.oak_stairs, Blocks.obsidian, Blocks.planks, Blocks.pumpkin, Blocks.piston, Blocks.piston_extension, Blocks.piston_head, Blocks.powered_comparator, Blocks.powered_repeater, Blocks.quartz_block, Blocks.quartz_stairs, Blocks.rail, Blocks.redstone_block, Blocks.redstone_torch, Blocks.redstone_wire, Blocks.sandstone, Blocks.sandstone_stairs, Blocks.skull, Blocks.sponge, Blocks.spruce_stairs, Blocks.stained_hardened_clay, Blocks.standing_sign, Blocks.stone_brick_stairs, Blocks.stone_button, Blocks.stone_pressure_plate, Blocks.stone_stairs, Blocks.stonebrick, Blocks.stained_glass, Blocks.stained_glass_pane, Blocks.sticky_piston, Blocks.stone_slab, Blocks.tnt, Blocks.torch, Blocks.trapdoor, Blocks.trapped_chest, Blocks.tripwire, Blocks.tripwire_hook, Blocks.unlit_redstone_torch, Blocks.unpowered_comparator, Blocks.unpowered_repeater, Blocks.wall_sign, Blocks.web, Blocks.wooden_button, Blocks.wooden_door, Blocks.wooden_pressure_plate, Blocks.wool, Blocks.wooden_slab };
		Block[] defaultOverwritableBlocks = { Blocks.tallgrass, Blocks.waterlily };
		String[] defaultMaterialDensities = { "\"minecraft:air=0.0\"", "\"minecraft:wool=0.1\"" };
		String[] defaultBlockDensities = { "\"ArchimedesShips:floater=0.04\"", "\"ArchimedesShips:balloon=0.02\"" };
		
		String[] blockblacklistnames = new String[defaultBlockBlacklist.length];
		for (int i = 0; i < defaultBlockBlacklist.length; i++)
		{
			blockblacklistnames[i] = Block.blockRegistry.getNameForObject(defaultBlockBlacklist[i]);
		}
		
		String[] blockwhitelistnames = new String[6 + defaultBlocksWhitelist.length];
		for (int i = 0; i < blockwhitelistnames.length - 6; i++)
		{
			blockwhitelistnames[i] = Block.blockRegistry.getNameForObject(defaultBlocksWhitelist[i]);
		}
		blockwhitelistnames[blockwhitelistnames.length - 6] = "ArchimedesShips:marker";
		blockwhitelistnames[blockwhitelistnames.length - 5] = "ArchimedesShips:floater";
		blockwhitelistnames[blockwhitelistnames.length - 4] = "ArchimedesShips:balloon";
		blockwhitelistnames[blockwhitelistnames.length - 3] = "ArchimedesShips:gauge";
		blockwhitelistnames[blockwhitelistnames.length - 2] = "ArchimedesShips:seat";
		blockwhitelistnames[blockwhitelistnames.length - 1] = "ArchimedesShips:engine";
		
		String[] overwritableblocksnames = new String[defaultOverwritableBlocks.length];
		for (int i = 0; i < defaultOverwritableBlocks.length; i++)
		{
			overwritableblocksnames[i] = Block.blockRegistry.getNameForObject(defaultOverwritableBlocks[i]);
		}
		
		config.load();
		
		shipEntitySyncRate = config.get("settings", "sync_rate", 20, "The amount of ticks between a server-client synchronization. Higher numbers reduce network traffic. Lower numbers increase multiplayer experience. 20 ticks = 1 second").getInt();
		enableAirShips = config.get("settings", "enable_air_ships", true, "Enable or disable air ships.").getBoolean(true);
		useNewAlgorithm = config.get("settings", "use_iterative_assemble_algorithm", false, "New assemble algorithm implemented in v1.6.2. Allows for larger ships but is a heavier load for CPU.").getBoolean(false);
		bankingMultiplier = (float) config.get("settings", "banking_multiplier", 3d, "A multiplier for how much ships bank while making turns. Set a positive value for passive banking or a negative value for active banking. 0 disables banking.").getDouble(3d);
		
		shipControlType = config.get("control", "control_type", CONTROL_TYPE_ARCHIMEDES, "Set to 0 to use vanilla boat controls, set to 1 to use the new Archimedes controls.").getInt();
		turnSpeed = (float) config.get("control", "turn_speed", 1D, "A multiplier of the ship's turn speed.").getDouble(1D);
		speedLimit = (float) config.get("control", "speed_limit", 30D, "The maximum velocity a ship can have, in blocks per second. This does not affect acceleration.").getDouble(30D);
		speedLimit /= 20F;
		disassembleOnDismount = config.get("control", "decompile_on_dismount", false).getBoolean(false);
		
		maxShipChunkBlocks = config.get("mobile_chunk", "max_chunk_blocks", 2048, "The maximum amount of blocks that a mobile ship chunk may contain.").getInt();
		//maxShipChunkBlocks = Math.min(maxShipChunkBlocks, 3400);
		flyBalloonRatio = (float) config.get("mobile_chunk", "airship_balloon_ratio", 0.4D, "The part of the total amount of blocks that should be balloon blocks in order to make an airship.").getDouble(0.4D);
		connectDiagonalBlocks1 = config.get("mobile_chunk", "connect_diagonal_blocks_1", false, "Blocks connected diagonally on one axis will also be added to the ship if this value is set to 'true'.").getBoolean(false);
		useWhitelist = config.get("mobile_chunk", "use_whitelist", false, "Switch this property to select the block restriction list to use. 'true' for the 'allowed_blocks' whitelist, 'false' for the 'forbidden_blocks' blacklist.").getBoolean(false);
		String[] forbiddenblocks = config.get("mobile_chunk", "forbidden_blocks", blockblacklistnames, "A list of blocks that will not be added to a ship.").getStringList();
		String[] allowedblocks = config.get("mobile_chunk", "allowed_blocks", blockwhitelistnames, "A list of blocks that are allowed on a ship.").getStringList();
		String[] overwritableblocks = config.get("mobile_chunk", "overwritable_blocks", overwritableblocksnames, "A list of blocks that may be overwritten when decompiling a ship.").getStringList();
		Collections.addAll(blockBlacklist, forbiddenblocks);
		Collections.addAll(blockWhitelist, allowedblocks);
		Collections.addAll(overwritableBlocks, overwritableblocks);
		
		loadedBlockDensities = config.get("mobile_chunk", "block_densities", defaultBlockDensities, "A list of pairs of a block with a density value. This list overrides the 'material_densities' list.").getStringList();
		loadedMaterialDensities = config.get("mobile_chunk", "material_densities", defaultMaterialDensities, "A list of pairs of a material with a density value. The first value is the name of a block. All blocks with the same material will get this density value, unless overridden.").getStringList();
		
		if (FMLCommonHandler.instance().getSide().isClient())
		{
			loadKeybindings();
		}
		
		config.save();
	}
	
	public void postLoad()
	{
		Pattern splitpattern = Pattern.compile("=");
		for (int i = 0; i < loadedBlockDensities.length; i++)
		{
			String s = loadedBlockDensities[i];
			s = s.replace("\"", "");
			String[] pair = splitpattern.split(s);
			if (pair.length != 2)
			{
				ArchimedesShipMod.modLog.warn("Invalid key-value pair at block_densities[" + i + "]");
				continue;
			}
			String key = pair[0];
			float density;
			try
			{
				density = Float.parseFloat(pair[1]);
			} catch (NumberFormatException e)
			{
				ArchimedesShipMod.modLog.warn("Cannot parse value " + pair[1] + " to floating point at block_densities[" + i + "]");
				continue;
			}
			Block block = Block.getBlockFromName(key);
			if (block == null)
			{
				ArchimedesShipMod.modLog.warn("No block found for " + key + " at block_densities[" + i + "]");
				continue;
			}
			
			MaterialDensity.addDensity(block, density);
		}
		
		for (int i = 0; i < loadedMaterialDensities.length; i++)
		{
			String s = loadedMaterialDensities[i];
			s = s.replace("\"", "");
			String[] pair = splitpattern.split(s);
			if (pair.length != 2)
			{
				ArchimedesShipMod.modLog.warn("Invalid key-value pair at material_densities[" + i + "]");
				continue;
			}
			String key = pair[0];
			float density;
			try
			{
				density = Float.parseFloat(pair[1]);
			} catch (NumberFormatException e)
			{
				ArchimedesShipMod.modLog.warn("Cannot parse value " + pair[1] + " to floating point at material_densities[" + i + "]");
				continue;
			}
			Block block = Block.getBlockFromName(key);
			if (block == null)
			{
				ArchimedesShipMod.modLog.warn("No block found for " + key + " at material_densities[" + i + "]");
				continue;
			}
			
			MaterialDensity.addDensity(block.getMaterial(), density);
		}
	}
	
	public boolean isBlockAllowed(Block block)
	{
		String id = Block.blockRegistry.getNameForObject(block);
		return useWhitelist ? blockWhitelist.contains(id) : !blockBlacklist.contains(id);
	}
	
	@SideOnly(Side.CLIENT)
	private void loadKeybindings()
	{
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
	private int getKeyIndex(Configuration config, String name, int defaultkey)
	{
		return Keyboard.getKeyIndex(config.get("control", name, Keyboard.getKeyName(defaultkey)).getString());
	}
}
