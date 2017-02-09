package com.elytradev.davincisvessels.common.object;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.LanguageEntries;
import com.elytradev.davincisvessels.common.object.achievement.SmartAchievementPage;
import com.elytradev.davincisvessels.common.object.block.BlockAS;
import com.elytradev.davincisvessels.common.object.block.BlockAnchorPoint;
import com.elytradev.davincisvessels.common.object.block.BlockCrate;
import com.elytradev.davincisvessels.common.object.block.BlockEngine;
import com.elytradev.davincisvessels.common.object.block.BlockGauge;
import com.elytradev.davincisvessels.common.object.block.BlockHelm;
import com.elytradev.davincisvessels.common.object.block.BlockRecolourable;
import com.elytradev.davincisvessels.common.object.block.BlockSeat;
import com.elytradev.davincisvessels.common.object.block.BlockSecuredBed;
import com.elytradev.davincisvessels.common.object.item.ItemBlockAnchorPoint;
import com.elytradev.davincisvessels.common.object.item.ItemGaugeBlock;
import com.elytradev.davincisvessels.common.object.item.ItemSecuredBed;
import com.elytradev.davincisvessels.common.tileentity.TileAnchorPoint;
import com.elytradev.davincisvessels.common.tileentity.TileCrate;
import com.elytradev.davincisvessels.common.tileentity.TileEngine;
import com.elytradev.davincisvessels.common.tileentity.TileEntitySecuredBed;
import com.elytradev.davincisvessels.common.tileentity.TileGauge;
import com.elytradev.davincisvessels.common.tileentity.TileHelm;

/**
 * Block registration is here, to keep the mod class nice and small.
 */

public class DavincisVesselsObjects {

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

    public static SmartAchievementPage achievementPage;

    public static Achievement achievementAssembleFailure;
    public static Achievement achievementAssembleSuccess;
    public static Achievement achievementAssembleMount;
    public static Achievement achievementCreateHelm;
    public static Achievement achievementCreateEngine;
    public static Achievement achievementSubmerseShip;
    public static Achievement achievementFlyShip;

    public static Material materialFloater;
    public static HashMap<String, Block> registeredBlocks;
    public static HashMap<String, Item> registeredItems;

    public static String REGISTRY_PREFIX = DavincisVesselsMod.MOD_ID.toLowerCase();

    public void preInit(FMLPreInitializationEvent e) {
        registeredBlocks = new HashMap<String, Block>();
        registeredItems = new HashMap<String, Item>();

        materialFloater = new Material(MapColor.CLOTH);

        itemSecuredBed = new ItemSecuredBed().setMaxStackSize(1).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerItem("securedBed", itemSecuredBed);

        blockMarkShip = (BlockHelm) new BlockHelm().setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        blockMarkShip.setHardness(1F).setResistance(1F);
        registerBlock("marker", blockMarkShip);

        blockFloater = new BlockAS(materialFloater, SoundType.WOOD).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        blockFloater.setHardness(1F).setResistance(1F);
        registerBlock("floater", blockFloater);

        blockBalloon = new BlockRecolourable(Material.CLOTH, SoundType.CLOTH).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        blockBalloon.setHardness(0.35F).setResistance(1F);
        registerBlock("balloon", blockBalloon, ItemCloth.class);

        blockGauge = (BlockGauge) new BlockGauge().setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        blockGauge.setHardness(1F).setResistance(1F);
        registerBlock("gauge", blockGauge, ItemGaugeBlock.class);

        blockSeat = (BlockSeat) new BlockSeat().setHardness(1F).setResistance(1F).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock("seat", blockSeat);

        blockBuffer = new BlockAS(Material.CLOTH, SoundType.WOOD).setHardness(1F).setResistance(1F).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock("buffer", blockBuffer);

        blockStickyBuffer = new BlockAS(Material.CLOTH, SoundType.CLOTH).setHardness(1F).setResistance(1F).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock("stickyBuffer", blockStickyBuffer);

        blockEngine = new BlockEngine(Material.IRON, 1F, 10).setHardness(2F).setResistance(3F).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock("engine", blockEngine);

        blockCrateWood = new BlockCrate(Material.WOOD).setHardness(1f).setResistance(1f).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock("crate_wood", blockCrateWood);

        blockAnchorPoint = new BlockAnchorPoint(Material.WOOD).setHardness(1f).setResistance(1F).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock("anchorPoint", blockAnchorPoint, ItemBlockAnchorPoint.class);

        blockSecuredBed = new BlockSecuredBed().setHardness(0.2F);
        registerBlock("securedBed", blockSecuredBed, false);
    }

    public void init(FMLInitializationEvent e) {
        GameRegistry.addRecipe(new ItemStack(blockMarkShip, 1), "X#X", "#O#", "X#X", Character.valueOf('X'), Blocks.PLANKS, Character.valueOf('#'), Items.STICK, Character.valueOf('O'), Items.IRON_INGOT);
        GameRegistry.registerTileEntity(TileHelm.class, "archiHelm");
        Blocks.FIRE.setFireInfo(blockMarkShip, 5, 5);

        GameRegistry.addShapelessRecipe(new ItemStack(blockFloater, 1), Blocks.LOG, Blocks.WOOL);
        GameRegistry.addShapelessRecipe(new ItemStack(blockFloater, 1), Blocks.LOG2, Blocks.WOOL);

        //GameRegistry.addRecipe(new ItemStack(blockBalloon, 1), "X", "#", Character.valueOf('X'), Block.cloth, Character.valueOf('#'), Item.silk);
        for (int i = 0; i < ItemDye.DYE_COLORS.length; i++) {
            GameRegistry.addRecipe(new ItemStack(blockBalloon, 1, i), "X", "#", Character.valueOf('X'), new ItemStack(Blocks.WOOL, 1, i), Character.valueOf('#'), Items.STRING);
        }
        Blocks.FIRE.setFireInfo(blockBalloon, 30, 60);

        GameRegistry.addShapedRecipe(new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), Items.IRON_INGOT, Character.valueOf('#'), Items.GOLD_INGOT, Character.valueOf('O'), Items.REDSTONE, Character.valueOf('V'), Blocks.GLASS_PANE);
        GameRegistry.addShapedRecipe(new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), Items.GOLD_INGOT, Character.valueOf('#'), Items.IRON_INGOT, Character.valueOf('O'), Items.REDSTONE, Character.valueOf('V'), Blocks.GLASS_PANE);
        GameRegistry.addShapedRecipe(new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), Items.IRON_INGOT, Character.valueOf('#'), Items.GOLD_INGOT, Character.valueOf('O'), Item.getItemFromBlock(blockGauge), Character.valueOf('V'), Blocks.GLASS_PANE);
        GameRegistry.addShapedRecipe(new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), Items.GOLD_INGOT, Character.valueOf('#'), Items.IRON_INGOT, Character.valueOf('O'), Item.getItemFromBlock(blockGauge), Character.valueOf('V'), Blocks.GLASS_PANE);
        GameRegistry.registerTileEntity(TileGauge.class, "archiGauge");

        GameRegistry.addShapedRecipe(new ItemStack(blockSeat), "X ", "XX", Character.valueOf('X'), Blocks.WOOL);
        Blocks.FIRE.setFireInfo(blockSeat, 30, 30);

        GameRegistry.addShapelessRecipe(new ItemStack(blockBuffer), blockFloater, new ItemStack(Items.DYE, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(blockStickyBuffer), blockBuffer, new ItemStack(Items.SLIME_BALL, 1));

        GameRegistry.addRecipe(new ItemStack(blockCrateWood, 3), " # ", "# #", "XXX", Character.valueOf('#'), Items.LEATHER, Character.valueOf('X'), Blocks.PLANKS);
        GameRegistry.registerTileEntity(TileCrate.class, "archiCrate");

        GameRegistry.addRecipe(new ItemStack(blockEngine, 1), "#O#", "#X#", "###", Character.valueOf('#'), Items.IRON_INGOT, Character.valueOf('O'), Items.WATER_BUCKET, Character.valueOf('X'), Blocks.FURNACE);
        GameRegistry.registerTileEntity(TileEngine.class, "archiEngine");

        GameRegistry.addRecipe(new ItemStack(blockAnchorPoint, 1), " X ", "XXX", "ZYZ", Character.valueOf('X'), Items.IRON_INGOT, Character.valueOf('Y'), Blocks.IRON_BLOCK, Character.valueOf('Z'), Items.REDSTONE);
        GameRegistry.registerTileEntity(TileAnchorPoint.class, "archiAnchor");

        GameRegistry.addShapelessRecipe(new ItemStack(blockSecuredBed), Blocks.BED, Items.IRON_INGOT);
        GameRegistry.registerTileEntity(TileEntitySecuredBed.class, "archiSecuredBed");

        achievementCreateHelm = new Achievement("achievement.archimedes.create.helm",
                LanguageEntries.ACHIEVEMENT_CREATE_HELM, 0, 0, Item.getItemFromBlock(blockMarkShip), null);
        achievementCreateEngine = new Achievement("achievement.archimedes.create.engine",
                LanguageEntries.ACHIEVEMENT_CREATE_ENGINE, 0, -3, Item.getItemFromBlock(blockEngine), null);
        achievementAssembleFailure = new Achievement("achievement.archimedes.assemble.failure",
                LanguageEntries.ACHIEVEMENT_ASSEMBLE_FAILURE, 3, 2, Item.getItemFromBlock(blockMarkShip), achievementCreateHelm);
        achievementAssembleSuccess = new Achievement("achievement.archimedes.assemble.success",
                LanguageEntries.ACHIEVEMENT_ASSEMBLE_SUCCESS, 3, 0, Item.getItemFromBlock(blockMarkShip), achievementCreateHelm);
        achievementAssembleMount = new Achievement("achievement.archimedes.assemble.mount",
                LanguageEntries.ACHIEVEMENT_ASSEMBLE_MOUNT, 3, -2, Item.getItemFromBlock(blockSeat), achievementAssembleSuccess);
        achievementFlyShip = new Achievement("achievement.archimedes.fly",
                LanguageEntries.ACHIEVEMENT_FLY_SHIP, 5, -4, Item.getItemFromBlock(blockBalloon), achievementAssembleMount);
        achievementSubmerseShip = new Achievement("achievement.archimedes.submerse",
                LanguageEntries.ACHIEVEMENT_SUBMERSE_SHIP, 5, -2, Item.getItemFromBlock(blockAnchorPoint), achievementAssembleMount);

        achievementPage = new SmartAchievementPage(DavincisVesselsMod.MOD_NAME);

        achievementPage.registerAchievement(achievementCreateHelm);
        achievementPage.registerAchievement(achievementCreateEngine);
        achievementPage.registerAchievement(achievementAssembleFailure);
        achievementPage.registerAchievement(achievementAssembleSuccess);
        achievementPage.registerAchievement(achievementAssembleMount);
        achievementPage.registerAchievement(achievementFlyShip);
        achievementPage.registerAchievement(achievementSubmerseShip);

        achievementPage.finalize();
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    private void registerBlock(String id, Block block) {
        registerBlock(id, block, true);
    }

    private void registerBlock(String id, Block block, boolean withItemBlock) {
        block.setUnlocalizedName("davincis." + id);
        block.setRegistryName(REGISTRY_PREFIX, id);
        GameRegistry.register(block);
        if (withItemBlock)
            GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        DavincisVesselsObjects.registeredBlocks.put(id, block);
    }

    private void registerBlock(String id, Block block, Class<? extends ItemBlock> itemBlockClass) {
        try {
            block.setUnlocalizedName("davincis." + id);
            block.setRegistryName(REGISTRY_PREFIX, id);
            GameRegistry.register(block);

            ItemBlock itemBlock = itemBlockClass.getDeclaredConstructor(Block.class).newInstance(block);
            itemBlock.setRegistryName(REGISTRY_PREFIX, id);
            GameRegistry.register(itemBlock);
            DavincisVesselsObjects.registeredBlocks.put(id, block);
        } catch (Exception e) {
            DavincisVesselsMod.LOG.error("Caught exception while registering " + block, e);
        }
    }

    private void registerItem(String id, Item item) {
        item.setUnlocalizedName("davincis." + id);
        item.setRegistryName(REGISTRY_PREFIX, id);
        GameRegistry.register(item);
        DavincisVesselsObjects.registeredItems.put(id, item);
    }
}
