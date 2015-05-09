package darkevilmac.archimedes;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import darkevilmac.archimedes.blockitem.*;
import darkevilmac.archimedes.command.CommandASHelp;
import darkevilmac.archimedes.command.CommandDisassembleNear;
import darkevilmac.archimedes.command.CommandDisassembleShip;
import darkevilmac.archimedes.command.CommandShipInfo;
import darkevilmac.archimedes.entity.EntityParachute;
import darkevilmac.archimedes.entity.EntitySeat;
import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.archimedes.handler.ConnectionHandler;
import darkevilmac.archimedes.network.ArchimedesShipsMessageToMessageCodec;
import darkevilmac.archimedes.network.ArchimedesShipsPacketHandler;
import darkevilmac.archimedes.network.NetworkUtil;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

@Mod(modid = ArchimedesShipMod.MOD_ID, name = ArchimedesShipMod.MOD_NAME, version = ArchimedesShipMod.MOD_VERSION)
public class ArchimedesShipMod {
    public static final String MOD_ID = "ArchimedesShipsPlus";
    public static final String MOD_VERSION = "1.7.10-V1.8.0";
    public static final String MOD_NAME = "Archimedes' Ships Plus";

    @Instance(MOD_ID)
    public static ArchimedesShipMod instance;

    @SidedProxy(clientSide = "darkevilmac.archimedes.ClientProxy", serverSide = "darkevilmac.archimedes.CommonProxy")
    public static CommonProxy proxy;

    public static Logger modLog;

    public static BlockHelm blockMarkShip;
    public static Block blockFloater;
    public static Block blockBalloon;
    public static BlockGauge blockGauge;
    public static BlockSeat blockSeat;
    public static Block blockBuffer;
    public static Block blockEngine;
    public static Block blockCrateWood;
    public static Block blockAnchorPoint;
    public static Material materialFloater;

    public ArchimedesConfig modConfig;
    public NetworkUtil network;

    public ArchimedesShipMod() {
        network = new NetworkUtil();
    }

    @EventHandler
    public void preInitMod(FMLPreInitializationEvent event) {
        modLog = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);

        modConfig = new ArchimedesConfig(new Configuration(event.getSuggestedConfigurationFile()));
        modConfig.loadAndSave();

        createBlocksAndItems();

        modConfig.postLoad();
    }

    private void createBlocksAndItems() {
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

        blockAnchorPoint = new BlockAnchorPoint(Material.wood).setHardness(1f).setResistance(1F).setCreativeTab(CreativeTabs.tabTransport).setBlockTextureName("archimedes:anchorPoint");
        blockAnchorPoint.setStepSound(Block.soundTypePiston);
        registerBlock("anchorPoint", blockAnchorPoint);
    }

    private void registerBlocksAndItems() {
        GameRegistry.addRecipe(new ItemStack(blockMarkShip, 1), "X#X", "#O#", "X#X", Character.valueOf('X'), Blocks.planks, Character.valueOf('#'), Items.stick, Character.valueOf('O'), Items.iron_ingot);
        GameRegistry.registerTileEntity(TileEntityHelm.class, "archiHelm");
        Blocks.fire.setFireInfo(blockMarkShip, 5, 5);

        GameRegistry.addShapelessRecipe(new ItemStack(blockFloater, 1), Blocks.log, Blocks.wool);
        GameRegistry.addShapelessRecipe(new ItemStack(blockFloater, 1), Blocks.log2, Blocks.wool);

        //GameRegistry.addRecipe(new ItemStack(blockBalloon, 1), "X", "#", Character.valueOf('X'), Block.cloth, Character.valueOf('#'), Item.silk);
        for (int i = 0; i < ItemDye.field_150923_a.length; i++) {
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

        GameRegistry.addRecipe(new ItemStack(blockAnchorPoint, 1), "XYX", "XXX", "ZZZ", Character.valueOf('X'), Blocks.planks, Character.valueOf('Y'), Blocks.redstone_torch, Character.valueOf('Z'), Items.redstone);
        GameRegistry.registerTileEntity(TileEntityAnchorPoint.class, "archiAnchor");
    }

    @EventHandler
    public void initMod(FMLInitializationEvent event) {

        network.channels = NetworkRegistry.INSTANCE.newChannel
                (MOD_ID, new ArchimedesShipsMessageToMessageCodec(), new ArchimedesShipsPacketHandler());

        registerBlocksAndItems();

        MinecraftForge.EVENT_BUS.register(new ConnectionHandler());
        FMLCommonHandler.instance().bus().register(new ConnectionHandler());


        EntityRegistry.registerModEntity(EntityShip.class, "shipmod", 1, this, 64, modConfig.shipEntitySyncRate, true);
        EntityRegistry.registerModEntity(EntitySeat.class, "attachment.seat", 2, this, 64, 100, false);
        EntityRegistry.registerModEntity(EntityParachute.class, "parachute", 3, this, 32, modConfig.shipEntitySyncRate, true);


        proxy.registerKeyHandlers(modConfig);
        proxy.registerEventHandlers();
        proxy.registerRenderers();
    }

    @EventHandler
    public void postInitMod(FMLPostInitializationEvent event) {
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        registerASCommand(event, new CommandASHelp());
        registerASCommand(event, new CommandDisassembleShip());
        registerASCommand(event, new CommandShipInfo());
        registerASCommand(event, new CommandDisassembleNear());
        Collections.sort(CommandASHelp.asCommands);
    }

    private void registerASCommand(FMLServerStartingEvent event, CommandBase commandbase) {
        event.registerServerCommand(commandbase);
        CommandASHelp.asCommands.add(commandbase);
    }

    private void registerBlock(String id, Block block) {
        registerBlock(id, block, ItemBlock.class);
    }

    private void registerBlock(String id, Block block, Class<? extends ItemBlock> itemblockclass) {
        block.setBlockName("archimedes." + id);
        block.setBlockTextureName("archimedes:" + id);
        GameRegistry.registerBlock(block, itemblockclass, id);
    }

}
