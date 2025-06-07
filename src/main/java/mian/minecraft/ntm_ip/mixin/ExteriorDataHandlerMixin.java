package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.api.ExteriorDataHandlerImpl;
import net.minecraft.nbt.CompoundTag;
import net.tardis.mod.misc.tardis.ExteriorDataHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExteriorDataHandler.class)
public class ExteriorDataHandlerMixin implements ExteriorDataHandlerImpl {
    @Unique
    private boolean ntm_immersive_portals$botiEnabled = true;

    @Inject(method = "serializeNBT()Lnet/minecraft/nbt/CompoundTag;", at = @At(value = "RETURN"))
    private void ntm_immersive_portals$serializeNBT(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        tag.putBoolean("boti_enabled", ntm_immersive_portals$isBOTIEnabled());
    }

    @Inject(method = "deserializeNBT(Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "TAIL"))
    private void ntm_immersive_portals$deserializeNBT(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("boti_enabled"))
            ntm_immersive_portals$setBOTIEnabled(tag.getBoolean("boti_enabled"));
    }

    @Override
    public boolean ntm_immersive_portals$isBOTIEnabled() {
        return ntm_immersive_portals$botiEnabled;
    }

    @Override
    public void ntm_immersive_portals$setBOTIEnabled(boolean enabled) {
        ntm_immersive_portals$botiEnabled = enabled;
    }
}
