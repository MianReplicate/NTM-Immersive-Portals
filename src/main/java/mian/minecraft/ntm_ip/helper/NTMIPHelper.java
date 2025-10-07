package mian.minecraft.ntm_ip.helper;

import io.netty.buffer.Unpooled;
import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.PublicEntityExterior;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.exterior.EntityExterior;
import net.tardis.mod.exterior.Exterior;
import net.tardis.mod.exterior.TileExterior;
import net.tardis.mod.helpers.WorldHelper;
import net.tardis.mod.network.Network;
import net.tardis.mod.network.packets.SyncDimensionListMessage;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.MiscHelper;
import qouteall.q_misc_util.MiscNetworking;
import qouteall.q_misc_util.api.DimensionAPI;
import qouteall.q_misc_util.dimension.DimensionIdManagement;
import qouteall.q_misc_util.dimension.DimensionIdRecord;
import qouteall.q_misc_util.dimension.DimensionTypeSync;
import qouteall.q_misc_util.forge.events.ServerDimensionDynamicUpdateEvent;

public class NTMIPHelper {
    public static ResourceLocation createRL(String path) {
        return new ResourceLocation(NTMIP.MODID, path);
    }

    public static void updateKnownDimensions(MinecraftServer server, ResourceKey<Level> worldKey) {
        DimensionIdManagement.updateAndSaveServerDimIdRecord();
        DimensionAPI.saveDimensionConfiguration(worldKey);

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        CompoundTag idMapTag = DimensionIdRecord.recordToTag(DimensionIdRecord.serverRecord, (dim) ->
                MiscHelper.getServer().getLevel(worldKey) != null || dim.equals(worldKey));

        buf.writeNbt(idMapTag);
        CompoundTag typeMapTag = DimensionTypeSync.createTagFromServerWorldInfo();
        buf.writeNbt(typeMapTag);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(new ClientboundCustomPayloadPacket(MiscNetworking.id_stcDimSync, buf));
            Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncDimensionListMessage(worldKey, true));
        }
        MinecraftForge.EVENT_BUS.post(new ServerDimensionDynamicUpdateEvent(server.levelKeys()));
    }

    public static void teleportFollowers(ITardisLevel tardis, Player player, Level from, BlockPos previousPos) {
        if (!tardis.isClient()) {
            for (Entity entity : from
                    .getEntitiesOfClass(Entity.class, WorldHelper.getCenteredAABB(previousPos, 16.0F))) {
                if (entity instanceof OwnableEntity pet) {
                    if (player.getUUID().equals(pet.getOwnerUUID())) {
                        boolean var10000;
                        label26:
                        {
                            if (entity instanceof TamableAnimal animal) {
                                if (animal.isInSittingPose()) {
                                    var10000 = true;
                                    break label26;
                                }
                            }

                            var10000 = false;
                        }

                        boolean isSitting = var10000;
                        if (!isSitting) {
                            tardis.getExterior().getTeleportHandler()
                                    .teleportEntity((ServerLevel) player.level(), entity);
                        }
                    }
                }
            }
        }
    }

    public static Vec3 getActualTardisLocation(ITardisLevel tardis) {
        Exterior exterior = tardis.getExterior();
        if (exterior instanceof TileExterior) {
            return tardis.getLocation().getPos().getCenter();
        } else if (exterior instanceof EntityExterior entityExterior) {
            PublicEntityExterior publicEntityExterior = (PublicEntityExterior) entityExterior;
            Entity entity = publicEntityExterior.ntm_immersive_portals$getEntity();
            if (entity != null) {
                return publicEntityExterior.ntm_immersive_portals$getEntity().position();
            }
        }
        return tardis.getLocation().getPos().getCenter(); // fallback
    }

    public static float getActualTardisRotation(ITardisLevel tardis) {
        ServerLevel dimensionTo = ((ServerLevel) tardis.getLevel()).getServer().getLevel(tardis.getLocation().getLevel());

        Float rotation = null;
        Exterior exterior = tardis.getExterior();
        if (exterior instanceof TileExterior) {
            BlockEntity be = dimensionTo.getBlockEntity(tardis.getLocation().getPos());
            if (be instanceof ExteriorTile) {
                rotation = WorldHelper.getHorizontalFacing(be.getBlockState()).toYRot();
            }
        } else if (exterior instanceof EntityExterior entityExterior) {
            PublicEntityExterior publicEntityExterior = (PublicEntityExterior) entityExterior;
            Entity entity = publicEntityExterior.ntm_immersive_portals$getEntity();
            if (entity != null) {
                rotation = publicEntityExterior.ntm_immersive_portals$getEntity().getYRot();
            }
        }

        if (rotation == null)
            rotation = tardis.getLocation().getDirection().toYRot();

        return rotation;
    }
}
