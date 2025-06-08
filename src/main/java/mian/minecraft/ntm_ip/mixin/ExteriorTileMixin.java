package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mian.minecraft.ntm_ip.misc.NTMIPPortals;
import net.minecraft.server.level.ServerLevel;
import net.tardis.mod.blockentities.InteriorDoorTile;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.misc.TeleportHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ExteriorTile.class)
public abstract class ExteriorTileMixin {
    @Shadow
    public abstract Optional<ITardisLevel> getTardis();

    @WrapOperation(remap = false, method = "tick", at = @At(target = "Lnet/tardis/mod/misc/TeleportHandler;tick(Lnet/minecraft/server/level/ServerLevel;)V", value = "INVOKE"))
    private static void ntm_immersive_portals$tick(TeleportHandler<InteriorDoorTile> instance,
                                                   ServerLevel level,
                                                   Operation<Void> original,
                                                   @Local(argsOnly = true) LocalRef<ExteriorTile> exteriorTileRef) {
        ITardisLevel tardis = exteriorTileRef.get().getTardis().orElse(null);
        if (tardis == null || NTMIPPortals.getPortalsForTardis(tardis).isEmpty())
            original.call(instance, level);
    }
}
