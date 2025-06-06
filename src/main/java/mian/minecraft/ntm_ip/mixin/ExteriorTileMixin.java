package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.Portalable;
import mian.minecraft.ntm_ip.api.PublicDoorData;
import mian.minecraft.ntm_ip.helper.PortalHelper;
import mian.minecraft.ntm_ip.misc.Portals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.blockentities.InteriorDoorTile;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.exterior.ExteriorType;
import net.tardis.mod.helpers.WorldHelper;
import net.tardis.mod.misc.DoorHandler;
import net.tardis.mod.misc.TeleportHandler;
import net.tardis.mod.misc.tardis.InteriorDoorData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.my_util.DQuaternion;

import java.util.Optional;
import java.util.UUID;

@Mixin(ExteriorTile.class)
public abstract class ExteriorTileMixin {
    @Shadow public abstract Optional<ITardisLevel> getTardis();

    @Shadow public abstract DoorHandler getDoorHandler();

    @Shadow private ExteriorType exteriorType;
    @Unique
    private int ntm_immersive_portals$portal;

    @WrapOperation(remap=false, method = "tick", at = @At(target = "Lnet/tardis/mod/misc/TeleportHandler;tick(Lnet/minecraft/server/level/ServerLevel;)V", value = "INVOKE"))
    private static void ntm_immersive_portals$tick(TeleportHandler<InteriorDoorTile> instance,
                                                   ServerLevel level,
                                                   Operation<Void> original,
                                                   @Local(argsOnly = true) LocalRef<ExteriorTile> exteriorTileRef) {
        ITardisLevel tardis = exteriorTileRef.get().getTardis().orElse(null);
        if (tardis == null || Portals.getPortalsForTardis(
                UUID.fromString(tardis.getLevel().dimension().location().getPath())).isEmpty())
            original.call(instance, level);
    }
}
