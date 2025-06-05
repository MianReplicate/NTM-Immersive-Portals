package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
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

@Mixin(InteriorDoorTile.class)
public class InteriorDoorTileMixin {
    @Unique
    private int ntm_immersive_portals$portal;

    @WrapOperation(remap=false, method = "lambda$tick$5", at = @At(target = "Lnet/tardis/mod/misc/TeleportHandler;tick(Lnet/minecraft/server/level/ServerLevel;)V", value = "INVOKE"))
    private static void ntm_immersive_portals$tick(TeleportHandler<InteriorDoorTile> instance,
                                                   ServerLevel tardisLevel,
                                                   Operation<Void> original,
                                                   @Local(argsOnly = true) LocalRef<InteriorDoorTile> door){

        ITardisLevel tardis = tardisLevel.getCapability(Capabilities.TARDIS).orElse(null);
        BlockEntity entity = tardisLevel.getServer().getLevel(tardis.getLocation().getLevel()).getBlockEntity(tardis.getLocation().getPos());

        // only main interior door get portals (the reason being it's too laggy to do all of them)
        if(!(entity instanceof ExteriorTile exteriorTile) || tardis == null || (tardis != null && (
                !tardis.getInteriorManager().getMainInteriorDoor().getPosition(tardisLevel)
                        .equals(WorldHelper.centerOfBlockPos(door.get().getBlockPos(), false))))){
            original.call(instance, tardisLevel);
        } else {
//            Portalable portalable = ((Portalable) door.get());
//            Portal portal = portalable.ntm_immersive_portals$getPortal();
//            boolean shouldTeleport = door.get().getDoorHandler().getDoorState().isOpen();
//
//            if (shouldTeleport) {
//                Vec3 origin = door.get().getBlockPos().getCenter();
//                Vec3 dest = tardis.getLocation().getPos().getCenter();
//
//                float y = WorldHelper.getHorizontalFacing(door.get().getBlockState()).toYRot();
//
//                Direction extDir = WorldHelper.getHorizontalFacing(exteriorTile.getBlockState());
//                dest = dest.relative(extDir, 0.75);
//
//                ResourceKey<Level> targetDim = tardis.getLocation().getLevel();
//                if(portal == null || !portal.isAlive()){
//                    portal = PortalHelper.createPortal(
//                            tardisLevel,
//                            origin,
//                            dest, // maybe work??,
//                            targetDim,
//                            DQuaternion.rotationByDegrees(new Vec3(0, -1, 0), y),
//                            1,
//                            2
//                    );
//                    portalable.ntm_immersive_portals$setPortal(portal);
//                    portal.setInteractable(false);
//                    portal.animation.defaultAnimation.durationTicks = 0;
//                    portal.setIsVisible(true);
//                    portal.teleportable = true;
//
//                    McHelper.spawnServerEntity(portal);
//
//                    NTMIP.LOGGER.info("Created portal!");
//                    NTMIP.LOGGER.info("Origin:"+ origin.toString());
//                    NTMIP.LOGGER.info("Dest:"+ dest.toString());
//                    NTMIP.LOGGER.info("Origin Dim:"+ tardisLevel.toString());
//                    NTMIP.LOGGER.info("Dest Dim:"+ targetDim.toString());
//                }
//
//                if(!tardisLevel.equals(portal.getOriginWorld())){
//                    portalable.ntm_immersive_portals$removePortal();
//                } else {
//                    if(!origin.equals(portal.getOriginPos())
//                            || !dest.equals(portal.getDestPos())
//                            || !targetDim.equals(portal.getDestDim())){
//                        NTMIP.LOGGER.info("Origin:"+ origin.toString());
//                        NTMIP.LOGGER.info("Dest:"+ dest.toString());
//                        NTMIP.LOGGER.info("Origin Dim:"+ tardisLevel.toString());
//                        NTMIP.LOGGER.info("Dest Dim:"+ targetDim.toString());
//
//                        portal.setOriginPos(origin);
//                        portal.setPos(dest);
//                        portal.setDestinationDimension(tardis.getLocation().getLevel());
//                        portal.reloadAndSyncToClient();
//                    }
//                }
//            } else {
//                portalable.ntm_immersive_portals$removePortal();
//            }
        }
    }

//    @Override
//    public void ntm_immersive_portals$removePortal() {
//        PortalHelper.removePortal(((BlockEntity)(Object) this).getLevel(), ntm_immersive_portals$portal);
//        ntm_immersive_portals$portal = 0;
//    }
//
//    @Override
//    public void ntm_immersive_portals$setPortal(Portal start) {
//        this.ntm_immersive_portals$portal = start.getId();
//    }
//    @Override
//    public Portal ntm_immersive_portals$getPortal() {
//        Entity portal = ((BlockEntity)(Object) this).getLevel().getEntity(ntm_immersive_portals$portal);
//        if(portal != null)
//            return (Portal) portal;
//        return null;
//    }
}
