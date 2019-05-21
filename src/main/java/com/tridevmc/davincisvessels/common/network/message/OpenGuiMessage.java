package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import com.tridevmc.davincisvessels.common.IElementProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkHooks;

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
    public void handle(EntityPlayer sender) {
        if (!(sender instanceof EntityPlayerMP))
            return;
        NetworkHooks.openGui((EntityPlayerMP) sender,
                (IElementProvider) sender.getEntityWorld().getEntityByID(entityId),
                (p) -> p.writeVarInt(entityId));
    }
}
