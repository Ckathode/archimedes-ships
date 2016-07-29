package darkevilmac.archimedes.common.network;

import com.unascribed.lambdanetwork.BiConsumer;
import com.unascribed.lambdanetwork.DataType;
import com.unascribed.lambdanetwork.LambdaNetwork;
import com.unascribed.lambdanetwork.LambdaNetworkBuilder;
import com.unascribed.lambdanetwork.Token;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.UUID;

import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.client.ClientProxy;
import darkevilmac.archimedes.client.gui.ContainerHelm;
import darkevilmac.archimedes.common.ArchimedesConfig;
import darkevilmac.archimedes.common.entity.EntityShip;
import darkevilmac.archimedes.common.entity.ShipAssemblyInteractor;
import darkevilmac.archimedes.common.tileentity.AnchorInstance;
import darkevilmac.archimedes.common.tileentity.TileEntityAnchorPoint;
import darkevilmac.archimedes.common.tileentity.TileEntityHelm;
import darkevilmac.movingworld.common.chunk.assembly.AssembleResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class ArchimedesShipsNetworking {

    public static LambdaNetwork NETWORK;

    public static void setupNetwork() {
        ArchimedesShipMod.modLog.info("Setting up network...");
        ArchimedesShipsNetworking.NETWORK = registerPackets(LambdaNetwork.builder().channel("ArchimedesShipsPlus")).build();
        ArchimedesShipMod.modLog.info("Setup network! " + ArchimedesShipsNetworking.NETWORK.toString());
    }

    private static LambdaNetworkBuilder registerPackets(LambdaNetworkBuilder builder) {
        builder = builder.packet("AssembleResultMessage").boundTo(Side.CLIENT)
                .with(DataType.ARBITRARY, "result").handledBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        ByteBuf buf = Unpooled.wrappedBuffer(token.getData("result"));
                        boolean prevFlag = buf.readBoolean();
                        byte resultCode = buf.readByte();
                        AssembleResult result = new AssembleResult(AssembleResult.ResultType.fromByte(resultCode), buf);
                        result.assemblyInteractor = new ShipAssemblyInteractor().fromByteBuf(resultCode, buf);

                        if (entityPlayer != null && entityPlayer.openContainer instanceof ContainerHelm) {
                            if (prevFlag) {
                                ((ContainerHelm) entityPlayer.openContainer).tileEntity.setPrevAssembleResult(result);
                                ((ContainerHelm) entityPlayer.openContainer).tileEntity.getPrevAssembleResult().assemblyInteractor = result.assemblyInteractor;
                            } else {
                                ((ContainerHelm) entityPlayer.openContainer).tileEntity.setAssembleResult(result);
                                ((ContainerHelm) entityPlayer.openContainer).tileEntity.getAssembleResult().assemblyInteractor = result.assemblyInteractor;
                            }
                        }
                    }
                });

        builder = builder.packet("ClientRequestSubmerseMessage").boundTo(Side.SERVER)
                .with(DataType.INT, "dimID")
                .with(DataType.INT, "entityID")
                .with(DataType.BOOLEAN, "submerse").handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        boolean submerse = token.getBoolean("submerse");
                        World world = DimensionManager.getWorld(token.getInt("dimID"));
                        if (world != null) {
                            Entity unCast = world.getEntityByID(token.getInt("entityID"));
                            if (unCast != null && unCast instanceof EntityShip) {
                                EntityShip ship = (EntityShip) unCast;

                                if (submerse && !ship.canSubmerge()) {
                                    if (entityPlayer != null && entityPlayer instanceof EntityPlayerMP)
                                        ((EntityPlayerMP) entityPlayer).connection.kickPlayerFromServer("Oi, stop hacking ya moron.  \n XOXO ~Archimedes");
                                    return;
                                }

                                ship.setSubmerge(submerse);
                            }
                        }
                    }
                });

        builder = builder.packet("ClientHelmActionMessage").boundTo(Side.SERVER)
                .with(DataType.INT, "action")
                .with(DataType.INT, "tileX")
                .with(DataType.INT, "tileY")
                .with(DataType.INT, "tileZ")
                .handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        BlockPos pos = new BlockPos(token.getInt("tileX"),
                                token.getInt("tileY"), token.getInt("tileZ"));
                        HelmClientAction action = HelmClientAction.fromInt((byte) token.getInt("action"));

                        if (entityPlayer.worldObj.getTileEntity(pos) != null && entityPlayer.worldObj.getTileEntity(pos) instanceof TileEntityHelm) {
                            TileEntityHelm tileEntity = (TileEntityHelm) entityPlayer.worldObj.getTileEntity(pos);
                            switch (action) {
                                case ASSEMBLE:
                                    tileEntity.assembleMovingWorld(entityPlayer);
                                    break;
                                case MOUNT:
                                    tileEntity.mountMovingWorld(entityPlayer, tileEntity.getMovingWorld(tileEntity.getWorld()));
                                    break;
                                case UNDOCOMPILE:
                                    tileEntity.undoCompilation(entityPlayer);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });

        builder = builder.packet("ClientRenameShipMessage").boundTo(Side.SERVER)
                .with(DataType.STRING, "newName")
                .with(DataType.INT, "tileX")
                .with(DataType.INT, "tileY")
                .with(DataType.INT, "tileZ")
                .handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        BlockPos pos = new BlockPos(token.getInt("tileX"),
                                token.getInt("tileY"), token.getInt("tileZ"));

                        if (entityPlayer.worldObj.getTileEntity(pos) != null && entityPlayer.worldObj.getTileEntity(pos) instanceof TileEntityHelm) {
                            TileEntityHelm helm = (TileEntityHelm) entityPlayer.worldObj.getTileEntity(pos);
                            helm.getInfo().setName(token.getString("newName"));
                        }
                    }
                });

        builder = builder.packet("ClientOpenGUIMessage").boundTo(Side.SERVER)
                .with(DataType.INT, "guiID").handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        entityPlayer.openGui(ArchimedesShipMod.instance, token.getInt("guiID"), entityPlayer.worldObj, 0, 0, 0);
                    }
                });

        builder = builder.packet("ClientAnchorPointActionMessage").boundTo(Side.SERVER)
                .with(DataType.BYTE, "actionId")
                .with(DataType.INT, "tileX")
                .with(DataType.INT, "tileY")
                .with(DataType.INT, "tileZ")
                .handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        World world = entityPlayer.worldObj;
                        BlockPos anchorPos = new BlockPos(token.getInt("tileX"), token.getInt("tileY"), token.getInt("tileZ"));
                        if (world == null || world.getTileEntity(anchorPos) == null || !(world.getTileEntity(anchorPos) instanceof TileEntityAnchorPoint))
                            return;

                        TileEntityAnchorPoint anchorPoint = (TileEntityAnchorPoint) world.getTileEntity(anchorPos);

                        if (token.getInt("actionId") == 0) {
                            // Switch
                            /**
                             * Clear the entries as well as notify the entries to clear us from them.
                             * Then switch mode.
                             */
                            for (HashMap.Entry<UUID, BlockPos> e : anchorPoint.instance.getRelatedAnchors().entrySet()) {
                                if (world.getTileEntity(e.getValue()) != null && world.getTileEntity(e.getValue()) instanceof TileEntityAnchorPoint) {
                                    TileEntityAnchorPoint entryAnchorPoint = (TileEntityAnchorPoint) world.getTileEntity(e.getValue());
                                    entryAnchorPoint.instance.removeRelation(anchorPoint.instance.getIdentifier());
                                } else {
                                    ArchimedesShipMod.modLog.error("Invalid entries in anchor tile: " + anchorPoint.toString() + ", cleaning.");
                                }
                            }

                            anchorPoint.instance.clearRelations();
                            anchorPoint.instance.setType(anchorPoint.instance.getType().opposite());
                            anchorPoint.instance.setIdentifier(UUID.randomUUID());
                        } else if (anchorPoint.content != null) {
                            // Link
                            /**
                             * As a note, we don't set the relation of our own anchor because the anchor we
                             * would relate it to has yet to be placed, we set this info when the anchor is placed.
                             */
                            if (anchorPoint.instance.getType() == AnchorInstance.InstanceType.FORLAND) {
                                if (!anchorPoint.content.getTagCompound().hasKey("instance")) {
                                    AnchorInstance itemAnchorInstanceTag = new AnchorInstance();

                                    itemAnchorInstanceTag.setType(AnchorInstance.InstanceType.FORSHIP);
                                    itemAnchorInstanceTag.setIdentifier(UUID.randomUUID());
                                    itemAnchorInstanceTag.addRelation(anchorPoint.instance.getIdentifier(), anchorPos);
                                    anchorPoint.content.getTagCompound().setTag("instance", itemAnchorInstanceTag.serializeNBT());
                                } else {
                                    AnchorInstance instanceFromKey = new AnchorInstance();
                                    instanceFromKey.deserializeNBT(anchorPoint.content.getTagCompound().getCompoundTag("instance"));

                                    if (instanceFromKey.getType() == AnchorInstance.InstanceType.FORLAND) {
                                        // Incorrect type, clear it.
                                        instanceFromKey.setIdentifier(UUID.randomUUID());
                                        instanceFromKey.setType(AnchorInstance.InstanceType.FORSHIP);
                                        instanceFromKey.clearRelations();
                                    }

                                    instanceFromKey.addRelation(anchorPoint.instance.getIdentifier(), anchorPos);
                                    anchorPoint.content.getTagCompound().setTag("instance", instanceFromKey.serializeNBT());
                                }
                            } else {
                                if (!anchorPoint.content.getTagCompound().hasKey("instance")) {
                                    if (!anchorPoint.content.getTagCompound().hasKey("instance")) {
                                        AnchorInstance itemAnchorInstanceTag = new AnchorInstance();

                                        itemAnchorInstanceTag.setType(AnchorInstance.InstanceType.FORLAND);
                                        itemAnchorInstanceTag.setIdentifier(UUID.randomUUID());
                                        itemAnchorInstanceTag.addRelation(anchorPoint.instance.getIdentifier(), anchorPos);
                                        anchorPoint.content.getTagCompound().setTag("instance", itemAnchorInstanceTag.serializeNBT());
                                    } else {
                                        AnchorInstance instanceFromKey = new AnchorInstance();
                                        instanceFromKey.deserializeNBT(anchorPoint.content.getTagCompound().getCompoundTag("instance"));

                                        if (instanceFromKey.getType() == AnchorInstance.InstanceType.FORSHIP) {
                                            // Incorrect type, clear it.
                                            instanceFromKey.setIdentifier(UUID.randomUUID());
                                            instanceFromKey.setType(AnchorInstance.InstanceType.FORLAND);
                                            instanceFromKey.clearRelations();
                                        }

                                        instanceFromKey.addRelation(anchorPoint.instance.getIdentifier(), anchorPos);
                                        anchorPoint.content.getTagCompound().setTag("instance", instanceFromKey.serializeNBT());
                                    }
                                }
                            }
                        }

                    }
                });

        builder = builder.packet("ControlInputMessage").boundTo(Side.SERVER)
                .with(DataType.INT, "dimID")
                .with(DataType.INT, "entityID")
                .with(DataType.BYTE, "control").handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        World world = DimensionManager.getWorld(token.getInt("dimID"));
                        if (world != null) {
                            Entity unCast = world.getEntityByID(token.getInt("entityID"));
                            if (unCast != null && unCast instanceof EntityShip) {
                                EntityShip ship = (EntityShip) unCast;

                                ship.getController().updateControl(ship, entityPlayer, token.getInt("control"));
                            }
                        }
                    }
                });


        builder = builder.packet("TranslatedChatMessage").boundTo(Side.CLIENT)
                .with(DataType.STRING, "message").handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        if (entityPlayer == null)
                            return;

                        String message = token.getString("message");

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

                        entityPlayer.addChatComponentMessage(text);
                    }
                });

        builder = builder.packet("ConfigMessage").boundTo(Side.CLIENT)
                .with(DataType.NBT_COMPOUND, "data")
                .handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        NBTTagCompound tag = token.getNBT("data");
                        ArchimedesConfig.SharedConfig config = null;

                        if (!tag.getBoolean("restore")) {
                            config = ArchimedesShipMod.instance.getLocalConfig().getShared()
                                    .deserialize(tag);
                        }

                        if (ArchimedesShipMod.proxy != null && ArchimedesShipMod.proxy instanceof ClientProxy) {
                            if (config != null) {
                                ((ClientProxy) ArchimedesShipMod.proxy).syncedConfig = ArchimedesShipMod.instance.getLocalConfig();
                                ((ClientProxy) ArchimedesShipMod.proxy).syncedConfig.setShared(config);
                            } else {
                                ((ClientProxy) ArchimedesShipMod.proxy).syncedConfig = null;
                            }
                        }

                    }
                });

        return builder;
    }
}