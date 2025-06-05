package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.Portalable;
import mian.minecraft.ntm_ip.helper.PortalHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.blockentities.InteriorDoorTile;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.Capabilities;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.helpers.WorldHelper;
import net.tardis.mod.misc.TeleportHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.platform_specific.IPRegistry;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

@Mixin(InteriorDoorTile.class)
public class InteriorDoorTileMixin implements Portalable {
    @Unique
    private Portal ntm_immersive_portals$portal;

    @WrapOperation(remap=false, method = "lambda$tick$5", at = @At(target = "Lnet/tardis/mod/misc/TeleportHandler;tick(Lnet/minecraft/server/level/ServerLevel;)V", value = "INVOKE"))
    private static void ntm_immersive_portals$tick(TeleportHandler<InteriorDoorTile> instance,
                                                   ServerLevel tardisLevel,
                                                   Operation<Void> original,
                                                   @Local(argsOnly = true) LocalRef<InteriorDoorTile> door){

        Portalable portalable = ((Portalable) door.get());
        Portal portal = portalable.ntm_immersive_portals$getPortal();

        if (this.shouldTeleport.get() && target != null) {
            Vec3 origin = this.teleportBounds.get().getCenter();
            Vec3 dest = this.positionSupplier.apply(target, null).position();

            float y = 0;

            ITardisLevel tardis = level.getCapability(Capabilities.TARDIS).orElse(null);
            if(tardis == null)
                return;

            y = WorldHelper.getFacingAngle(tile.getBlockState());

            Direction extDir = tardis.getLocation().getDirection();
            dest = dest.relative(extDir, 0.5);
//                if(door instanceof ExteriorTile tile)
//                    dest = WorldHelper.centerOfBlockPos(doorTile.getBlockPos().relative(WorldHelper.getHorizontalFacing(doorTile.getBlockState())))
            if(ntm_immersive_portals$portal == null || !ntm_immersive_portals$portal.isAlive()){
                ntm_immersive_portals$portal = PortalHelper.createPortal(
                        level,
                        origin,
                        dest, // maybe work??,
                        target.dimension(),
                        DQuaternion.rotationByDegrees(new Vec3(0, -1, 0), y),
                        1,
                        2
                );
                ntm_immersive_portals$portal.setInteractable(false);
                ntm_immersive_portals$portal.animation.defaultAnimation.durationTicks = 0;
                ntm_immersive_portals$portal.setIsVisible(true);
                ntm_immersive_portals$portal.teleportable = true;

                McHelper.spawnServerEntity(ntm_immersive_portals$portal);

                NTMIP.LOGGER.info("Created portal!");
                NTMIP.LOGGER.info("Origin:"+ origin.toString());
                NTMIP.LOGGER.info("Dest:"+ dest.toString());
                NTMIP.LOGGER.info("Origin Dim:"+ level.toString());
                NTMIP.LOGGER.info("Dest Dim:"+ target.toString());
            }

            if(!level.equals(ntm_immersive_portals$portal.getOriginWorld())){
                ntm_immersive_portals$removePortal();
            } else {
                if(!origin.equals(ntm_immersive_portals$portal.getOriginPos())
                        || !dest.equals(ntm_immersive_portals$portal.getDestPos())
                        || !target.equals(ntm_immersive_portals$portal.getDestWorld())){
                    NTMIP.LOGGER.info("Origin:"+ origin.toString());
                    NTMIP.LOGGER.info("Dest:"+ dest.toString());
                    NTMIP.LOGGER.info("Origin Dim:"+ level.toString());
                    NTMIP.LOGGER.info("Dest Dim:"+ target.toString());

                    ntm_immersive_portals$portal.setOriginPos(origin);
                    ntm_immersive_portals$portal.setPos(dest);
                    ntm_immersive_portals$portal.setDestinationDimension(target.dimension());
                    ntm_immersive_portals$portal.reloadAndSyncToClient();
                }
            }
        } else {
            ntm_immersive_portals$removePortal();
        }
    }

    @Override
    public void ntm_immersive_portals$removePortal() {
        if(ntm_immersive_portals$portal != null && ntm_immersive_portals$portal.isAlive())
            ntm_immersive_portals$portal.kill();
        ntm_immersive_portals$portal = null;
    }

    @Override
    public void ntm_immersive_portals$setPortal(Portal start) {
        this.ntm_immersive_portals$portal = start;
    }
    @Override
    public Portal ntm_immersive_portals$getPortal() {
        return ntm_immersive_portals$portal;
    }
}
