package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.Portalable;
import mian.minecraft.ntm_ip.api.PublicDoorData;
import mian.minecraft.ntm_ip.helper.PortalHelper;
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
public abstract class ExteriorTileMixin implements Portalable {
    @Shadow public abstract Optional<ITardisLevel> getTardis();

    @Shadow public abstract DoorHandler getDoorHandler();

    @Unique
    private int ntm_immersive_portals$portal;

    @WrapOperation(remap=false, method = "tick", at = @At(target = "Lnet/tardis/mod/misc/TeleportHandler;tick(Lnet/minecraft/server/level/ServerLevel;)V", value = "INVOKE"))
    private static void ntm_immersive_portals$tick(TeleportHandler<InteriorDoorTile> instance,
                                                   ServerLevel level,
                                                   Operation<Void> original,
                                                   @Local(argsOnly = true) LocalRef<ExteriorTile> exteriorTileRef){
        ITardisLevel tardis = exteriorTileRef.get().getTardis().orElse(null);
        if(tardis == null){
            original.call(instance, level);
        } else {
            Portalable portalable = ((Portalable) exteriorTileRef.get());
            Portal portal = portalable.ntm_immersive_portals$getPortal();
            boolean shouldTeleport = exteriorTileRef.get().getDoorHandler().getDoorState().isOpen();

            if (shouldTeleport) {
                Vec3 origin = exteriorTileRef.get().getBlockPos().getCenter();
                InteriorDoorData doorData = tardis.getInteriorManager().getMainInteriorDoor();
                if(tardis.getInteriorManager().getMainDoorID().isEmpty())
                    tardis.getInteriorManager().setMainDoor(((PublicDoorData) doorData).ntm_immersive_portals$getUUID());
                Vec3 dest = tardis.getInteriorManager().getMainInteriorDoor().getPosition(tardis.getLevel());

                float y = WorldHelper.getHorizontalFacing(exteriorTileRef.get().getBlockState()).toYRot();

                Direction intDir = Direction.fromYRot(tardis.getInteriorManager().getMainInteriorDoor().getRotation(tardis));
                dest = dest.relative(intDir, -2);

                Direction extDir = tardis.getLocation().getDirection();
                origin = origin.relative(extDir, 0.5);

                ResourceKey<Level> targetDim = tardis.getId();
                if(portal == null || !portal.isAlive()){
                    portal = PortalHelper.createPortal(
                            level,
                            origin,
                            dest, // maybe work??,
                            targetDim,
                            DQuaternion.rotationByDegrees(new Vec3(0, -1, 0), y),
                            1,
                            2
                    );
                    portalable.ntm_immersive_portals$setPortal(portal);
                    portal.setInteractable(false);
                    portal.animation.defaultAnimation.durationTicks = 0;
                    portal.setIsVisible(true);
                    portal.teleportable = true;

                    McHelper.spawnServerEntity(portal);

                    NTMIP.LOGGER.info("Created portal!");
                    NTMIP.LOGGER.info("Origin:"+ origin.toString());
                    NTMIP.LOGGER.info("Dest:"+ dest.toString());
                    NTMIP.LOGGER.info("Origin Dim:"+ level.toString());
                    NTMIP.LOGGER.info("Dest Dim:"+ targetDim.toString());
                }

                if(!level.equals(portal.getOriginWorld())){
                    portalable.ntm_immersive_portals$removePortal();
                } else {
                    if(!origin.equals(portal.getOriginPos())
                            || !dest.equals(portal.getDestPos())
                            || !targetDim.equals(portal.getDestDim())){
                        NTMIP.LOGGER.info("Origin:"+ origin.toString());
                        NTMIP.LOGGER.info("Dest:"+ dest.toString());
                        NTMIP.LOGGER.info("Origin Dim:"+ level.toString());
                        NTMIP.LOGGER.info("Dest Dim:"+ targetDim.toString());

                        portal.setOriginPos(origin);
                        portal.setPos(dest);
                        portal.setDestinationDimension(tardis.getLocation().getLevel());
                        portal.reloadAndSyncToClient();
                    }
                }
            } else {
                portalable.ntm_immersive_portals$removePortal();
            }
        }
    }

    @Override
    public void ntm_immersive_portals$removePortal() {
        PortalHelper.removePortal(((BlockEntity)(Object) this).getLevel(), ntm_immersive_portals$portal);
        ntm_immersive_portals$portal = 0;
    }

    @Override
    public void ntm_immersive_portals$setPortal(Portal start) {
        this.ntm_immersive_portals$portal = start.getId();
    }
    @Override
    public Portal ntm_immersive_portals$getPortal() {
        Entity portal = ((BlockEntity)(Object) this).getLevel().getEntity(ntm_immersive_portals$portal);
        if(portal != null)
            return (Portal) portal;
        return null;
    }
}
