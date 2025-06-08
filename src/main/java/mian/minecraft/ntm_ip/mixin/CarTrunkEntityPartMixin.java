package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mian.minecraft.ntm_ip.misc.NTMIPPortals;
import net.minecraft.server.level.ServerLevel;
import net.tardis.mod.blockentities.InteriorDoorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.entity.CarTrunkEntityPart;
import net.tardis.mod.misc.TeleportHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CarTrunkEntityPart.class)
public class CarTrunkEntityPartMixin {
    @WrapOperation(remap = false, method = "tick", at = @At(target = "Lnet/tardis/mod/misc/TeleportHandler;tick(Lnet/minecraft/server/level/ServerLevel;)V", value = "INVOKE"))
    private void ntm_immersive_portals$tick(TeleportHandler<InteriorDoorTile> instance,
                                            ServerLevel level,
                                            Operation<Void> original) {
        ITardisLevel tardis = ((CarTrunkEntityPart<?>) (Object) this).getParent()
                .cachedTardis;
        if (tardis == null || NTMIPPortals.getPortalsForTardis(tardis).isEmpty())
            original.call(instance, level);
    }
}
