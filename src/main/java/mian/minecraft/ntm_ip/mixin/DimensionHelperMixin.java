package mian.minecraft.ntm_ip.mixin;

import io.netty.buffer.Unpooled;
import mian.minecraft.ntm_ip.NTMIP;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import net.tardis.mod.dimension.DimensionHelper;
import net.tardis.mod.network.Network;
import net.tardis.mod.network.packets.SyncDimensionListMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.q_misc_util.MiscHelper;
import qouteall.q_misc_util.MiscNetworking;
import qouteall.q_misc_util.api.DimensionAPI;
import qouteall.q_misc_util.dimension.DimensionIdManagement;
import qouteall.q_misc_util.dimension.DimensionIdRecord;
import qouteall.q_misc_util.dimension.DimensionTypeSync;
import qouteall.q_misc_util.forge.events.ServerDimensionDynamicUpdateEvent;

import java.util.function.BiFunction;

@Mixin(DimensionHelper.class)
public class DimensionHelperMixin {
    @Inject(method = "getOrCreateWorld", at = @At(value = "RETURN", ordinal = 1))
    private static void addToImmersivePortalsDimAPI(MinecraftServer server, ResourceKey<Level> worldKey, BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory, CallbackInfoReturnable<ServerLevel> cir){
        NTMIP.LOGGER.info("A new TARDIS dimension was created. Adding it to Immersive Portal's world map: " + worldKey.location().getPath());

        ServerLevel newWorld = cir.getReturnValue();
        NTMIP.LOGGER.info(newWorld.toString());
//        ((IEMinecraftServer_Misc)server).ip_addDimensionToWorldMap(worldKey, newWorld);

        DimensionIdManagement.updateAndSaveServerDimIdRecord();
        DimensionAPI.saveDimensionConfiguration(worldKey);

        Packet dimSyncPacket = MiscNetworking.createDimSyncPacket();

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        CompoundTag idMapTag = DimensionIdRecord.recordToTag(DimensionIdRecord.serverRecord, (dim) ->
                MiscHelper.getServer().getLevel(dim) != null || dim.equals(worldKey));
        buf.writeNbt(idMapTag);
        CompoundTag typeMapTag = DimensionTypeSync.createTagFromServerWorldInfo();
        buf.writeNbt(typeMapTag);

//        Network.sendPacketToAll(dimSyncPacket);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(dimSyncPacket);
            Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncDimensionListMessage(worldKey, true));
        }
        MinecraftForge.EVENT_BUS.post(new ServerDimensionDynamicUpdateEvent(server.levelKeys()));
    }
//    @Redirect(method = "getOrCreateWorld", at = @At(value = "INVOKE", target = "Lnet/tardis/mod/dimension/DimensionHelper;createAndRegisterDynamicWorldAndDimension(Lnet/minecraft/server/MinecraftServer;Ljava/util/Map;Lnet/minecraft/resources/ResourceKey;Ljava/util/function/BiFunction;)Lnet/minecraft/server/level/ServerLevel;"))
//    private static ServerLevel addToImmersivePortalsDimAPI(MinecraftServer server,
//                                                           Map<ResourceKey<Level>, ServerLevel> map,
//                                                           ResourceKey<Level> worldKey,
//                                                           BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory){
//        NTMIP.LOGGER.info("A new TARDIS dimension was created. Using DimensionAPI to create it: " + worldKey.location().getPath());
//        ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, worldKey.location());
//
//        DimensionAPI.addDimensionDynamically(worldKey.location(), dimensionFactory.apply(server, dimensionKey));
//        return server.getLevel(worldKey);
//    }
}
