package darkevilmac.archimedes.client.gui;

import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.tileentity.TileEntityEngine;
import darkevilmac.archimedes.common.tileentity.TileEntityHelm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ASGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te;
        switch (ID) {
            case 1:
                te = world.getTileEntity(pos);
                if (te instanceof TileEntityHelm) {
                    return new ContainerHelm((TileEntityHelm) te, player);
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
                if (te instanceof TileEntityEngine) {
                    return new ContainerEngine((TileEntityEngine) te, player);
                }
                return null;
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
                if (te instanceof TileEntityHelm) {
                    return new GuiHelm((TileEntityHelm) te, player);
                }
                return null;
            case 2:
                if (player.getRidingEntity() instanceof EntityShip) {
                    EntityShip ship = (EntityShip) player.getRidingEntity();
                    return new GuiShip(ship, player);
                }
                return null;
            case 3:
                te = world.getTileEntity(pos);
                if (te instanceof TileEntityEngine) {
                    return new GuiEngine((TileEntityEngine) te, player);
                }
                return null;
            default:
                return null;
        }
    }
}
