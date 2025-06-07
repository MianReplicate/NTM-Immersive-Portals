package mian.minecraft.ntm_ip.mixin;

import io.netty.buffer.Unpooled;
import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.helper.Helper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
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
import qouteall.imm_ptl.core.McHelper;
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

        Helper.updateKnownDimensions(server, worldKey);
    }
}
