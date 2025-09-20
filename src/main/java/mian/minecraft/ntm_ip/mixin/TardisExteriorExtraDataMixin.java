package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.api.ExteriorDataHandlerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.network.packets.tardis.TardisExteriorExtraData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TardisExteriorExtraData.class)
public class TardisExteriorExtraDataMixin {
    @Unique
    private boolean ntm_immersive_portals$isBOTIEnabled;

    @Inject(remap = false, at = @At("TAIL"), method = "serialize")
    public void serialize(FriendlyByteBuf buf, CallbackInfo ci){
        buf.writeBoolean(ntm_immersive_portals$isBOTIEnabled);
    }

    @Inject(remap = false, at = @At("TAIL"), method = "deserialize")
    public void deserialize(FriendlyByteBuf buf, CallbackInfo ci){
        ntm_immersive_portals$isBOTIEnabled = buf.readBoolean();
    }

    @Inject(remap = false, at = @At("TAIL"), method = "apply")
    public void apply(ITardisLevel tardis, CallbackInfo ci){
        ((ExteriorDataHandlerImpl)tardis.getExteriorExtraData()).ntm_immersive_portals$setBOTIEnabled(ntm_immersive_portals$isBOTIEnabled);
    }

    @Inject(remap = false, at = @At("TAIL"), method = "createFromTardis")
    public void createFromTardis(ITardisLevel tardis, CallbackInfo ci){
        this.ntm_immersive_portals$isBOTIEnabled = ((ExteriorDataHandlerImpl) tardis.getExteriorExtraData()).ntm_immersive_portals$isBOTIEnabled();
    }
}
