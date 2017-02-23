package com.elytradev.davincisvessels.common.network.message;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.network.DavincisVesselsNetworking;
import com.elytradev.concrete.Message;
import com.elytradev.concrete.NetworkContext;
import com.elytradev.concrete.annotation.field.MarshalledAs;
import com.elytradev.concrete.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by darkevilmac on 2/2/2017.
 */
@ReceivedOn(Side.SERVER)
public class OpenGuiMessage extends Message {

    @MarshalledAs("i8")
    public int guiID;

    public OpenGuiMessage(int guiID) {
        super(DavincisVesselsNetworking.NETWORK);
        this.guiID = guiID;
    }

    public OpenGuiMessage(NetworkContext ctx) {
        super(ctx);
    }

    @Override
    protected void handle(EntityPlayer sender) {
        sender.openGui(DavincisVesselsMod.INSTANCE, guiID, sender.world, 0, 0, 0);
    }
}
