package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import com.tridevmc.davincisvessels.DavincisVesselsMod;
import com.tridevmc.davincisvessels.common.entity.EntityVessel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.LogicalSide;

/**
 * Created by darkevilmac on 1/29/2017.
 */
@RegisteredMessage(channel = "davincisvessels", destination = LogicalSide.SERVER)
public class RequestSubmerseMessage extends Message {

    public EntityVessel vessel;
    public boolean doSumberse;

    public RequestSubmerseMessage(EntityVessel vessel, boolean doSumberse) {
        super();
        this.vessel = vessel;
        this.doSumberse = doSumberse;
    }

    public RequestSubmerseMessage() {
        super();
    }

    @Override
    public void handle(PlayerEntity sender) {
        if (vessel != null) {
            if (doSumberse && !vessel.canSubmerge()) {
                if (sender instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) sender).connection.disconnect(new StringTextComponent("Invalid submerse request!" +
                            "\nCheating to go underwater... reconsider your life choices."));
                    DavincisVesselsMod.LOG.warn("A user tried to submerse in a vessel that can't, user info: " + sender.getGameProfile().toString());
                }
                return;
            }

            vessel.setSubmerge(doSumberse);
            // TODO: Achievements are gone.
            //sender.addStat(DavincisVesselsContent.achievementSubmerseVessel);
        }
    }
}
