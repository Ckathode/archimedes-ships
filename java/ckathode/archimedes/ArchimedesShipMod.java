package ckathode.archimedes;

import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Logger;

import ckathode.archimedes.blockitem.BlockAS;
import ckathode.archimedes.blockitem.BlockCrate;
import ckathode.archimedes.blockitem.BlockEngine;
import ckathode.archimedes.blockitem.BlockGauge;
import ckathode.archimedes.blockitem.BlockHelm;
import ckathode.archimedes.blockitem.BlockSeat;
import ckathode.archimedes.blockitem.ItemGaugeBlock;
import ckathode.archimedes.blockitem.TileEntityCrate;
import ckathode.archimedes.blockitem.TileEntityEngine;
import ckathode.archimedes.blockitem.TileEntityGauge;
import ckathode.archimedes.blockitem.TileEntityHelm;
import ckathode.archimedes.command.CommandASHelp;
import ckathode.archimedes.command.CommandDisassembleNear;
import ckathode.archimedes.command.CommandDisassembleShip;
import ckathode.archimedes.command.CommandShipInfo;
import ckathode.archimedes.entity.EntityEntityAttachment;
import ckathode.archimedes.entity.EntityParachute;
import ckathode.archimedes.entity.EntitySeat;
import ckathode.archimedes.entity.EntityShip;
import ckathode.archimedes.mrot.MetaRotations;
import ckathode.archimedes.network.ASMessagePipeline;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ArchimedesShipMod.MOD_ID, name = ArchimedesShipMod.MOD_NAME, version = ArchimedesShipMod.MOD_VERSION)
public class ArchimedesShipMod
{
	public static final String		MOD_ID		= "ArchimedesShips";
	public static final String		MOD_VERSION	= "1.7.10 v1.7.1";
	public static final String		MOD_NAME	= "Archimedes' Ships";
	
	@Instance(MOD_ID)
	public static ArchimedesShipMod	instance;
	
	@SidedProxy(clientSide = "ckathode.archimedes.ClientProxy", serverSide = "ckathode.archimedes.CommonProxy")
	public static CommonProxy		proxy;
	
	public static Logger			modLog;
	
	public static BlockHelm			blockMarkShip;
	public static Block				blockFloater;
	public static Block				blockBalloon;
	public static BlockGauge		blockGauge;
	public static BlockSeat			blockSeat;
	public static Block				blockBuffer;
	public static Block				blockEngine;
	public static Block				blockCrateWood;
	public static Material			materialFloater;
	
	public ArchimedesConfig			modConfig;
	public ASMessagePipeline		pipeline;
	public MetaRotations			metaRotations;
	
	public ArchimedesShipMod()
	{
		pipeline = new ASMessagePipeline();
		metaRotations = new MetaRotations();
	}
	
	@EventHandler
	public void preInitMod(FMLPreInitializationEvent event)
	{
		modLog = event.getModLog();
		
		/*MaterialMap.registerMaterial("air", Material.air);
		MaterialMap.registerMaterial("anvil", Material.anvil);
		MaterialMap.registerMaterial("cactus", Material.cactus);
		MaterialMap.registerMaterial("cake", Material.cake);
		MaterialMap.registerMaterial("carpet", Material.carpet);
		MaterialMap.registerMaterial("circuits", Material.circuits);
		MaterialMap.registerMaterial("clay", Material.clay);
		MaterialMap.registerMaterial("cloth", Material.cloth);
		MaterialMap.registerMaterial("coral", Material.coral);
		MaterialMap.registerMaterial("dragon_egg", Material.dragonEgg);
		MaterialMap.registerMaterial("fire", Material.fire);
		MaterialMap.registerMaterial("glass", Material.glass);
		MaterialMap.registerMaterial("gourd", Material.gourd);
		MaterialMap.registerMaterial("grass", Material.grass);
		MaterialMap.registerMaterial("ground", Material.ground);
		MaterialMap.registerMaterial("ice", Material.ice);
		MaterialMap.registerMaterial("ice_packed", Material.packedIce);
		MaterialMap.registerMaterial("iron", Material.iron);
		MaterialMap.registerMaterial("lava", Material.lava);
		MaterialMap.registerMaterial("leaves", Material.leaves);
		MaterialMap.registerMaterial("piston", Material.piston);
		MaterialMap.registerMaterial("plants", Material.plants);
		MaterialMap.registerMaterial("portal", Material.portal);
		MaterialMap.registerMaterial("redstone_light", Material.redstoneLight);
		MaterialMap.registerMaterial("rock", Material.rock);
		MaterialMap.registerMaterial("sand", Material.sand);
		MaterialMap.registerMaterial("snow", Material.snow);
		MaterialMap.registerMaterial("snow_crafted", Material.craftedSnow);
		MaterialMap.registerMaterial("sponge", Material.sponge);
		MaterialMap.registerMaterial("tnt", Material.tnt);
		MaterialMap.registerMaterial("vine", Material.vine);
		MaterialMap.registerMaterial("water", Material.water);
		MaterialMap.registerMaterial("web", Material.web);
		MaterialMap.registerMaterial("wood", Material.wood);*/
		
		modConfig = new ArchimedesConfig(new Configuration(event.getSuggestedConfigurationFile()));
		modConfig.loadAndSave();
		
		metaRotations.setConfigDirectory(event.getModConfigurationDirectory());
		
		pipeline.initalize();
		
		createBlocksAndItems();
		
		modConfig.postLoad();
	}
	
	private void createBlocksAndItems()
	{
		materialFloater = new Material(MapColor.clothColor);
		
		blockMarkShip = (BlockHelm) new BlockHelm().setCreativeTab(CreativeTabs.tabTransport);
		blockMarkShip.setStepSound(Block.soundTypeWood).setHardness(1F).setResistance(1F);
		registerBlock("marker", blockMarkShip);
		
		blockFloater = new BlockAS(materialFloater).setCreativeTab(CreativeTabs.tabTransport);
		blockFloater.setStepSound(Block.soundTypeWood).setHardness(1F).setResistance(1F);
		registerBlock("floater", blockFloater);
		
		blockBalloon = new BlockColored(Material.cloth).setCreativeTab(CreativeTabs.tabTransport);
		blockBalloon.setStepSound(Block.soundTypeCloth).setHardness(0.35F).setResistance(1F);
		registerBlock("balloon", blockBalloon, ItemCloth.class);
		
		blockGauge = (BlockGauge) new BlockGauge().setCreativeTab(CreativeTabs.tabTransport);
		blockGauge.setStepSound(Block.soundTypeMetal).setHardness(1F).setResistance(1F);
		registerBlock("gauge", blockGauge, ItemGaugeBlock.class);
		
		blockSeat = (BlockSeat) new BlockSeat().setHardness(1F).setResistance(1F).setCreativeTab(CreativeTabs.tabTransport);
		blockSeat.setStepSound(Block.soundTypeCloth);
		registerBlock("seat", blockSeat);
		
		blockBuffer = new BlockAS(Material.cloth).setHardness(1F).setResistance(1F).setCreativeTab(CreativeTabs.tabTransport);
		blockBuffer.setStepSound(Block.soundTypeWood);
		registerBlock("buffer", blockBuffer);
		
		blockEngine = new BlockEngine(Material.iron, 1f, 10).setHardness(2F).setResistance(3F).setCreativeTab(CreativeTabs.tabTransport);
		blockEngine.setStepSound(Block.soundTypeMetal);
		registerBlock("engine", blockEngine);
		
		blockCrateWood = new BlockCrate(Material.wood).setHardness(1f).setResistance(1f).setCreativeTab(CreativeTabs.tabTransport);
		blockCrateWood.setStepSound(Block.soundTypeWood);
		registerBlock("crate_wood", blockCrateWood);
	}
	
	private void registerBlocksAndItems()
	{
		GameRegistry.addRecipe(new ItemStack(blockMarkShip, 1), "X#X", "#O#", "X#X", Character.valueOf('X'), Blocks.planks, Character.valueOf('#'), Items.stick, Character.valueOf('O'), Items.iron_ingot);
		GameRegistry.registerTileEntity(TileEntityHelm.class, "archiHelm");
		Blocks.fire.setFireInfo(blockMarkShip, 5, 5);
		
		GameRegistry.addShapelessRecipe(new ItemStack(blockFloater, 1), Blocks.log, Blocks.wool);
		GameRegistry.addShapelessRecipe(new ItemStack(blockFloater, 1), Blocks.log2, Blocks.wool);
		
		//GameRegistry.addRecipe(new ItemStack(blockBalloon, 1), "X", "#", Character.valueOf('X'), Block.cloth, Character.valueOf('#'), Item.silk);
		for (int i = 0; i < ItemDye.field_150923_a.length; i++)
		{
			GameRegistry.addRecipe(new ItemStack(blockBalloon, 1, i), "X", "#", Character.valueOf('X'), new ItemStack(Blocks.wool, 1, i), Character.valueOf('#'), Items.string);
		}
		Blocks.fire.setFireInfo(blockBalloon, 30, 60);
		
		GameRegistry.addRecipe(new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('#'), Items.gold_ingot, Character.valueOf('O'), Items.redstone, Character.valueOf('V'), Blocks.glass_pane);
		GameRegistry.addRecipe(new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), Items.gold_ingot, Character.valueOf('#'), Items.iron_ingot, Character.valueOf('O'), Items.redstone, Character.valueOf('V'), Blocks.glass_pane);
		GameRegistry.addRecipe(new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('#'), Items.gold_ingot, Character.valueOf('O'), Items.redstone, Character.valueOf('V'), Blocks.glass_pane);
		GameRegistry.addRecipe(new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), Items.gold_ingot, Character.valueOf('#'), Items.iron_ingot, Character.valueOf('O'), Items.redstone, Character.valueOf('V'), Blocks.glass_pane);
		GameRegistry.registerTileEntity(TileEntityGauge.class, "archiGauge");
		
		GameRegistry.addRecipe(new ItemStack(blockSeat), "X ", "XX", Character.valueOf('X'), Blocks.wool);
		Blocks.fire.setFireInfo(blockSeat, 30, 30);
		
		GameRegistry.addShapelessRecipe(new ItemStack(blockBuffer), blockFloater, new ItemStack(Items.dye, 1, 0));
		
		GameRegistry.addRecipe(new ItemStack(blockCrateWood, 3), " # ", "# #", "XXX", Character.valueOf('#'), Items.leather, Character.valueOf('X'), Blocks.planks);
		GameRegistry.registerTileEntity(TileEntityCrate.class, "archiCrate");
		
		GameRegistry.addRecipe(new ItemStack(blockEngine, 1), "#O#", "#X#", "###", Character.valueOf('#'), Items.iron_ingot, Character.valueOf('O'), Items.water_bucket, Character.valueOf('X'), Blocks.furnace);
		GameRegistry.registerTileEntity(TileEntityEngine.class, "archiEngine");
	}
	
	@EventHandler
	public void initMod(FMLInitializationEvent event)
	{
		registerBlocksAndItems();
		
		EntityRegistry.registerModEntity(EntityShip.class, "shipmod", 1, this, 64, modConfig.shipEntitySyncRate, true);
		EntityRegistry.registerModEntity(EntityEntityAttachment.class, "attachment", 2, this, 64, 100, false);
		EntityRegistry.registerModEntity(EntitySeat.class, "attachment.seat", 3, this, 64, 100, false);
		EntityRegistry.registerModEntity(EntityParachute.class, "parachute", 4, this, 32, modConfig.shipEntitySyncRate, true);
		
		//In g/cm^3
		/*MaterialDensity.addDensity(Material.air, 0F);
		//MaterialDensity.addDensity(Material.wood, 0.700F);
		MaterialDensity.addDensity(Material.wood, 0.500F);
		MaterialDensity.addDensity(Material.rock, 2.500F);
		MaterialDensity.addDensity(Material.water, 1.000F);
		MaterialDensity.addDensity(Material.lava, 2.500F);
		MaterialDensity.addDensity(Material.ice, 0.916F);
		//MaterialDensity.addDensity(Material.iron, 7.874F);
		MaterialDensity.addDensity(Material.iron, 5.000F);
		MaterialDensity.addDensity(Material.anvil, 5.000F);
		//MaterialDensity.addDensity(Material.glass, 2.600F);
		MaterialDensity.addDensity(Material.glass, 0.400F);
		MaterialDensity.addDensity(Material.leaves, 0.200F);
		MaterialDensity.addDensity(Material.plants, 0.200F);
		//MaterialDensity.addDensity(Material.cloth, 1.314F);
		MaterialDensity.addDensity(Material.cloth, 0.700F);
		MaterialDensity.addDensity(Material.sand, 1.600F);
		MaterialDensity.addDensity(Material.ground, 2.000F);
		MaterialDensity.addDensity(Material.grass, 2.000F);
		MaterialDensity.addDensity(Material.clay, 2.000F);
		MaterialDensity.addDensity(Material.gourd, 0.900F);
		MaterialDensity.addDensity(Material.sponge, 0.400F);
		MaterialDensity.addDensity(Material.craftedSnow, 0.800F);
		MaterialDensity.addDensity(Material.tnt, 1.200F);
		MaterialDensity.addDensity(Material.piston, 1.000F);
		MaterialDensity.addDensity(Material.cloth, 0.100F);
		MaterialDensity.addDensity(materialFloater, 0.04F);
		MaterialDensity.addDensity(blockBalloon, 0.02F);*/
		
		proxy.registerKeyHandlers(modConfig);
		proxy.registerEventHandlers();
		proxy.registerRenderers();
		proxy.registerPackets(pipeline);
	}
	
	@EventHandler
	public void postInitMod(FMLPostInitializationEvent event)
	{
		metaRotations.readMetaRotationFiles();
		pipeline.postInitialize();
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		registerASCommand(event, new CommandASHelp());
		registerASCommand(event, new CommandDisassembleShip());
		registerASCommand(event, new CommandShipInfo());
		registerASCommand(event, new CommandDisassembleNear());
		Collections.sort(CommandASHelp.asCommands);
	}
	
	private void registerASCommand(FMLServerStartingEvent event, CommandBase commandbase)
	{
		event.registerServerCommand(commandbase);
		CommandASHelp.asCommands.add(commandbase);
	}
	
	private void registerBlock(String id, Block block)
	{
		registerBlock(id, block, ItemBlock.class);
	}
	
	private void registerBlock(String id, Block block, Class<? extends ItemBlock> itemblockclass)
	{
		block.setBlockName("archimedes." + id);
		block.setBlockTextureName("archimedes:" + id);
		GameRegistry.registerBlock(block, itemblockclass, id);
	}
}
