package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mian.minecraft.ntm_ip.misc.Portals;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.tardis.mod.blockentities.InteriorDoorTile;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.Capabilities;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.helpers.WorldHelper;
import net.tardis.mod.misc.TeleportHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(InteriorDoorTile.class)
public class InteriorDoorTileMixin {
    @Unique
    private int ntm_immersive_portals$portal;

    @WrapOperation(remap=false, method = "lambda$tick$5", at = @At(target = "Lnet/tardis/mod/misc/TeleportHandler;tick(Lnet/minecraft/server/level/ServerLevel;)V", value = "INVOKE"))
    private static void ntm_immersive_portals$tick(TeleportHandler<InteriorDoorTile> instance,
                                                   ServerLevel tardisLevel,
                                                   Operation<Void> original,
                                                   @Local(argsOnly = true) LocalRef<InteriorDoorTile> door) {

        ITardisLevel tardis = tardisLevel.getCapability(Capabilities.TARDIS).orElse(null);
        BlockEntity entity = tardisLevel.getServer().getLevel(tardis.getLocation().getLevel()).getBlockEntity(tardis.getLocation().getPos());

        // only main interior door get portals (the reason being it's too laggy to do all of them)
        if (!(entity instanceof ExteriorTile) || Portals.getPortalsForTardis(tardis).isEmpty() || tardis == null || (tardis != null && (
                !tardis.getInteriorManager().getMainInteriorDoor().getPosition(tardisLevel)
                        .equals(WorldHelper.centerOfBlockPos(door.get().getBlockPos(), false))))) {
            original.call(instance, tardisLevel);
        }
    }
}
