[center][img]http://i.imgur.com/QO3n22h.png[/img][/center]

[center]Craft and create your own ship... And sail it across the seven seas![/center]

[size=medium][b]Index[/b][/size]
[list]
[*][goto=download]Download[/goto]
[*][goto=source]Source[/goto]
[*][goto=install]Installation[/goto]
[*][goto=read]Read before posting[/goto]
[*]Features
[list]
[*][goto=blocks]Blocks and items[/goto]
[*][goto=ship]Ships[/goto]
[/list]
[*][goto=videos]Videos[/goto]
[*][goto=banners]Banners[/goto]
[*][goto=modpacks]Modpacks[/goto]
[*][goto=faq]FAQ[/goto]
[*][goto=changelog]Changelog[/goto]
[*][goto=todo]Bugs & To-do[/goto]
[*][goto=oldversions]Old versions[/goto]
[/list]
You can follow me on Twitter.
[twitterbtn]Ckathode[/twitterbtn]

[anchor=download][size=medium][b]Download[/b][/size][/anchor]
[size=large][b][url=http://www.curse.com/mc-mods/minecraft/archimedes-ships]Curse.com Mod Page[/url][/b][/size]
[size=large][b][url=http://goo.gl/neH8T3] Archimedes' Ships v1.7.1[/url][/b] for Minecraft 1.7.10[/size]
[size=large][b][url=http://goo.gl/kSOVhx]Archimedes' Ships v1.7.0[/url][/b] for Minecraft 1.7.2[/size]
[size=large][b][url=http://goo.gl/qXhnrg]Archimedes' Ships v1.4.5[/url][/b] for Minecraft 1.6[/size]

[anchor=source][size=medium][b]Source[/b][/size][/anchor]
You can find the full source of the mod code at:
[url=https://github.com/Ckathode/archimedes-ships]GitHub/Ckathode/archimedes-ships[/url]

[anchor=install][size=medium][b]Installation[/b][/size][/anchor][numlist]
[*]Requires Minecraft Forge, so install that first.
[*]Move the downloaded ArchimedesShips-*.jar to the [b]%appdata%/.minecraft/mods/[/b] folder.
[*](Optional) Edit the configuration file for various settings. ([b].minecraft/config/ArchimedesShips.cfg[/b])
[*]Enjoy!
[/numlist][anchor=read][size=medium][b]Read this before you post[/b][/size][/anchor]
Before you post, make sure you read the whole topic carefully, especially the [goto=faq]FAQ[/goto] section.
If you get an error screen, please post the appeared error. Bugs and crashes are not excluded.
Ideas, videos and other support are always welcome.

[size=medium][b]Features[/b][/size]
[anchor=blocks][size=medium][i]Blocks and items[/i][/size][/anchor]
[u]Ship's Helm (Ship marker)[/u]
The ship's helm, previously called the ship marker, is the main block to create ships.
Right click the block to create a ship out of connected blocks and mount it. See [goto=ship][i]Features:Ship[/i][/goto] for information about the ship blocks.
Crafting recipe:
[img]http://i.imgur.com/4kPfQ3p.png[/img]

[u]Measurement Gauges[/u]
The basic gauge block consist of two functional indicators.
One gauge acts like a compass and will always point to the north, the other indicates the speed of the ship it is attached to. A whole circle equals 80 kilometres per hour.
It will look like this:
[img]http://i.imgur.com/o2pQ7Knh.png[/img]

Crafting recipe:
[img]http://i.imgur.com/YjHrjqM.png[/img]

The extended gauge block is specialized for air ships and contains two additional gauges, once indicating the current vertical velocity, the other indicating the height by 

using two pointers. One pointer that indicates 10 blocks per whole circle, a second pointer indicating 100 blocks per whole circle.
It will look like this:
[img]http://i.imgur.com/zWrn3V7.jpg[/img]

Crafting recipe:
[img]http://i.imgur.com/dl7QYPy.png[/img]

[u]Floater[/u]
[b]Please note[/b]: The floater is not an essential part of the ship!
It is simply a very lightweight block, that gives ships the ability to float heigher and not sinking too deep. The more floaters you include in your ship, the higher it will 

be on the water.
Crafting recipe:
[img]http://i.imgur.com/ojYoDJH.png[/img]

[u]Air Balloon[/u]
The balloon is the essential block to build air ships. 40% of the blocks on a ship should be a balloon in order to give the ship the ability to fly. This percentage is 

adjustable in the config file.
Balloons will get the same colour as the wool it is crafted with.
Crafting recipe:
[img]http://i.imgur.com/fwu2e2D.png[/img]

[u]Passenger Seat[/u]
This block allows other players to join you on your ship.
If a ship contains a seat, players can interact with the ship by right clicking it and will automatically be assigned to a seat.
Crafting recipe:
[img]http://i.imgur.com/wB0Wftb.png[/img]

[u]Crate[/u]
The crate block grabs mobs that walk over it and a few other entity types and attaches them to the block.
This allows you to transport them on your ship.
Crafting recipe:
[img]http://i.imgur.com/yHW8pJW.png[/img]

[u]Steam Engine[/u]
The steam engine allows your ship to accelerate, rotate and lift faster. It requires burnable fuel, like coal or wood, to provide power.
Right click the block to enter its inventory.
Crafting recipe:
[img]http://i.imgur.com/kWFrIjy.png[/img]

[u]Shore Buffer[/u]
A simple block that will never attach to ships.
Crafting recipe:
[img]http://i.imgur.com/MaFItEL.png[/img]

[anchor=ship][size=medium][i]Ship[/i][/size][/anchor]
[u]Assemble a ship[/u]
To create a ship, simply build it as you build everything in Minecraft.
Then it needs a ship helm, which is the main block of the ship and the pilot seat at the same time.
Right clicking the helm opens up a GUI:
[img]http://i.imgur.com/3ctSpEK.png[/img]
[list]
[*]Rename: Allows you to rename the ship. Press Enter or the same button again to confirm.
[*]Assemble: Finds all connecting blocks and shows the latest results on the screen. The assembled ship will be stored in the Helm block.
[*]Undo: Reverts to the previous assembly, in case the current assembly failed. For now, this can only be done once.
[*]Mount: Creates an entity out of the assembled blocks and lets you mount the ship.
[/list]

When assembling, none of the following blocks will be considered part of the ship by default:
[spoiler]
[list]
[*]Dirt
[*]Grass
[*]Sand
[*]Gravel
[*]Clay
[*]Ice
[*]Water
[*]Lava
[*]Snow
[*]Water lily
[*]Tall grass
[*]Netherrack
[*]Soul sand
[*]Tall grass
[/list]
[/spoiler]
You can add or remove blocks to this list in the config file.

Disassembling a ship will overwrite the following blocks by default:
[spoiler]
[list]
[*]Tall grass
[/list]
[/spoiler]
You can add or remove blocks to this list in the config file.

[u]Sail a ship[/u]
The ship can be steered with the movement keys, where left and right change the direction of the ship and forward and backward change the forward velocity of the ship, no matter where you're looking at.
Through the configuration file you can enable the old vanilla control, by setting the control_type property to 0.
[list]
[*]Ascend - X : Makes the ship ascend, if it is an airship.
[*]Descend - Z : Makes the ship descend, if it is an airship.
[*]Brake - C : The ship will quickly come to a full stop.
[*]Align - = (equals): The ship will align and rotate to the world grid without rejoining with the world blocks.
[*]Disassemble - \ (backslash): The ship will align and the blocks will rejoin with the world. This enables you to edit the ship.
[*]Open GUI - K : Opens a GUI screen, it does not contain many functions yet.
[/list]

[u]Command a ship[/u]
[list]
[list]
[*]/ashelp OR /as?
Lists all Archimedes' Ships mod commands.
[/list]
[/list]

[list]
[list]
[*]/asinfo
Shows information about the ship you're currently sailing.
[/list]
[/list]

[list]
[list]
[*]/asdisassemble [overwrite OR drop] Disassemble the current ship. If the "overwrite" parameter is added, the ship will disassemble anyways and overwrite existing world blocks in the process if any. If the "drop" parameter is added, the ship will drop to items.
[*]/asdisassemblenear [range]
Disassembles the nearest ship in a given range. If the range is not indicated, a range of 16 blocks is used. The ship will drop to items if it cannot disassemble at its current location.
[/list]
[/list]

[img]http://i.imgur.com/mGJO8Xe.jpg[/img]

[anchor=videos][size=medium][b]Videos[/b][/size][/anchor]
A mod showcase by ericton:
[media]http://www.youtube.com/embed/9qDgaxxzzQg[/media]

The Yogscast using Archimedes' Ships:
[media]http://www.youtube.com/embed/RhiybUoIFsk[/media]

A mod showcase by UnspeakableGaming:
[media]http://www.youtube.com/embed/SdDabsIcxPk[/media]

The funniest mod showcase ever by XerainGaming:
[media]http://www.youtube.com/embed/oCzACqDPnyc[/media]

[anchor=banners][size=medium][b]Banners[/b][/size][/anchor]
If you like the mod and want to support it, paste this code in your signature:
[code][url=http://www.minecraftforum.net/topic/1857899-][img]http://i.imgur.com/xaftzO0.png[/img][/url][/code] [img]http://i.imgur.com/xaftzO0.png[/img]

[anchor=modpacks][size=medium][b]Modpacks[/b][/size][/anchor]
[spoiler]
[list]
[*]Private modpacks: Free to include Archimedes' Ships.
[*]Public modpacks (no profit): Free to include Archimedes' Ships, as long as the modpack is not monetized. Credit to the original author must be given and a link to this thread must be available.
[*]Public modpacks (monetized): Please request permission to the original author, incuding a detailed description of the modpack, Minecraft version and location of distribution. If you do not fulfill in these requirements, your request will be ignored. Credit to the original author must be given and a link to this thread must be available.
[/list]
[/spoiler]

[anchor=faq][size=medium][b]FAQ[/b][/size][/anchor]
[list]
[list]
[*][i]Will players be able to walk around ships while sailing?[/i]
The way the mod is currently shaped, this is quite impossible. Because of the ships' smooth motion, small synchronization errors between (integrated) server and client occur, making it impossible to do proper collision resolution.
[/list]
[/list]

[anchor=changelog][size=medium][b]Changelog[/b][/size][/anchor]
[spoiler][code] ===Version 1.7.1===
- Updated to Minecraft 1.7.10.
- Added mass property to the Helm GUI.

===Version 1.7.0===
- Floating mechanics now depend on the density of the blocks and materials on the ship. Configurable in the config file.
- Removed advanced floating mechanics, since it's now always enabled.
- Added a new algorithm to assemble ships, which allows for larger ships but could be a heavier load for CPU. Enable it in the config file.
- Patched up all chat commands. Changed /asdismount to /asdisassemble and /askill to /assdisassemblenear.
- Ships now bank when making turns. Banking multiplier configurable.
- Entities do not escape from crates anymore.
- Right click an entity to release it from a crate.
- Crates can now only be placed on top of another block.
- Fixed dead entities not being removed from crates.
- Added safety for Tessellator not being ended.
- Added Dutch translations.
- Modified English translations, stopped using the words 'compile' and 'decompile', which are rather programming terms.

===Version 1.6.1===
- Fixed server crashes.

===Version 1.6.0===
- Added a crate block to transport mobs and some other entities.
- Changed engine name to Steam Engine.
- Enhanced engine texture.
- Added steam engine block crafting recipe and full implementation.
- Added Error safety in network pipeline.
- Added Russian translations.
- Added a whitelist of blocks to the config file.
- Improved client-side tile entity instance cleanup to possibly fix world leaking.
- Improved synchronization of tile entities.
- Increased floater floating capability.
- You can now remount a ship even when any block meta data has changed.
- Fixed a huge network leak where a lot of useless information is sent to the client.

===Version 1.5.2===
- Fixed max entity boundingbox check radius increasing with every ship built.
- Fixed server crashing when dismounting ship.

===Version 1.5.1===
- Removed /asalign command.
- Fixed passenger seats not working.
- Fixed pilot seat in wrong position for very large ships.
- Fixed pilot seat in wrong position after loading the world.
- Improved synchronization for unmounted ships.
- Readded readme file to the download.

===Version 1.5.0===
- Added a compile status GUI to the helm block. The latest compiled ship is now saved in the tile entity.
- Added a GUI to the ship entity.
- Added the possibility to name a ship.
- IMPORTANT Pressing shift to dismount a ship no longer decompiles it, but keeps the ship as an entity. This can be re-enabled through the config file.
- Made Helm and Seat flammable.
- Added a keybinding to decompile the ship: backslash '\'
- Added keybinding settings to the config file.
- Increased base turn speed.
- Improved alignment.
- Improved parachute.
- Improved gauge render performance.
- Implemented usage of Forge built-in block rotation method.
- Added language file support.
- Removed right click dismount setting.
- Removed mrot reload command.

===Version 1.4.5===
- Renamed the ship marker to the 'ship's helm'.
- Removed 'remount on decompilation fail' setting.
- Fixed decompilation fail mounting issues.
- Fixed entity disappearing when decompiling above world.
- Fixed /asinfo command rounding balloon percentage.

===Version 1.4.4===
- Added version check for default.mrot file.
- Fixed ships colliding with squids.
- Fixed item duplication bug on ship compilation.

===Version 1.4.3===
- Fixed tile entities not rendering properly.

===Version 1.4.2===
- Fixed parachutes crashing server.

===Version 1.4.1===
- Fixed ships could not be larger than about 1000 blocks.

===Version 1.4.0===
- Added passenger seats with built-in parachutes.
- Added more information to the /asinfo command.
- Added compressing of ship data before sending to increase max blocks on ship.
- Added shore buffer blocks which don not connect to ships.
- Added coloured balloon blocks.
- Added piston, trapdoor meta rotations.
- Added engine block functionality. (Not finished & craftable yet)
- Added buffer block.
- Improved dismounting.
- Decreased balloon block hardness.
- Fixed bounding box rotation.
- Fixed not all signs on ships synchronizing text.

===Version 1.3.6===
- Moved textures to the assets/archimedes/textures/ folder.
- Fixed huge lag caused by splash particles.

===Version 1.3.5===
- Fixed empty spaces being replaced with water on decompiling.
- Fixed being seated on wrong position on ships larger than 16*16*16 blocks.

===Version 1.3.4===
- Fixed server giving an error when creating a ship.
- Increased ship size limit to 3400 blocks.

===Version 1.3.3===
- Added a keybinding to align the ship to world grid without rejoining, can be used instead of the /asalign command.
- Added the detection of empty space blocks on a ship where no water can come, improving the floating mechanism.
- Improved mobile chunk connection algorithm performance.
- Fixed aligning a ship not rounding its position.
- Fixed balloons causing ships to sink much deeper.
- Fixed taking falling damage when landing with an airship.
- Removed the useless pack.mcmeta file from the download, fixing crashes with third party launchers.

===Version 1.3.2===
- Right clicking a mounted ship will dismount you again.
- Fixed landing on top of the ship or in the water after dismounting.

===Version 1.3.1===
- Added speed limit setting to the config file.
- Decreased default speed limit by 25%.
- Removed airship balloon ratio limits.
- Changed keybinding names.
- Fixed being able to dismount a ship by right clicking it.

===Version 1.3.0===
- Added airships, including a functionality for balloons.
- Added the possibility to build ships larger than 16*16*16 blocks.
- Added a functionality for floaters, making ships floating less deep.
- Added brake control to the control scheme, 'C' by default.
- Added the up and down controls to the control scheme, respectively 'X' and 'Z' by default.
- Added bed meta rotations.
- Added an option to the config file to change the turn speed.
- Added the blocks IDs to the config file which can be overwritten by a decompiling ship.
- Added an option to the config file to change the synchronization rate of ships between server and client.
- Added an option to the config file to determine if the player should remount the ship if decompilation failed.
- Added more options to the config file along with the new airship features.
- Added extended gauges, showing height and vertical velocity beside the standard gauges.
- Changed the look of the compass gauge.
- Made gauges not render pointers when player is further than 16 blocks far.
- Decreased base turn speed.
- Made ships turn more smoothly.
- Player will not get inside an opaque block anymore when mounted on a ship.
- Improved performace, especially for large ships.
- Fixed ships gaining huge vertical momentum when in a waterfall or lavafall.
- Fixed blocks from other mods that act as air blocks not being added to the ship.
- Fixed certain blocks dropping as items on rejoining with the world, like beds, doors, torches and ladders.

===Version 1.2.0===
- Changed the ship controls.
- Added a property to the config file to change ship control type.
- Changed the speed limit from 43.2 km/h to 144 km/h.
- Added a white south pole pointer to the compass gauge.
- Made ships move more smoothly -> increased sync rate to 1 per second again.
- Ships can now contain multiple ship markers.
- Added the /as (OR /ashelp OR /as?) command, listing all AS commands.
- Added the /asdestroy [range] command, killing the nearest ship in range. Dropping items if it can't decompile.
- Added the /asalign, aligning the ship you're riding on to the grid, without rejoining with the world.
- Fixed ship splash particles.
- Fixed ships not being influenced by liquid streams.
- Fixed ships being blocked by squids and other mobs.
- Fixed very thin pointers on gauges when mounted on a large ship.
- Fixed torches and ladders dropping of the ship when rejoining with the world. (Still ladders & signs still happening)
- Added crash-safety at the renderers when a block from another mod cannot render on ships.

===Version 1.1.2===
- Fixed sometimes not being able to dismount large ships.
- Fixed certain tile entities not meta-rotating.
- Fixed signs not being displayed on ships.

===Version 1.1.1===
- Added meta rotation of furnaces, dispensers and chests, redstone repeaters, levers, buttons and doors.
- Added tall grass to the forbidden blocks.
- Made the player stand behind the steering wheel.
- Fixed the server crash on startup.
- Fixed some large ships bugs.

===Version 1.1.0===
- Added a list of forbidden blocks to the configuration file.
- Added soul sand to the forbidden blocks.
- Added a fileloader to read meta rotations from the .mrot files located in the .minecraft/config/ArchimedesShips/ directory.
- Added ladder and fence gate meta rotations.
- Tileentities are now saved between games.
- Readded pushing between boats and ships.
- Increased the server-client sync rate of ships when playing singleplayer or in a LAN world.
- Changed the command names.
- Added a parameter to the asdismount command to overwrite world blocks with ship blocks.
- Changed the display name of the gauges block to Measurement Gauges.
- Changed the gauges tileentity tag to fix compatibility issues with other mods.
- Fixed crashes with ships with leaves.
- Fixed crashes when creating a ship larger than 16 blocks wide, deep or high.

===Version 1.0.0===
- Initial release.
[/code][/spoiler]

[anchor=todo][size=medium][b]Bugs & To-do[/b][/size][/anchor]
[spoiler]To-do:
[list]
[*]Improve commands and permissions.
[*](Possibly) Make fuel inventory per-ship instead of per-block to be accessible while sailing.
[/list]
Bugs:
[list]
[*]Passengers may drop out of the ship when going fast.
[*]Creating a ship with light sources may glitch world lighting. Fix by reloading the chunks.
[/list]
[/spoiler]

[anchor=oldversions][size=medium][b]Old versions[/b][/size][/anchor]
[spoiler](1.6.2) v1.3.6: [url=http://www.mediafire.com/download/jsl73tijlgs61t6/ArchimedesShips_v1.3.6.zip]http://www.mediafire...hips_v1.3.6.zip[/url]
(1.5.2) v1.3.5: http://www.mediafire.com/download/91j913bdc0d539z/ArchimedesShips_v1.3.5_for_1.5.2.zip
(1.5.2) v1.2.0: [url=http://www.mediafire.com/download/ajxnjroj60zagxk/ArchimedesShips_v1.2.0.zip]http://www.mediafire...hips_v1.2.0.zip[/url]
(1.5.2) v1.1.2: [url=http://www.mediafire.com/download/4go2rl6wb8zja4d/ArchimedesShips_v1.1.2.zip]http://www.mediafire...hips_v1.1.2.zip[/url]
(1.5.2) v1.0.0: [url=http://www.mediafire.com/download/zjj6psa1zb597vs/ArchimedesShips_v1.0.0.zip]http://www.mediafire...hips_v1.0.0.zip[/url][/spoiler]