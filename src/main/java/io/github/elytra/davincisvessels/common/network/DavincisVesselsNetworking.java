package io.github.elytra.davincisvessels.common.network;

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
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.UUID;

import io.github.elytra.davincisvessels.DavincisVesselsMod;
import io.github.elytra.davincisvessels.client.ClientProxy;
import io.github.elytra.davincisvessels.client.gui.ContainerHelm;
import io.github.elytra.davincisvessels.common.DavincisVesselsConfig;
import io.github.elytra.davincisvessels.common.entity.EntityShip;
import io.github.elytra.davincisvessels.common.entity.ShipAssemblyInteractor;
import io.github.elytra.davincisvessels.common.object.DavincisVesselsObjects;
import io.github.elytra.davincisvessels.common.tileentity.AnchorInstance;
import io.github.elytra.davincisvessels.common.tileentity.BlockLocation;
import io.github.elytra.davincisvessels.common.tileentity.TileAnchorPoint;
import io.github.elytra.davincisvessels.common.tileentity.TileHelm;
import io.github.elytra.movingworld.common.chunk.assembly.AssembleResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class DavincisVesselsNetworking {

    public static LambdaNetwork NETWORK;

    public static void setupNetwork() {
        DavincisVesselsMod.LOG.info("Setting up network...");
        DavincisVesselsNetworking.NETWORK = registerPackets(LambdaNetwork.builder().channel("ArchimedesShipsPlus")).build();
        DavincisVesselsMod.LOG.info("Setup network! " + DavincisVesselsNetworking.NETWORK.toString());
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
                                        ((EntityPlayerMP) entityPlayer).connection.disconnect("Oi, stop hacking ya moron.  \n XOXO ~Da Vinci");
                                    return;
                                }

                                ship.setSubmerge(submerse);
                                entityPlayer.addStat(DavincisVesselsObjects.achievementSubmerseShip);
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

                        if (entityPlayer.world.getTileEntity(pos) != null && entityPlayer.world.getTileEntity(pos) instanceof TileHelm) {
                            TileHelm tileEntity = (TileHelm) entityPlayer.world.getTileEntity(pos);
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

                        if (entityPlayer.world.getTileEntity(pos) != null && entityPlayer.world.getTileEntity(pos) instanceof TileHelm) {
                            TileHelm helm = (TileHelm) entityPlayer.world.getTileEntity(pos);
                            helm.getInfo().setName(token.getString("newName"));
                        }
                    }
                });

        builder = builder.packet("ClientOpenGUIMessage").boundTo(Side.SERVER)
                .with(DataType.INT, "guiID").handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        entityPlayer.openGui(DavincisVesselsMod.INSTANCE, token.getInt("guiID"), entityPlayer.world, 0, 0, 0);
                    }
                });

        builder = builder.packet("ClientAnchorPointActionMessage").boundTo(Side.SERVER)
                .with(DataType.BYTE, "actionID")
                .with(DataType.INT, "tileX")
                .with(DataType.INT, "tileY")
                .with(DataType.INT, "tileZ")
                .handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        World world = entityPlayer.world;
                        BlockPos anchorPos = new BlockPos(token.getInt("tileX"), token.getInt("tileY"), token.getInt("tileZ"));
                        if (world == null || world.getTileEntity(anchorPos) == null || !(world.getTileEntity(anchorPos) instanceof TileAnchorPoint))
                            return;

                        TileAnchorPoint anchorPoint = (TileAnchorPoint) world.getTileEntity(anchorPos);

                        if (token.getInt("actionID") == 0) {
                            // Switch
                            /**
                             * Clear the entries as well as notify the entries to clear us from them.
                             * Then switch mode.
                             */
                            for (HashMap.Entry<UUID, BlockLocation> e : anchorPoint.getInstance().getRelatedAnchors().entrySet()) {
                                if (world.getTileEntity(e.getValue().pos) != null && world.getTileEntity(e.getValue().pos) instanceof TileAnchorPoint) {
                                    TileAnchorPoint entryAnchorPoint = (TileAnchorPoint) world.getTileEntity(e.getValue().pos);
                                    ((EntityPlayerMP) entityPlayer).connection.sendPacket(entryAnchorPoint.getUpdatePacket());
                                } else {
                                    DavincisVesselsMod.LOG.error("Invalid entries in anchor tile: " + anchorPoint.toString() + ", cleaning.");
                                }
                            }

                            anchorPoint.getInstance().clearRelations();
                            anchorPoint.getInstance().setType(anchorPoint.getInstance().getType().opposite());
                            anchorPoint.getInstance().setIdentifier(UUID.randomUUID());
                            anchorPoint.markDirty();
                        } else if (anchorPoint.content != null) {
                            // Link
                            /**
                             * As a note, we don't set the relation of our own anchor because the anchor we
                             * would relate it to has yet to be placed, we set this info when the anchor is placed.
                             */
                            if (anchorPoint.getInstance().getType() == AnchorInstance.InstanceType.FORLAND) {
                                if (anchorPoint.content.getTagCompound() == null) {
                                    anchorPoint.content.setTagCompound(new NBTTagCompound());
                                }
                                if (anchorPoint.content.getTagCompound().hasKey("INSTANCE"))
                                    anchorPoint.content.getTagCompound().removeTag("INSTANCE");
                                AnchorInstance itemAnchorInstanceTag = new AnchorInstance();

                                itemAnchorInstanceTag.setType(AnchorInstance.InstanceType.FORSHIP);
                                itemAnchorInstanceTag.setIdentifier(UUID.randomUUID());
                                itemAnchorInstanceTag.addRelation(anchorPoint.getInstance().getIdentifier(),
                                        new BlockLocation(anchorPos, entityPlayer.world.provider.getDimension()));
                                anchorPoint.content.getTagCompound().setTag("INSTANCE", itemAnchorInstanceTag.serializeNBT());
                            } else {
                                if (anchorPoint.content.getTagCompound() == null) {
                                    anchorPoint.content.setTagCompound(new NBTTagCompound());
                                }
                                if (anchorPoint.content.getTagCompound().hasKey("INSTANCE"))
                                    anchorPoint.content.getTagCompound().removeTag("INSTANCE");

                                AnchorInstance itemAnchorInstanceTag = new AnchorInstance();

                                itemAnchorInstanceTag.setType(AnchorInstance.InstanceType.FORLAND);
                                itemAnchorInstanceTag.setIdentifier(UUID.randomUUID());
                                itemAnchorInstanceTag.addRelation(anchorPoint.getInstance().getIdentifier(),
                                        new BlockLocation(anchorPos, entityPlayer.world.provider.getDimension()));
                                anchorPoint.content.getTagCompound().setTag("INSTANCE", itemAnchorInstanceTag.serializeNBT());
                            }
                        }
                        anchorPoint.markDirty();
                        if (world instanceof WorldServer)
                            ((WorldServer) world).getPlayerChunkMap().markBlockForUpdate(anchorPoint.getPos());
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

                        entityPlayer.sendMessage(text);
                    }
                });

        builder = builder.packet("ConfigMessage").boundTo(Side.CLIENT)
                .with(DataType.NBT_COMPOUND, "data")
                .handledOnMainThreadBy(new BiConsumer<EntityPlayer, Token>() {
                    @Override
                    public void accept(EntityPlayer entityPlayer, Token token) {
                        NBTTagCompound tag = token.getNBT("data");
                        DavincisVesselsConfig.SharedConfig config = null;

                        if (!tag.getBoolean("restore")) {
                            config = DavincisVesselsMod.INSTANCE.getLocalConfig().getShared()
                                    .deserialize(tag);
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
                });

        return builder;
    }
}