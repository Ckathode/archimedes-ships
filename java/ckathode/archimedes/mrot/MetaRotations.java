package ckathode.archimedes.mrot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import ckathode.archimedes.ArchimedesShipMod;

public class MetaRotations
{
	private File							metaRotationsDirectory;
	public Map<Integer, BlockMetaRotation>	metaRotationMap;
	
	public MetaRotations()
	{
		metaRotationMap = new HashMap<Integer, BlockMetaRotation>();
	}
	
	public boolean hasBlock(Block block)
	{
		return metaRotationMap.containsKey(Block.getIdFromBlock(block));
	}
	
	public int getRotatedMeta(Block block, int meta, int rotate)
	{
		if (rotate == 0) return meta;
		BlockMetaRotation rotation = metaRotationMap.get(Block.getIdFromBlock(block));
		if (rotation == null) return meta;
		return rotation.getRotatedMeta(meta, rotate);
	}
	
	public void addMetaRotation(Block block, int bitmask, int... metarotation)
	{
		if (block == null)
		{
			ArchimedesShipMod.modLog.error("Adding null block meta rotations");
			return;
		}
		ArchimedesShipMod.modLog.trace("Adding meta rotations (block=" + Block.blockRegistry.getNameForObject(block) + ", id=" + Block.getIdFromBlock(block) + ", mask=" + bitmask + ", rot=" + Arrays.toString(metarotation) + ")");
		
		metaRotationMap.put(Block.getIdFromBlock(block), new BlockMetaRotation(block, metarotation, bitmask));
	}
	
	public void setConfigDirectory(File configdirectory)
	{
		metaRotationsDirectory = new File(configdirectory, "ArchimedesShips");
		if (!metaRotationsDirectory.isDirectory())
		{
			metaRotationsDirectory.mkdirs();
		}
	}
	
	public boolean parseMetaRotations(BufferedReader reader) throws IOException, OutdatedMrotException
	{
		boolean hasversionno = false;
		int lineno = 0;
		String line;
		String[] as;
		while ((line = reader.readLine()) != null)
		{
			lineno++;
			if (line.startsWith("#") || line.length() == 0)
			{
				continue;
			} else if (line.startsWith("version="))
			{
				hasversionno = true;
				as = line.split("=");
				if (as.length != 2)
				{
					mrotError("Version number is invalid", lineno);
					throw new OutdatedMrotException("?");
				}
				String modversion = ArchimedesShipMod.MOD_VERSION;
				String version = as[1].trim();
				if (!version.equals(modversion))
				{
					throw new OutdatedMrotException(version);
				}
				continue;
			}
			
			Block[] blocks;
			int mask = 0xFFFFFFFF;
			int[] rot = new int[4];
			
			as = line.split(";");
			if (as.length < 3)
			{
				mrotError("Not enough parameters", lineno);
				continue;
			}
			
			String[] blocksstr = as[0].split(",");
			blocks = new Block[blocksstr.length];
			for (int i = 0; i < blocksstr.length; i++)
			{
				String name = blocksstr[i].trim();
				blocks[i] = Block.getBlockFromName(name);
				if (blocks[i] == null)
				{
					mrotError("No block exists for " + name, lineno);
				}
			}
			
			try
			{
				mask = Integer.decode(as[1].trim()).intValue();
				String[] srot = as[2].split(",");
				for (int i = 0; i < rot.length; i++)
				{
					rot[i] = Integer.parseInt(srot[i].trim());
				}
			} catch (NumberFormatException e)
			{
				mrotError(e.getLocalizedMessage(), lineno);
			}
			
			for (Block b : blocks)
			{
				addMetaRotation(b, mask, rot);
			}
		}
		return hasversionno;
	}
	
	public void mrotError(String msg, int lineno)
	{
		ArchimedesShipMod.modLog.warn("Error in metarotation file at line " + lineno + " (" + msg + ")");
	}
	
	public void readMetaRotationFiles()
	{
		if (metaRotationsDirectory == null) throw new NullPointerException("Config folder has not been initialized");
		metaRotationMap.clear();
		
		try
		{
			try
			{
				readMetaRotationFile(new File(metaRotationsDirectory, "default.mrot"));
			} catch (OutdatedMrotException ome)
			{
				ArchimedesShipMod.modLog.info("Outdated default.mrot detected: " + ome.getLocalizedMessage());
				createDefaultMrot();
				readMetaRotationFile(new File(metaRotationsDirectory, "default.mrot"));
			} catch (FileNotFoundException fnfe)
			{
				ArchimedesShipMod.modLog.info("default.mrot file not found: " + fnfe.getLocalizedMessage());
				createDefaultMrot();
				readMetaRotationFile(new File(metaRotationsDirectory, "default.mrot"));
			} catch (Exception e0)
			{
				throw e0;
			}
		} catch (Exception e1)
		{
			ArchimedesShipMod.modLog.error("Could not load default meta rotations", e1);
		}
		
		File[] files = metaRotationsDirectory.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File f, String name)
			{
				return !name.equals("default.mrot") && name.endsWith(".mrot");
			}
		});
		
		for (File f : files)
		{
			try
			{
				readMetaRotationFile(f);
			} catch (OutdatedMrotException ome)
			{
				ome.printStackTrace();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void readMetaRotationFile(File file) throws IOException, OutdatedMrotException
	{
		ArchimedesShipMod.modLog.info("Reading metarotation file: " + file.getName());
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		boolean flag = parseMetaRotations(reader);
		if (!flag && file.getName().equals("default.mrot"))
		{
			throw new OutdatedMrotException("pre-1.4.4");
		}
		reader.close();
	}
	
	public void createDefaultMrot()
	{
		ArchimedesShipMod.modLog.info("Creating default.mrot");
		File defaultfile = new File(metaRotationsDirectory, "default.mrot");
		BufferedWriter writer = null;
		try
		{
			defaultfile.createNewFile();
			writer = new BufferedWriter(new FileWriter(defaultfile));
			writer.write("version=");
			writer.write(ArchimedesShipMod.MOD_VERSION);
			writer.write("\n");
			writer.write("#----------------#\n");
			writer.write("# VANILLA BLOCKS #\n");
			writer.write("#----------------#\n");
			writer.write("# Default vanilla block meta rotations\n");
			writer.write("# This file will be overwritten every start, changes will not be implemented!\n");
			writer.write("# blocknames/blockIDs; bitmask; 4 metadata values in the clockwise rotation order\n");
			writer.write("\n");
			writer.write("# Pumpkin & Lantern\n");
			writer.write("pumpkin, lit_pumpkin; 0x3; 0, 1, 2, 3;\n");
			writer.write("\n");
			writer.write("# Stairs\n");
			writer.write("oak_stairs, stone_stairs, brick_stairs, stone_brick_stairs, nether_brick_stairs, sandstone_stairs, spruce_stairs, birch_stairs, jungle_stairs, quartz_stairs, acacia_stairs, dark_oak_stairs; 0x3; 2, 1, 3, 0;\n");
			writer.write("\n");
			writer.write("# Torches, levers and buttons\n");
			writer.write("torch, unlit_redstone_torch, redstone_torch, stone_button, wooden_button, lever; 0x7; 4, 1, 3, 2;\n");
			writer.write("\n");
			writer.write("# Sign\n");
			writer.write("wall_sign; 0x7; 3, 4, 2, 5;\n");
			writer.write("\n");
			writer.write("# Log\n");
			writer.write("log, log2; 0xC; 4, 8, 4, 8;\n");
			writer.write("\n");
			writer.write("# Quarts pillar\n");
			writer.write("quartz_block; 0x7; 3, 4, 3, 4;\n");
			writer.write("\n");
			writer.write("# Ladder\n");
			writer.write("ladder; 0x7; 3, 4, 2, 5;\n");
			writer.write("\n# Fence gate\n");
			writer.write("fence_gate; 0x3; 0, 1, 2, 3;\n");
			writer.write("\n# Furnace, dispenser, chest, pistons\n");
			writer.write("furnace, lit_furnace, dispenser, chest, ender_chest, trapped_chest, sticky_piston, piston; 0x7; 2, 5, 3, 4;\n");
			writer.write("\n# Redstone repeater, comparator\n");
			writer.write("unpowered_repeater, powered_repeater, unpowered_comparator, powered_comparator; 0x3; 0, 1, 2, 3;\n");
			writer.write("\n# Doors\n");
			writer.write("wooden_door, iron_door; 0x3; 0, 1, 2, 3;\n");
			writer.write("\n# Trapdoor\n");
			writer.write("trapdoor; 0x3; 3, 1, 2, 0;\n");
			writer.write("\n# Bed\n");
			writer.write("bed, tripwire_hook; 0x3; 0, 1, 2, 3;\n");
			writer.write("# Archimedes' Ships block meta rotations\n");
			writer.write("ArchimedesShips:marker; 0x3; 0, 1, 2, 3;\n");
			writer.write("ArchimedesShips:gauge; 0x3; 0, 1, 2, 3;\n");
			writer.write("ArchimedesShips:seat; 0x3; 0, 1, 2, 3;\n");
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
