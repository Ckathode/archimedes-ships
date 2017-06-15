package com.elytradev.davincisvessels.common.object;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.object.block.*;
import com.elytradev.davincisvessels.common.object.item.ItemBlockAnchorPoint;
import com.elytradev.davincisvessels.common.object.item.ItemGaugeBlock;
import com.elytradev.davincisvessels.common.object.item.ItemSecuredBed;
import com.elytradev.davincisvessels.common.tileentity.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.HashMap;

import static com.elytradev.davincisvessels.DavincisVesselsMod.RESOURCE_DOMAIN;

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

    // TODO: Achievements are gone.
    //public static SmartAchievementPage achievementPage;
    public static Advancement achievementAssembleFailure;
    public static Advancement achievementAssembleSuccess;
    public static Advancement achievementAssembleMount;
    public static Advancement achievementCreateHelm;
    public static Advancement achievementCreateEngine;
    public static Advancement achievementSubmerseShip;
    public static Advancement achievementFlyShip;

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
        Blocks.FIRE.setFireInfo(blockMarkShip, 5, 5);
        Blocks.FIRE.setFireInfo(blockBalloon, 30, 60);
        Blocks.FIRE.setFireInfo(blockSeat, 30, 30);

        GameRegistry.registerTileEntity(TileHelm.class, "archiHelm");
        GameRegistry.registerTileEntity(TileGauge.class, "archiGauge");
        GameRegistry.registerTileEntity(TileCrate.class, "archiCrate");
        GameRegistry.registerTileEntity(TileEngine.class, "archiEngine");
        GameRegistry.registerTileEntity(TileAnchorPoint.class, "archiAnchor");
        GameRegistry.registerTileEntity(TileEntitySecuredBed.class, "archiSecuredBed");
    }

    @SubscribeEvent
    public void onRecipeRegisterEvent(RegistryEvent.Register<IRecipe> recipeRegister) {
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockMarkShip, 1), "X#X", "#O#", "X#X", Character.valueOf('X'), "plankWood", Character.valueOf('#'), "stickWood", Character.valueOf('O'), "ingotIron");
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), "ingotIron", Character.valueOf('#'), "ingotGold", Character.valueOf('O'), "dustRedstone", Character.valueOf('V'), Blocks.GLASS_PANE);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), "ingotGold", Character.valueOf('#'), "ingotIron", Character.valueOf('O'), "dustRedstone", Character.valueOf('V'), Blocks.GLASS_PANE);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), "ingotIron", Character.valueOf('#'), "ingotGold", Character.valueOf('O'), Item.getItemFromBlock(blockGauge), Character.valueOf('V'), Blocks.GLASS_PANE);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), "ingotGold", Character.valueOf('#'), "ingotIron", Character.valueOf('O'), Item.getItemFromBlock(blockGauge), Character.valueOf('V'), Blocks.GLASS_PANE);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockSeat), "X ", "XX", Character.valueOf('X'), Blocks.WOOL);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockCrateWood, 3), " # ", "# #", "XXX", Character.valueOf('#'),"leather", Character.valueOf('X'), "plankWood");
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockEngine, 1), "#O#", "#X#", "###", Character.valueOf('#'), "ingotIron", Character.valueOf('O'), Items.WATER_BUCKET, Character.valueOf('X'), Blocks.FURNACE);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockAnchorPoint, 1), " X ", "XXX", "ZYZ", Character.valueOf('X'), "ingotIron", Character.valueOf('Y'), "blockIron", Character.valueOf('Z'), "dustRedstone");
        
        registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(blockSecuredBed), Blocks.BED, "ironIngot");
        registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(blockBuffer), blockFloater, "dye");
        registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(blockStickyBuffer), blockBuffer, "slimeball");
        registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(blockFloater, 1), "logWood", Blocks.WOOL);
        for (int i = 0; i < ItemDye.DYE_COLORS.length; i++) {
            registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(blockBalloon, 1, i), "X", "#", Character.valueOf('X'), new ItemStack(Blocks.WOOL, 1, i), Character.valueOf('#'), "string");
        }
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    private int recipeID = 0;

    private void registerShapedRecipe(IForgeRegistry<IRecipe> registry, ItemStack out, Object... input) {
        ShapedOreRecipe recipe = new ShapedOreRecipe(new ResourceLocation(RESOURCE_DOMAIN, out.getUnlocalizedName() + recipeID++), out, input);
        registry.register(recipe);
    }

    private void registerShapelessRecipe(IForgeRegistry<IRecipe> registry, ItemStack out, Object... input) {
        ShapelessOreRecipe recipe = new ShapelessOreRecipe(new ResourceLocation(RESOURCE_DOMAIN, out.getUnlocalizedName() + recipeID++), out, input);
        registry.register(recipe);
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
