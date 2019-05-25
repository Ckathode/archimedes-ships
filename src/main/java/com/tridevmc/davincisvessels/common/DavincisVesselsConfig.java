package com.tridevmc.davincisvessels.common;

import com.google.common.collect.Lists;
import com.tridevmc.compound.config.ConfigValue;
import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.content.block.BlockBalloon;
import com.tridevmc.davincisvessels.common.content.block.BlockSeat;
import com.tridevmc.davincisvessels.common.control.EnumShipControlType;
import com.tridevmc.movingworld.MovingWorldMod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class DavincisVesselsConfig {

    //Settings
    @ConfigValue(comment = "Enable or disable airships.")
    public boolean enableAirShips = true;
    @ConfigValue(comment = "Enable or disable submersibles.")
    public boolean enableSubmersibles = true;
    @ConfigValue(comment = "The amount of ticks between a server->client sync on the ship entity. Higher numbers reduce network traffic, lower numbers reduce rubber-banding.")
    public int shipEntitySyncRate = 5;
    //Mobile Chunk
    @ConfigValue(comment = "The maximum amount of blocks that can make up a ship.")
    public int maxShipChunkBlocks = 2048;
    @ConfigValue(comment = "The percentage of blocks required for a ship to take flight. 0.4 means 40% for example.")
    public float flyBalloonRatio = 0.4F;
    @ConfigValue(comment = "The percentage of blocks that will not be filled with water required to submerse. 0.3 means 30% for example.")
    public float submersibleFillRatio = 0.25F;
    //Control
    @ConfigValue(comment = "The control type to use for ships, defaults to custom controls that fit airships better.")
    public EnumShipControlType shipControlType = EnumShipControlType.DAVINCIS;
    @ConfigValue(comment = "The turn speed when controlling a ship.")
    public float turnSpeed = 1F;
    @ConfigValue(comment = "The maximum speed for a vessel to travel, measured in blocks per second.")
    public float speedLimit = 1.5f;
    @ConfigValue(comment = "A multiplier for how much ships bank while making turns. Set a positive value for passive banking or a negative value for active banking. 0 disables banking.")
    public float bankingMultiplier = 3F;
    @ConfigValue(comment = "Determines if air ships will slowly fall down when they're not being piloted.")
    public boolean enableShipDownfall = true;
    @ConfigValue(comment = "Determines if a ship will automatically be disassembled when a ship is disassembled.")
    public boolean disassembleOnDismount = false;
    @ConfigValue(comment = "Determines if engines are required for a ship to move.")
    public boolean enginesMandatory = false;
    @ConfigValue(comment = "The amount of fuel an engine will consume each tick it is in use.")
    public int engineConsumptionRate = 10;
    @ConfigValue(comment = "The radius that an anchor can grab ships within.")
    public int anchorRadius = 12;

    @ConfigValue(comment = "A list of blocks that will function as balloons.")
    public List<Block> balloonAlternatives = DavincisVesselsMod.CONTENT.balloonBlocks;
    @ConfigValue(comment = "A list of blocks that will behave as seats.")
    public List<Block> seats = Lists.newArrayList(DavincisVesselsMod.CONTENT.blockSeat);
    @ConfigValue(comment = "Blocks that behave like a sticky buffer, they stop assembly when reached.")
    public List<Block> stickyObjects = Lists.newArrayList(DavincisVesselsMod.CONTENT.blockStickyBuffer, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.OAK_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.BIRCH_BUTTON, Blocks.ACACIA_BUTTON);

    public void addBlacklistWhitelistEntries() {
        MovingWorldMod.CONFIG.addBlacklistedBlock(DavincisVesselsMod.CONTENT.blockBuffer);

        ForgeRegistries.BLOCKS.getEntries().stream()
                .filter(e -> e.getKey().getNamespace().equals(DavincisVesselsMod.MOD_ID)
                        && !e.getKey().getPath().equals("buffer"))
                .forEach(e -> MovingWorldMod.CONFIG.addWhitelistedBlock(e.getValue()));
    }

    public boolean isBalloon(Block block) {
        return block instanceof BlockBalloon || balloonAlternatives.contains(block);
    }

    public boolean isSeat(Block block) {
        return block instanceof BlockSeat || seats.contains(block);
    }

    public boolean isSticky(Block block) {
        return block == DavincisVesselsMod.CONTENT.blockStickyBuffer || stickyObjects.contains(block);
    }


}
