package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import com.tridevmc.davincisvessels.common.DavincisUIHooks;
import com.tridevmc.davincisvessels.common.IElementProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.LogicalSide;

@RegisteredMessage(channel = "davincisvessels", destination = LogicalSide.SERVER)
public class OpenGuiMessage extends Message {

    public int entityId;

    public OpenGuiMessage(int entityId) {
        super();
        this.entityId = entityId;
    }

    public OpenGuiMessage() {
        super();
    }

    @Override
    public void handle(PlayerEntity sender) {
        if (!(sender instanceof ServerPlayerEntity))
            return;
        DavincisUIHooks.openGui(sender, (IElementProvider) sender.world.getEntityByID(entityId));
    }
}
