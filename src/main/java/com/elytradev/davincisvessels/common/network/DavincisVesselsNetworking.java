package com.elytradev.davincisvessels.common.network;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.network.message.*;
import com.elytradev.concrete.NetworkContext;


public class DavincisVesselsNetworking {

    public static NetworkContext NETWORK;

    public static void setupNetwork() {
        DavincisVesselsMod.LOG.info("Setting up network...");
        DavincisVesselsNetworking.NETWORK = registerPackets();
        DavincisVesselsMod.LOG.info("Setup network! " + DavincisVesselsNetworking.NETWORK.toString());
    }

    private static NetworkContext registerPackets() {
        NetworkContext context = NetworkContext.forChannel("DavincisVessels");

        context.register(AssembleResultMessage.class);
        context.register(RequestSubmerseMessage.class);
        context.register(HelmActionMessage.class);
        context.register(RenameShipMessage.class);
        context.register(OpenGuiMessage.class);
        context.register(AnchorPointMessage.class);
        context.register(ControlInputMessage.class);
        context.register(TranslatedChatMessage.class);
        context.register(ConfigMessage.class);

        return context;
    }
}