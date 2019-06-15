package com.tridevmc.davincisvessels.common;

import com.tridevmc.compound.config.ConfigType;
import com.tridevmc.compound.config.ConfigValue;
import com.tridevmc.davincisvessels.common.control.EnumShipControlType;
import net.minecraftforge.fml.config.ModConfig;

@ConfigType(ModConfig.Type.SERVER)
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
    public double flyBalloonRatio = 0.4F;
    @ConfigValue(comment = "The percentage of blocks that will not be filled with water required to submerse. 0.3 means 30% for example.")
    public double submersibleFillRatio = 0.25F;
    //Control
    @ConfigValue(comment = "The control type to use for ships, defaults to custom controls that fit airships better.")
    public EnumShipControlType shipControlType = EnumShipControlType.DAVINCIS;
    @ConfigValue(comment = "The turn speed when controlling a ship.")
    public double turnSpeed = 1F;
    @ConfigValue(comment = "The maximum speed for a vessel to travel, measured in blocks per second.")
    public double speedLimit = 1.5f;
    @ConfigValue(comment = "A multiplier for how much ships bank while making turns. Set a positive value for passive banking or a negative value for active banking. 0 disables banking.")
    public double bankingMultiplier = 3F;
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

}
