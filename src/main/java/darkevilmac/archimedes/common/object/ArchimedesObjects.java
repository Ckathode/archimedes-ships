package darkevilmac.archimedes.common.object;

import darkevilmac.archimedes.common.object.block.*;
import darkevilmac.archimedes.common.object.item.ItemGaugeBlock;
import darkevilmac.archimedes.common.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
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

    public static Material materialFloater;
    public static HashMap<String, Block> registeredBlocks;

    public void preInit(FMLPreInitializationEvent e) {
        registeredBlocks = new HashMap<String, Block>();

        materialFloater = new Material(MapColor.clothColor);

        blockMarkShip = (BlockHelm) new BlockHelm().setCreativeTab(CreativeTabs.tabTransport);
        blockMarkShip.setStepSound(Block.soundTypeWood).setHardness(1F).setResistance(1F);
        registerBlock("marker", blockMarkShip);

        blockFloater = new BlockAS(materialFloater).setCreativeTab(CreativeTabs.tabTransport);
        blockFloater.setStepSound(Block.soundTypeWood).setHardness(1F).setResistance(1F);
        registerBlock("floater", blockFloater);

        blockBalloon = new BlockRecolourable(Material.cloth).setCreativeTab(CreativeTabs.tabTransport);
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

        blockStickyBuffer = new BlockAS(Material.cloth).setHardness(1F).setResistance(1F).setCreativeTab(CreativeTabs.tabTransport);
        blockStickyBuffer.setStepSound(Block.soundTypeMetal);
        registerBlock("stickyBuffer", blockStickyBuffer);

        blockEngine = new BlockEngine(Material.iron, 1f, 10).setHardness(2F).setResistance(3F).setCreativeTab(CreativeTabs.tabTransport);
        blockEngine.setStepSound(Block.soundTypeMetal);
        registerBlock("engine", blockEngine);

        blockCrateWood = new BlockCrate(Material.wood).setHardness(1f).setResistance(1f).setCreativeTab(CreativeTabs.tabTransport);
        blockCrateWood.setStepSound(Block.soundTypeWood);
        registerBlock("crate_wood", blockCrateWood);

        blockAnchorPoint = new BlockAnchorPoint(Material.wood).setHardness(1f).setResistance(1F).setCreativeTab(CreativeTabs.tabTransport);
        blockAnchorPoint.setStepSound(Block.soundTypePiston);
        registerBlock("anchorPoint", blockAnchorPoint);
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

        GameRegistry.addRecipe(new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('#'), Items.gold_ingot, Character.valueOf('O'), Items.redstone, Character.valueOf('V'), Blocks.glass_pane);
        GameRegistry.addRecipe(new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), Items.gold_ingot, Character.valueOf('#'), Items.iron_ingot, Character.valueOf('O'), Items.redstone, Character.valueOf('V'), Blocks.glass_pane);
        GameRegistry.addRecipe(new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('#'), Items.gold_ingot, Character.valueOf('O'), Items.redstone, Character.valueOf('V'), Blocks.glass_pane);
        GameRegistry.addRecipe(new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), Items.gold_ingot, Character.valueOf('#'), Items.iron_ingot, Character.valueOf('O'), Items.redstone, Character.valueOf('V'), Blocks.glass_pane);
        GameRegistry.registerTileEntity(TileEntityGauge.class, "archiGauge");

        GameRegistry.addRecipe(new ItemStack(blockSeat), "X ", "XX", Character.valueOf('X'), Blocks.wool);
        Blocks.fire.setFireInfo(blockSeat, 30, 30);

        GameRegistry.addShapelessRecipe(new ItemStack(blockBuffer), blockFloater, new ItemStack(Items.dye, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(blockStickyBuffer), blockBuffer, new ItemStack(Items.slime_ball, 1));

        GameRegistry.addRecipe(new ItemStack(blockCrateWood, 3), " # ", "# #", "XXX", Character.valueOf('#'), Items.leather, Character.valueOf('X'), Blocks.planks);
        GameRegistry.registerTileEntity(TileEntityCrate.class, "archiCrate");

        GameRegistry.addRecipe(new ItemStack(blockEngine, 1), "#O#", "#X#", "###", Character.valueOf('#'), Items.iron_ingot, Character.valueOf('O'), Items.water_bucket, Character.valueOf('X'), Blocks.furnace);
        GameRegistry.registerTileEntity(TileEntityEngine.class, "archiEngine");

        GameRegistry.addRecipe(new ItemStack(blockAnchorPoint, 1), "XYX", "XXX", "ZZZ", Character.valueOf('X'), Blocks.planks, Character.valueOf('Y'), Blocks.redstone_torch, Character.valueOf('Z'), Items.redstone);
        GameRegistry.registerTileEntity(TileEntityAnchorPoint.class, "archiAnchor");
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    private void registerBlock(String id, Block block) {
        registerBlock(id, block, ItemBlock.class);
    }

    private void registerBlock(String id, Block block, Class<? extends ItemBlock> itemBlockClass) {
        block.setUnlocalizedName("archimedes." + id);
        GameRegistry.registerBlock(block, itemBlockClass, id);
        ArchimedesObjects.registeredBlocks.put(id, block);
    }
}
