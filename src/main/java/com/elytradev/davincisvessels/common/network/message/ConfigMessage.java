package com.elytradev.davincisvessels.common.network.message;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.client.ClientProxy;
import com.elytradev.davincisvessels.common.DavincisVesselsConfig;
import com.elytradev.davincisvessels.common.network.DavincisVesselsNetworking;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by darkevilmac on 2/3/2017.
 */
@ReceivedOn(Side.CLIENT)
public class ConfigMessage extends Message {

    public NBTTagCompound data;

    public ConfigMessage(NBTTagCompound data) {
        super(DavincisVesselsNetworking.NETWORK);
        this.data = data;
    }

    public ConfigMessage(NetworkContext ctx) {
        super(ctx);
    }

    @Override
    protected void handle(EntityPlayer sender) {
        DavincisVesselsConfig.SharedConfig config = null;

        if (!data.getBoolean("restore")) {
            config = DavincisVesselsMod.INSTANCE.getLocalConfig().getShared()
                    .deserialize(data);
        }

        if (DavincisVesselsMod.PROXY instanceof ClientProxy) {
            if (config != null) {
                ((ClientProxy) DavincisVesselsMod.PROXY).syncedConfig = DavincisVesselsMod.INSTANCE.getLocalConfig();
                ((ClientProxy) DavincisVesselsMod.PROXY).syncedConfig.setShared(config);
            } else {
                ((ClientProxy) DavincisVesselsMod.PROXY).syncedConfig = null;
            }
        }
    }
}
