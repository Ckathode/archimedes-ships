package io.github.elytra.davincisvessels.common.network.message;

import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by darkevilmac on 2/2/2017.
 */
@ReceivedOn(Side.CLIENT)
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
