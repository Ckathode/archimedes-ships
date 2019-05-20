package com.tridevmc.davincisvessels.common.network.message;

import com.tridevmc.compound.network.message.Message;
import com.tridevmc.compound.network.message.RegisteredMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.LogicalSide;

/**
 * Created by darkevilmac on 2/3/2017.
 */
@RegisteredMessage(channel = "davincisvessels", destination = LogicalSide.CLIENT)
public class TranslatedChatMessage extends Message {

    public String message;

    public TranslatedChatMessage(String message) {
        super();
        this.message = message;
    }

    public TranslatedChatMessage() {
        super();
    }

    @Override
    public void handle(EntityPlayer sender) {
        if (sender == null)
            return;

        String[] split = message.split("~");
        TextComponentString text = new TextComponentString("");

        if (split.length > 0) {
            for (String string : split) {
                if (string.startsWith("TR:")) {
                    text.appendSibling(new TextComponentTranslation(string.substring(3)));
                } else {
                    text.appendSibling(new TextComponentString(string));
                }
            }
        }

        sender.sendMessage(text);
    }
}
