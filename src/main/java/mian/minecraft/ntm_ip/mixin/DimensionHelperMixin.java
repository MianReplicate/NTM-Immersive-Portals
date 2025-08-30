package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.helper.NTMIPHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.tardis.mod.dimension.DimensionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiFunction;

@Mixin(DimensionHelper.class)
public class DimensionHelperMixin {
    @Inject(method = "getOrCreateWorld", at = @At(value = "RETURN", ordinal = 1), remap = false)
    private static void addToImmersivePortalsDimAPI(MinecraftServer server, ResourceKey<Level> worldKey, BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory, CallbackInfoReturnable<ServerLevel> cir) {
        NTMIP.LOGGER.info("A new TARDIS dimension was created. Adding it to Immersive Portal's world map: " + worldKey.location().getPath());

        NTMIPHelper.updateKnownDimensions(server, worldKey);
    }
}
