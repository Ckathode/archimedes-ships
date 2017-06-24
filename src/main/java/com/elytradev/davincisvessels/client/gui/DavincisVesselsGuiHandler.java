package com.elytradev.davincisvessels.client.gui;

import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.elytradev.davincisvessels.common.tileentity.TileAnchorPoint;
import com.elytradev.davincisvessels.common.tileentity.TileEngine;
import com.elytradev.davincisvessels.common.tileentity.TileHelm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class DavincisVesselsGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te;
        switch (ID) {
            case 1:
                te = world.getTileEntity(pos);
                if (te instanceof TileHelm) {
                    return new ContainerHelm((TileHelm) te, player);
                }
                return null;
            case 2:
                if (player.getRidingEntity() instanceof EntityShip) {
                    EntityShip ship = (EntityShip) player.getRidingEntity();
                    return new ContainerShip(ship, player);
                }
                return null;
            case 3:
                te = world.getTileEntity(pos);
                if (te instanceof TileEngine) {
                    return new ContainerEngine((TileEngine) te, player);
                }
                return null;
            case 4:
                te = world.getTileEntity(pos);
                if (te instanceof TileAnchorPoint) {
                    return new ContainerAnchorPoint((TileAnchorPoint) te, player);
                }
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te;
        switch (ID) {
            case 1:
                te = world.getTileEntity(pos);
                if (te instanceof TileHelm) {
                    return new GuiHelm((TileHelm) te, player);
                }
            case 2:
                if (player.getRidingEntity() instanceof EntityShip) {
                    EntityShip ship = (EntityShip) player.getRidingEntity();
                    return new GuiShip(ship, player);
                }
            case 3:
                te = world.getTileEntity(pos);
                if (te instanceof TileEngine) {
                    return new GuiEngine((TileEngine) te, player);
                }
            case 4:
                te = world.getTileEntity(pos);
                if (te instanceof TileAnchorPoint) {
                    return new GuiAnchorPoint((TileAnchorPoint) te, player);
                }
            default:
                return null;
        }
    }
}
