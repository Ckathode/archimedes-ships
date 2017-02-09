package io.github.elytra.davincisvessels.common.network.message;

import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.client.ClientProxy;
import io.github.elytra.davincisvessels.common.DavincisVesselsConfig;
import io.github.elytra.davincisvessels.common.network.DavincisVesselsNetworking;
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

        if (DavincisVesselsMod.PROXY != null && DavincisVesselsMod.PROXY instanceof ClientProxy) {
            if (config != null) {
                ((ClientProxy) DavincisVesselsMod.PROXY).syncedConfig = DavincisVesselsMod.INSTANCE.getLocalConfig();
                ((ClientProxy) DavincisVesselsMod.PROXY).syncedConfig.setShared(config);
            } else {
                ((ClientProxy) DavincisVesselsMod.PROXY).syncedConfig = null;
            }
        }
    }
}
