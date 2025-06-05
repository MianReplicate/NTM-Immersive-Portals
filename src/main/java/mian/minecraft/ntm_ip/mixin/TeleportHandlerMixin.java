package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.Portalable;
import net.minecraft.server.level.ServerLevel;
import net.tardis.mod.misc.TeleportHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.portal.Portal;

@Mixin(TeleportHandler.class)
public abstract class TeleportHandlerMixin implements Portalable {
    @Unique
    private Portal ntm_immersive_portals$start;
    @Unique
    private Portal ntm_immersive_portals$destination;

    @Inject(remap=false, cancellable = true, method = "tick", at = @At(ordinal = 0, target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;", value = "INVOKE"))
    public void tick(ServerLevel level, CallbackInfo ci){
        NTMIP.LOGGER.info("Hey im here ticking :O");
        ci.cancel();
    }

    @Override
    public void ntm_immersive_portals$setStart(Portal start) {
        this.ntm_immersive_portals$start = start;
    }

    @Override
    public void ntm_immersive_portals$setDestination(Portal destination) {
        this.ntm_immersive_portals$destination = destination;
    }

    @Override
    public Portal ntm_immersive_portals$getStart() {
        return ntm_immersive_portals$start;
    }

    @Override
    public Portal ntm_immersive_portals$getDestination() {
        return ntm_immersive_portals$destination;
    }
}
