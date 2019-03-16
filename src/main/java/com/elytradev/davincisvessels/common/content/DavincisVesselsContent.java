package com.elytradev.davincisvessels.common.content;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.content.block.*;
import com.elytradev.davincisvessels.common.content.item.ItemBlockAnchorPoint;
import com.elytradev.davincisvessels.common.content.item.ItemGaugeBlock;
import com.elytradev.davincisvessels.common.content.item.ItemSecuredBed;
import com.elytradev.davincisvessels.common.tileentity.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.List;

import static com.elytradev.davincisvessels.DavincisVesselsMod.MOD_ID;

/**
 * Block registration is here, to keep the mod class nice and small.
 */

public class DavincisVesselsContent {

    public BlockHelm blockMarkShip;
    public Block blockFloater;
    public Block blockBalloon;
    public BlockGauge blockGauge;
    public BlockSeat blockSeat;
    public Block blockStickyBuffer;
    public Block blockBuffer;
    public Block blockEngine;
    public Block blockCrateWood;
    public Block blockAnchorPoint;
    public Block blockSecuredBed;

    public static Item itemSecuredBed;

    public static Material materialFloater;
    public static HashMap<String, Block> registeredBlocks;
    public static HashMap<String, Item> registeredItems;
    public static List<String> skipMesh = Lists.newArrayList();
    public static String REGISTRY_PREFIX = DavincisVesselsMod.MOD_ID.toLowerCase();
    private static List<Item> itemBlocksToRegister;
    private int recipeID = 0;

    public void init(FMLInitializationEvent e) {
        this.setFireInfo(blockMarkShip, 5, 5);
        this.setFireInfo(blockBalloon, 30, 60);
        this.setFireInfo(blockSeat, 30, 30);

        GameRegistry.registerTileEntity(TileHelm.class, "archiHelm");
        GameRegistry.registerTileEntity(TileGauge.class, "archiGauge");
        GameRegistry.registerTileEntity(TileCrate.class, "archiCrate");
        GameRegistry.registerTileEntity(TileEngine.class, "archiEngine");
        GameRegistry.registerTileEntity(TileAnchorPoint.class, "archiAnchor");
        GameRegistry.registerTileEntity(TileEntitySecuredBed.class, "archiSecuredBed");
    }

    private void setFireInfo(Block block, int encouragement, int flammability) {
        BlockFire fire = (BlockFire) Blocks.FIRE;
        fire.setFireInfo(block, encouragement, flammability);
    }

    @SubscribeEvent
    public void onItemRegister(RegistryEvent.Register<Item> blockRegister) {
        IForgeRegistry<Item> registry = blockRegister.getRegistry();
        registeredItems = new HashMap<String, Item>();

        itemSecuredBed = new ItemSecuredBed().setMaxStackSize(1).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerItem(registry, "securedBed", itemSecuredBed);

        for (Item item : itemBlocksToRegister) {
            registry.register(item);
        }
    }

    @SubscribeEvent
    public void onBlockRegister(RegistryEvent.Register<Block> blockRegister) {
        IForgeRegistry<Block> registry = blockRegister.getRegistry();
        registeredBlocks = Maps.newHashMap();
        itemBlocksToRegister = Lists.newArrayList();
        materialFloater = new Material(MapColor.CLOTH);

        blockMarkShip = (BlockHelm) new BlockHelm().setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        blockMarkShip.setHardness(1F).setResistance(1F);
        registerBlock(registry, "marker", blockMarkShip);

        blockFloater = new BlockAS(materialFloater, SoundType.WOOD).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        blockFloater.setHardness(1F).setResistance(1F);
        registerBlock(registry, "floater", blockFloater);

        blockBalloon = new BlockRecolourable(Material.CLOTH, SoundType.CLOTH).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        blockBalloon.setHardness(0.35F).setResistance(1F);
        registerBlock(registry, "balloon", blockBalloon, ItemCloth.class);
        skipMesh.add("balloon");

        blockGauge = (BlockGauge) new BlockGauge().setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        blockGauge.setHardness(1F).setResistance(1F);
        registerBlock(registry, "gauge", blockGauge, ItemGaugeBlock.class);
        skipMesh.add("gauge");

        blockSeat = (BlockSeat) new BlockSeat().setHardness(1F).setResistance(1F).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock(registry, "seat", blockSeat);

        blockBuffer = new BlockAS(Material.CLOTH, SoundType.WOOD).setHardness(1F).setResistance(1F).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock(registry, "buffer", blockBuffer);

        blockStickyBuffer = new BlockAS(Material.CLOTH, SoundType.WOOD).setHardness(1F).setResistance(1F).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock(registry, "stickyBuffer", blockStickyBuffer);

        blockEngine = new BlockEngine(Material.IRON, 1F, DavincisVesselsMod.INSTANCE.getNetworkConfig().getShared().engineConsumptionRate).setHardness(2F).setResistance(3F).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock(registry, "engine", blockEngine);

        blockCrateWood = new BlockCrate(Material.WOOD).setHardness(1f).setResistance(1f).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock(registry, "crate_wood", blockCrateWood);

        blockAnchorPoint = new BlockAnchorPoint(Material.WOOD).setHardness(1f).setResistance(1F).setCreativeTab(DavincisVesselsMod.CREATIVE_TAB);
        registerBlock(registry, "anchorPoint", blockAnchorPoint, ItemBlockAnchorPoint.class);

        blockSecuredBed = new BlockSecuredBed().setHardness(0.2F);
        registerBlock(registry, "securedBed", blockSecuredBed, false);
    }

    @SubscribeEvent
    public void onRecipeRegister(RegistryEvent.Register<IRecipe> recipeRegister) {
        DavincisVesselsMod.LOG.info("Registering recipes for Davincis Vessels...");
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockMarkShip, 1), "X#X", "#O#", "X#X", Character.valueOf('X'), "plankWood", Character.valueOf('#'), "stickWood", Character.valueOf('O'), "ingotIron");
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), "ingotIron", Character.valueOf('#'), "ingotGold", Character.valueOf('O'), "dustRedstone", Character.valueOf('V'), Blocks.GLASS_PANE);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockGauge, 1, 0), "VXV", "XO#", " # ", Character.valueOf('X'), "ingotGold", Character.valueOf('#'), "ingotIron", Character.valueOf('O'), "dustRedstone", Character.valueOf('V'), Blocks.GLASS_PANE);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), "ingotIron", Character.valueOf('#'), "ingotGold", Character.valueOf('O'), Item.getItemFromBlock(blockGauge), Character.valueOf('V'), Blocks.GLASS_PANE);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockGauge, 1, 1), "VXV", "XO#", "V#V", Character.valueOf('X'), "ingotGold", Character.valueOf('#'), "ingotIron", Character.valueOf('O'), Item.getItemFromBlock(blockGauge), Character.valueOf('V'), Blocks.GLASS_PANE);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockSeat), "X ", "XX", Character.valueOf('X'), Blocks.WOOL);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockCrateWood, 3), " # ", "# #", "XXX", Character.valueOf('#'), "leather", Character.valueOf('X'), "plankWood");
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockEngine, 1), "#O#", "#X#", "###", Character.valueOf('#'), "ingotIron", Character.valueOf('O'), Items.WATER_BUCKET, Character.valueOf('X'), Blocks.FURNACE);
        registerShapedRecipe(recipeRegister.getRegistry(), new ItemStack(blockAnchorPoint, 1), " X ", "XXX", "ZYZ", Character.valueOf('X'), "ingotIron", Character.valueOf('Y'), "blockIron", Character.valueOf('Z'), "dustRedstone");

        registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(itemSecuredBed), Items.BED, "ingotIron");
        registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(blockBuffer), blockFloater, "dye");
        registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(blockStickyBuffer), blockBuffer, "slimeball");
        registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(blockFloater, 1), "logWood", Blocks.WOOL);

        for (int i = 0; i < ItemDye.DYE_COLORS.length; i++) {
            EnumDyeColor dyeColor = EnumDyeColor.byMetadata(i);
            registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(blockBalloon, 1, dyeColor.getMetadata()), new ItemStack(Blocks.WOOL, 1, dyeColor.getMetadata()), "string");
            registerShapelessRecipe(recipeRegister.getRegistry(), new ItemStack(blockBalloon, 1, dyeColor.getMetadata()),
                    "dye" + dyeColor.getUnlocalizedName().substring(0, 1).toUpperCase() + dyeColor.getUnlocalizedName().substring(1),
                    blockBalloon);
        }
        DavincisVesselsMod.LOG.info("Registration complete!");
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    private void registerShapedRecipe(IForgeRegistry<IRecipe> registry, ItemStack out, Object... input) {
        ResourceLocation resourceLocation = new ResourceLocation(MOD_ID, out.getUnlocalizedName() + recipeID++);
        registry.register(new ShapedOreRecipe(resourceLocation, out, input).setRegistryName(resourceLocation));
    }

    private void registerShapelessRecipe(IForgeRegistry<IRecipe> registry, ItemStack out, Object... input) {
        ResourceLocation resourceLocation = new ResourceLocation(MOD_ID, out.getUnlocalizedName() + recipeID++);
        registry.register(new ShapelessOreRecipe(resourceLocation, out, input).setRegistryName(resourceLocation));
    }

    private void registerBlock(IForgeRegistry<Block> registry, String id, Block block) {
        registerBlock(registry, id, block, true);
    }

    private void registerBlock(IForgeRegistry<Block> registry, String id, Block block, boolean withItemBlock) {
        block.setUnlocalizedName("davincis." + id);
        block.setRegistryName(REGISTRY_PREFIX, id);
        registry.register(block);
        if (withItemBlock)
            itemBlocksToRegister.add(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        DavincisVesselsContent.registeredBlocks.put(id, block);
    }

    private void registerBlock(IForgeRegistry<Block> registry, String id, Block block, Class<? extends ItemBlock> itemBlockClass) {
        try {
            block.setUnlocalizedName("davincis." + id);
            block.setRegistryName(REGISTRY_PREFIX, id);
            registry.register(block);

            ItemBlock itemBlock = itemBlockClass.getDeclaredConstructor(Block.class).newInstance(block);
            itemBlock.setRegistryName(REGISTRY_PREFIX, id);
            itemBlocksToRegister.add(itemBlock);
            DavincisVesselsContent.registeredBlocks.put(id, block);
        } catch (Exception e) {
            DavincisVesselsMod.LOG.error("Caught exception while registering " + block, e);
        }
    }

    private void registerItem(IForgeRegistry<Item> registry, String id, Item item) {
        item.setUnlocalizedName("davincis." + id);
        item.setRegistryName(REGISTRY_PREFIX, id);
        registry.register(item);
        DavincisVesselsContent.registeredItems.put(id, item);
    }
}
