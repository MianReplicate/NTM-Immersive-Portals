package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mian.minecraft.ntm_ip.NTMIP;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.q_misc_util.MiscNetworking;
import qouteall.q_misc_util.api.DimensionAPI;
import qouteall.q_misc_util.dimension.DimensionIdManagement;
import qouteall.q_misc_util.ducks.IEMinecraftServer_Misc;

import java.util.function.BiFunction;

@Mixin(DimensionHelper.class)
public class DimensionHelperMixin {
    @Inject(method = "getOrCreateWorld", at = @At(value = "RETURN", ordinal = 1))
    private static void addToImmersivePortalsDimAPI(MinecraftServer server, ResourceKey<Level> worldKey, BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory, CallbackInfoReturnable<ServerLevel> cir){
        NTMIP.LOGGER.info("A new TARDIS dimension was created. Adding it to Immersive Portal's world map: " + worldKey.location().getPath());

        ServerLevel newWorld = cir.getReturnValue();
        NTMIP.LOGGER.info(newWorld.toString());
        ((IEMinecraftServer_Misc)server).ip_addDimensionToWorldMap(worldKey, newWorld);
        DimensionIdManagement.updateAndSaveServerDimIdRecord();
        DimensionAPI.saveDimensionConfiguration(worldKey);

        Packet dimSyncPacket = MiscNetworking.createDimSyncPacket();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(dimSyncPacket);
        }
    }
}
