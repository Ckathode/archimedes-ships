package com.tridevmc.davincisvessels.common.content;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.content.block.*;
import com.tridevmc.davincisvessels.common.content.item.ItemBlockAnchorPoint;
import com.tridevmc.davincisvessels.common.content.item.ItemSecuredBed;
import com.tridevmc.davincisvessels.common.entity.EntityParachute;
import com.tridevmc.davincisvessels.common.entity.EntitySeat;
import com.tridevmc.davincisvessels.common.entity.EntityShip;
import com.tridevmc.davincisvessels.common.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DavincisVesselsContent {

    public BlockHelm blockHelm;
    public Block blockFloater;
    public BlockGauge blockGauge;
    public BlockGauge blockGaugeExtended;
    public BlockSeat blockSeat;
    public Block blockStickyBuffer;
    public Block blockBuffer;
    public Block blockEngine;
    public Block blockCrate;
    public Block blockAnchorPoint;
    public Block blockSecuredBed;
    public List<Block> balloonBlocks;

    public Item itemSecuredBed;

    public ItemGroup itemGroup = new ItemGroup("davincisTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(blockHelm);
        }
    };

    public Map<Class<? extends TileEntity>, TileEntityType> tileTypes = Maps.newHashMap();
    public Map<Class<? extends Entity>, EntityType<?>> entityTypes = Maps.newHashMap();

    public Material materialFloater;
    public HashMap<String, Block> registeredBlocks;
    public HashMap<String, Item> registeredItems;
    private String REGISTRY_PREFIX = DavincisVesselsMod.MOD_ID.toLowerCase();
    private List<Item> itemBlocksToRegister;

    private void setFireInfo(Block block, int encouragement, int flammability) {
        BlockFire fire = (BlockFire) Blocks.FIRE;
        fire.setFireInfo(block, encouragement, flammability);
    }

    @SubscribeEvent
    public void onTileRegister(final RegistryEvent.Register<TileEntityType<?>> e) {
        IForgeRegistry<TileEntityType<?>> registry = e.getRegistry();
        registerTileEntity(registry, new ResourceLocation(REGISTRY_PREFIX, "helm"), TileHelm::new);
        registerTileEntity(registry, new ResourceLocation(REGISTRY_PREFIX, "gauge"), TileGauge::new);
        registerTileEntity(registry, new ResourceLocation(REGISTRY_PREFIX, "crate"), TileCrate::new);
        registerTileEntity(registry, new ResourceLocation(REGISTRY_PREFIX, "engine"), TileEngine::new);
        registerTileEntity(registry, new ResourceLocation(REGISTRY_PREFIX, "anchor_point"), TileAnchorPoint::new);
        registerTileEntity(registry, new ResourceLocation(REGISTRY_PREFIX, "secured_bed"), TileEntitySecuredBed::new);
    }

    @SubscribeEvent
    public void onEntityRegister(final RegistryEvent.Register<EntityType<?>> e) {
        IForgeRegistry<EntityType<?>> registry = e.getRegistry();
        registerEntity(registry, new ResourceLocation(REGISTRY_PREFIX, "shipmod"), 64, DavincisVesselsMod.CONFIG.shipEntitySyncRate, true, EntityShip.class, EntityShip::new);
        registerEntity(registry, new ResourceLocation(REGISTRY_PREFIX, "attachment_seat"), 64, 20, false, EntitySeat.class, EntitySeat::new);
        registerEntity(registry, new ResourceLocation(REGISTRY_PREFIX, "parachute"), 32, DavincisVesselsMod.CONFIG.shipEntitySyncRate, true, EntityParachute.class, EntityParachute::new);
    }

    @SubscribeEvent
    public void onItemRegister(final RegistryEvent.Register<Item> e) {
        IForgeRegistry<Item> registry = e.getRegistry();
        registeredItems = new HashMap<>();

        itemSecuredBed = new ItemSecuredBed();
        registerItem(registry, "secured_bed", itemSecuredBed);

        for (Item item : itemBlocksToRegister) {
            registry.register(item);
        }
    }

    @SubscribeEvent
    public void onBlockRegister(final RegistryEvent.Register<Block> e) {
        IForgeRegistry<Block> registry = e.getRegistry();
        registeredBlocks = Maps.newHashMap();
        itemBlocksToRegister = Lists.newArrayList();
        materialFloater = new Material(MaterialColor.WOOL, false, true, true, true, true, true, false, EnumPushReaction.NORMAL);

        this.balloonBlocks = new ArrayList<>();
        for (EnumDyeColor colour : EnumDyeColor.values()) {
            BlockBalloon balloon = new BlockBalloon(colour);
            registerBlock(registry, colour.getTranslationKey() + "_balloon", balloon);
            balloonBlocks.add(balloon);
            this.setFireInfo(balloon, 30, 60);
        }

        blockHelm = new BlockHelm(Block.Properties.create(Material.WOOD).hardnessAndResistance(1F));
        registerBlock(registry, "helm", blockHelm);

        blockFloater = new BlockAS(materialFloater, SoundType.WOOD);
        registerBlock(registry, "floater", blockFloater);

        blockGauge = new BlockGauge();
        registerBlock(registry, "gauge", blockGauge);

        blockGaugeExtended = new BlockGauge();
        registerBlock(registry, "gauge_ext", blockGaugeExtended);

        blockSeat = new BlockSeat();
        registerBlock(registry, "seat", blockSeat);

        blockBuffer = new BlockAS(Material.CLOTH, SoundType.WOOD);
        registerBlock(registry, "buffer", blockBuffer);

        blockStickyBuffer = new BlockAS(Material.CLOTH, SoundType.WOOD);
        registerBlock(registry, "sticky_buffer", blockStickyBuffer);

        blockEngine = new BlockEngine(1F, DavincisVesselsMod.CONFIG.engineConsumptionRate);
        registerBlock(registry, "engine", blockEngine);

        blockCrate = new BlockCrate();
        registerBlock(registry, "crate_wood", blockCrate);

        blockAnchorPoint = new BlockAnchorPoint();
        registerBlock(registry, "anchor_point", blockAnchorPoint, ItemBlockAnchorPoint.class);

        blockSecuredBed = new BlockSecuredBed();
        registerBlock(registry, "secured_bed", blockSecuredBed, false);

        this.setFireInfo(blockHelm, 5, 5);
        this.setFireInfo(blockSeat, 30, 30);
    }

    private void registerBlock(IForgeRegistry<Block> registry, String id, Block block) {
        registerBlock(registry, id, block, true);
    }

    private void registerBlock(IForgeRegistry<Block> registry, String id, Block block, boolean withItemBlock) {
        block.setRegistryName(REGISTRY_PREFIX, id);
        registry.register(block);
        if (withItemBlock)
            itemBlocksToRegister.add(new ItemBlock(block, new Item.Properties().group(itemGroup)).setRegistryName(block.getRegistryName()));
        registeredBlocks.put(id, block);
    }

    private void registerBlock(IForgeRegistry<Block> registry, String id, Block block, Class<? extends ItemBlock> itemBlockClass) {
        try {
            block.setRegistryName(REGISTRY_PREFIX, id);
            registry.register(block);

            ItemBlock itemBlock = itemBlockClass.getDeclaredConstructor(Block.class).newInstance(block);
            itemBlock.setRegistryName(REGISTRY_PREFIX, id);
            itemBlocksToRegister.add(itemBlock);
            registeredBlocks.put(id, block);
        } catch (Exception e) {
            DavincisVesselsMod.LOG.error("Caught exception while registering " + block, e);
        }
    }

    private void registerItem(IForgeRegistry<Item> registry, String id, Item item) {
        item.setRegistryName(REGISTRY_PREFIX, id);
        registry.register(item);
        registeredItems.put(id, item);
    }

    private void registerTileEntity(IForgeRegistry<TileEntityType<?>> registry, ResourceLocation id, Supplier<TileEntity> tileSupplier) {
        Type<?> dataFixer = null;

        try {
            dataFixer = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(1519)).getChoiceType(TypeReferences.BLOCK_ENTITY, id.toString());
        } catch (IllegalArgumentException e) {
            if (SharedConstants.developmentMode) {
                throw e;
            }
        }

        TileEntityType<TileEntity> tileType = TileEntityType.Builder.create(tileSupplier).build(dataFixer);
        tileType.setRegistryName(id);
        this.tileTypes.put(tileSupplier.get().getClass(), tileType);
        registry.register(tileType);
    }

    private void registerEntity(IForgeRegistry<EntityType<?>> registry, ResourceLocation id,
                                int range, int updateFrequency, boolean sendVelocityUpdates,
                                Class<? extends Entity> clazz, Function<? super World, ? extends Entity> entityCreator) {
        EntityType<Entity> entityType = EntityType.Builder.create(clazz, entityCreator)
                .tracker(range, updateFrequency, sendVelocityUpdates)
                .disableSummoning()
                .build(id.toString());
        entityType.setRegistryName(id);
        registry.register(entityType);
        this.entityTypes.put(clazz, entityType);
    }
}
