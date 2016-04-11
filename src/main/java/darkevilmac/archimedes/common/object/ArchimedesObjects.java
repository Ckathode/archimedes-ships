package darkevilmac.archimedes.common.object;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.common.object.block.*;
import darkevilmac.archimedes.common.object.item.ItemGaugeBlock;
import darkevilmac.archimedes.common.object.item.ItemSecuredBed;
import darkevilmac.archimedes.common.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;

/**
 * Block registration is here, to keep the mod class nice and small.
 */

public class ArchimedesObjects {

    public static BlockHelm blockMarkShip;
    public static Block blockFloater;
    public static Block blockBalloon;
    public static BlockGauge blockGauge;
    public static BlockSeat blockSeat;
    public static Block blockStickyBuffer;
    public static Block blockBuffer;
    public static Block blockEngine;
    public static Block blockCrateWood;
    public static Block blockAnchorPoint;
    public static Block blockSecuredBed;

    public static Item itemSecuredBed;

    public static Material materialFloater;
    public static HashMap<String, Block> registeredBlocks;
    public static HashMap<String, Item> registeredItems;

    public void preInit(FMLPreInitializationEvent e) {
        registeredBlocks = new HashMap<String, Block>();
        registeredItems = new HashMap<String, Item>();

        materialFloater = new Material(MapColor.clothColor);

        itemSecuredBed = new ItemSecuredBed().setMaxStackSize(1).setCreativeTab(ArchimedesShipMod.creativeTab);
        registerItem("securedBed", itemSecuredBed);

        blockMarkShip = (BlockHelm) new BlockHelm().setCreativeTab(ArchimedesShipMod.creativeTab);
        blockMarkShip.setHardness(1F).setResistance(1F);
        registerBlock("marker", blockMarkShip);

        blockFloater = new BlockAS(materialFloater, SoundType.WOOD).setCreativeTab(ArchimedesShipMod.creativeTab);
        blockFloater.setHardness(1F).setResistance(1F);
        registerBlock("floater", blockFloater);

        blockBalloon = new BlockRecolourable(Material.cloth, SoundType.CLOTH).setCreativeTab(ArchimedesShipMod.creativeTab);
        blockBalloon.setHardness(0.35F).setResistance(1F);
        registerBlock("balloon", blockBalloon, ItemCloth.class);

        blockGauge = (BlockGauge) new BlockGauge().setCreativeTab(ArchimedesShipMod.creativeTab);
        blockGauge.setHardness(1F).setResistance(1F);
        registerBlock("gauge", blockGauge, ItemGaugeBlock.class);

        blockSeat = (BlockSeat) new BlockSeat().setHardness(1F).setResistance(1F).setCreativeTab(ArchimedesShipMod.creativeTab);
        registerBlock("seat", blockSeat);

        blockBuffer = new BlockAS(Material.cloth, SoundType.WOOD).setHardness(1F).setResistance(1F).setCreativeTab(ArchimedesShipMod.creativeTab);
        registerBlock("buffer", blockBuffer);

        blockStickyBuffer = new BlockAS(Material.cloth, SoundType.CLOTH).setHardness(1F).setResistance(1F).setCreativeTab(ArchimedesShipMod.creativeTab);
        registerBlock("stickyBuffer", blockStickyBuffer);

        blockEngine = new BlockEngine(Material.iron, 1F, 10).setHardness(2F).setResistance(3F).setCreativeTab(ArchimedesShipMod.creativeTab);
        registerBlock("engine", blockEngine);

        blockCrateWood = new BlockCrate(Material.wood).setHardness(1f).setResistance(1f).setCreativeTab(ArchimedesShipMod.creativeTab);
        registerBlock("crate_wood", blockCrateWood);

        blockAnchorPoint = new BlockAnchorPoint(Material.wood).setHardness(1f).setResistance(1F).setCreativeTab(ArchimedesShipMod.creativeTab);
        registerBlock("anchorPoint", blockAnchorPoint);

        blockSecuredBed = new BlockSecuredBed().setHardness(0.2F);
        registerBlockNoItemBlock("securedBed", blockSecuredBed);
    }

    public void init(FMLInitializationEvent e) {
        GameRegistry.addRecipe(new ItemStack(blockMarkShip, 1), "X#X", "#O#", "X#X", Character.valueOf('X'), Blocks.planks, Character.valueOf('#'), Items.stick, Character.valueOf('O'), Items.iron_ingot);
        GameRegistry.registerTileEntity(TileEntityHelm.class, "archiHelm");
        Blocks.fire.setFireInfo(blockMarkShip, 5, 5);

        GameRegistry.addShapelessRecipe(new ItemStack(blockFloater, 1), Blocks.log, Blocks.wool);
        GameRegistry.addShapelessRecipe(new ItemStack(blockFloater, 1), Blocks.log2, Blocks.wool);

        //GameRegistry.addRecipe(new ItemStack(blockBalloon, 1), "X", "#", Character.valueOf('X'), Block.cloth, Character.valueOf('#'), Item.silk);
        for (int i = 0; i < ItemDye.dyeColors.length; i++) {
            GameRegistry.addRecipe(new ItemStack(blockBalloon, 1, i), "X", "#", Character.valueOf('X'), new ItemStack(Blocks.wool, 1, i), Character.valueOf('#'), Items.string);
        }
        Blocks.fire.setFireInfo(blockBalloon, 30, 60);

        GameRegistry.addShapedRecipe(new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('#'), Items.gold_ingot, Character.valueOf('O'), Items.redstone, Character.valueOf('V'), Blocks.glass_pane);
        GameRegistry.addShapedRecipe(new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), Items.gold_ingot, Character.valueOf('#'), Items.iron_ingot, Character.valueOf('O'), Items.redstone, Character.valueOf('V'), Blocks.glass_pane);
        GameRegistry.addShapedRecipe(new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('#'), Items.gold_ingot, Character.valueOf('O'), Item.getItemFromBlock(blockGauge), Character.valueOf('V'), Blocks.glass_pane);
        GameRegistry.addShapedRecipe(new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), Items.gold_ingot, Character.valueOf('#'), Items.iron_ingot, Character.valueOf('O'), Item.getItemFromBlock(blockGauge), Character.valueOf('V'), Blocks.glass_pane);
        GameRegistry.registerTileEntity(TileEntityGauge.class, "archiGauge");

        GameRegistry.addShapedRecipe(new ItemStack(blockSeat), "X ", "XX", Character.valueOf('X'), Blocks.wool);
        Blocks.fire.setFireInfo(blockSeat, 30, 30);

        GameRegistry.addShapelessRecipe(new ItemStack(blockBuffer), blockFloater, new ItemStack(Items.dye, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(blockStickyBuffer), blockBuffer, new ItemStack(Items.slime_ball, 1));

        GameRegistry.addRecipe(new ItemStack(blockCrateWood, 3), " # ", "# #", "XXX", Character.valueOf('#'), Items.leather, Character.valueOf('X'), Blocks.planks);
        GameRegistry.registerTileEntity(TileEntityCrate.class, "archiCrate");

        GameRegistry.addRecipe(new ItemStack(blockEngine, 1), "#O#", "#X#", "###", Character.valueOf('#'), Items.iron_ingot, Character.valueOf('O'), Items.water_bucket, Character.valueOf('X'), Blocks.furnace);
        GameRegistry.registerTileEntity(TileEntityEngine.class, "archiEngine");

        GameRegistry.addRecipe(new ItemStack(blockAnchorPoint, 1), " X ", "XXX", "ZYZ", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('Y'), Blocks.iron_block, Character.valueOf('Z'), Items.redstone);
        GameRegistry.registerTileEntity(TileEntityAnchorPoint.class, "archiAnchor");

        GameRegistry.addShapelessRecipe(new ItemStack(blockSecuredBed), Blocks.bed, Items.iron_ingot);
        GameRegistry.registerTileEntity(TileEntitySecuredBed.class, "archiSecuredBed");
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    private void registerBlockNoItemBlock(String id, Block block) {
        block.setUnlocalizedName("archimedes." + id);
        GameRegistry.registerBlock(block, null, id);
        ArchimedesObjects.registeredBlocks.put(id, block);
    }

    private void registerBlock(String id, Block block) {
        block.setUnlocalizedName("archimedes." + id);
        GameRegistry.registerBlock(block, id);
        ArchimedesObjects.registeredBlocks.put(id, block);
    }

    private void registerBlock(String id, Block block, Class<? extends ItemBlock> itemBlockClass) {
        block.setUnlocalizedName("archimedes." + id);
        GameRegistry.registerBlock(block, itemBlockClass, id);
        ArchimedesObjects.registeredBlocks.put(id, block);
    }

    private void registerItem(String id, Item item) {
        item.setUnlocalizedName("archimedes." + id);
        GameRegistry.registerItem(item, id);
        ArchimedesObjects.registeredItems.put(id, item);
    }
}
