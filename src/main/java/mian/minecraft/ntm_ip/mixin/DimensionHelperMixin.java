package mian.minecraft.ntm_ip.mixin;

import commoble.infiniverse.api.InfiniverseAPI;
import mian.minecraft.ntm_ip.NTMIP;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.tardis.mod.dimension.DimensionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.q_misc_util.MiscNetworking;
import qouteall.q_misc_util.api.DimensionAPI;
import qouteall.q_misc_util.dimension.DimensionIdManagement;
import qouteall.q_misc_util.ducks.IEMinecraftServer_Misc;

import java.util.Map;
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

//        Packet dimSyncPacket = MiscNetworking.createDimSyncPacket();
//
//        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
//            player.connection.send(dimSyncPacket);
//        }
    }
//    @Redirect(method = "getOrCreateWorld", at = @At(value = "INVOKE", target = "Lnet/tardis/mod/dimension/DimensionHelper;createAndRegisterDynamicWorldAndDimension(Lnet/minecraft/server/MinecraftServer;Ljava/util/Map;Lnet/minecraft/resources/ResourceKey;Ljava/util/function/BiFunction;)Lnet/minecraft/server/level/ServerLevel;"))
//    private static ServerLevel addToImmersivePortalsDimAPI(MinecraftServer server,
//                                                           Map<ResourceKey<Level>, ServerLevel> map,
//                                                           ResourceKey<Level> worldKey,
//                                                           BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory){
//        NTMIP.LOGGER.info("A new TARDIS dimension was created. Adding it to Immersive Portal's world map: " + worldKey.location().getPath());
//        ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, worldKey.location());
//        ServerLevel newWorld = InfiniverseAPI.get().getOrCreateLevel(server, worldKey, () -> dimensionFactory.apply(server, dimensionKey));
//        NTMIP.LOGGER.info(newWorld.toString());
//        ((IEMinecraftServer_Misc)server).ip_addDimensionToWorldMap(worldKey, newWorld);
//        DimensionIdManagement.updateAndSaveServerDimIdRecord();
//        DimensionAPI.saveDimensionConfiguration(worldKey);
//
//        Packet dimSyncPacket = MiscNetworking.createDimSyncPacket();
//
//        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
//            player.connection.send(dimSyncPacket);
//        }
//        return newWorld;
//    }
}
