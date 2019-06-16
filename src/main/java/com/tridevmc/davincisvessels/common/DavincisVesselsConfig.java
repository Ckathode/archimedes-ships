package com.tridevmc.davincisvessels.common;

import com.tridevmc.compound.config.ConfigType;
import com.tridevmc.compound.config.ConfigValue;
import com.tridevmc.davincisvessels.common.control.EnumVesselControlType;
import net.minecraftforge.fml.config.ModConfig;

@ConfigType(ModConfig.Type.SERVER)
public class DavincisVesselsConfig {

    //Settings
    @ConfigValue(comment = "Enable or disable airvessels.")
    public boolean enableAirVessels = true;
    @ConfigValue(comment = "Enable or disable submersibles.")
    public boolean enableSubmersibles = true;
    @ConfigValue(comment = "The amount of ticks between a server->client sync on the vessel entity. Higher numbers reduce network traffic, lower numbers reduce rubber-banding.")
    public int vesselEntitySyncRate = 5;
    //Mobile Chunk
    @ConfigValue(comment = "The maximum amount of blocks that can make up a vessel.")
    public int maxVesselChunkBlocks = 2048;
    @ConfigValue(comment = "The percentage of blocks required for a vessel to take flight. 0.4 means 40% for example.")
    public double flyBalloonRatio = 0.4F;
    @ConfigValue(comment = "The percentage of blocks that will not be filled with water required to submerse. 0.3 means 30% for example.")
    public double submersibleFillRatio = 0.25F;
    //Control
    @ConfigValue(comment = "The control type to use for vessels, defaults to custom controls that fit airvessels better.")
    public EnumVesselControlType vesselControlType = EnumVesselControlType.DAVINCIS;
    @ConfigValue(comment = "The turn speed when controlling a vessel.")
    public double turnSpeed = 1F;
    @ConfigValue(comment = "The maximum speed for a vessel to travel, measured in blocks per second.")
    public double speedLimit = 1.5f;
    @ConfigValue(comment = "A multiplier for how much vessels bank while making turns. Set a positive value for passive banking or a negative value for active banking. 0 disables banking.")
    public double bankingMultiplier = 3F;
    @ConfigValue(comment = "Determines if air vessels will slowly fall down when they're not being piloted.")
    public boolean enableVesselDownfall = true;
    @ConfigValue(comment = "Determines if a vessel will automatically be disassembled when a vessel is disassembled.")
    public boolean disassembleOnDismount = false;
    @ConfigValue(comment = "Determines if engines are required for a vessel to move.")
    public boolean enginesMandatory = false;
    @ConfigValue(comment = "The amount of fuel an engine will consume each tick it is in use.")
    public int engineConsumptionRate = 10;
    @ConfigValue(comment = "The radius that an anchor can grab vessels within.")
    public int anchorRadius = 12;

}
