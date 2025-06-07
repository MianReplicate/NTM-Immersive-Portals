package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.q_misc_util.MiscNetworking;
import qouteall.q_misc_util.dimension.DimensionIdRecord;

@Mixin(MiscNetworking.class)
public class MiscNetworkingMixin {

    // this is kinda hacky but i can't really find another way for immersive portals to stop crashing
    @Inject(method = "lambda$processDimSync$1", at = @At(value = "HEAD"))
    private static void processDimSync(CompoundTag typeMap, ClientGamePacketListener packetListener, CallbackInfo ci, @Local(argsOnly = true) CompoundTag idMap){
        DimensionIdRecord.clientRecord = DimensionIdRecord.tagToRecord(idMap);
    }
}
