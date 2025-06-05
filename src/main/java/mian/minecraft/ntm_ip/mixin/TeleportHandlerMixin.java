package mian.minecraft.ntm_ip.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.Portalable;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.blockentities.InteriorDoorTile;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.helpers.WorldHelper;
import net.tardis.mod.misc.IDoor;
import net.tardis.mod.misc.TeleportEntry;
import net.tardis.mod.misc.TeleportHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.platform_specific.IPRegistry;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(TeleportHandler.class)
public class TeleportHandlerMixin<T> implements Portalable {
    @Shadow
    Supplier<Boolean> shouldTeleport;
    @Shadow @Final private T parent;
    @Shadow @Final private Supplier<AABB> teleportBounds;
    @Shadow @Final private BiFunction<ServerLevel, Entity, TeleportEntry.LocationData> positionSupplier;
    @Shadow @Final private Function<ServerLevel, ServerLevel> targetDimSupplier;
    @Unique
    private Portal ntm_immersive_portals$portal;

    @Inject(remap=false, cancellable = true, method = "tick", at = @At(target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;", value = "INVOKE"))
    public void ntm_immersive_portals$tick(ServerLevel level, CallbackInfo ci){
        if(parent instanceof IDoor door){
            ServerLevel target = this.targetDimSupplier.apply(level);
            if (this.shouldTeleport.get() && target != null) {
                Vec3 origin = this.teleportBounds.get().getCenter();
                Vec3 dest = this.positionSupplier.apply(target, null).position();

                float y = 0;
                if(door instanceof ExteriorTile tile){
                     y = tile.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
                } else if(door instanceof InteriorDoorTile tile){
                    y = tile.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite().toYRot();
                }
//                if(door instanceof ExteriorTile tile)
//                    dest = WorldHelper.centerOfBlockPos(doorTile.getBlockPos().relative(WorldHelper.getHorizontalFacing(doorTile.getBlockState())))
                if(ntm_immersive_portals$portal == null || !ntm_immersive_portals$portal.isAlive()){
                    ntm_immersive_portals$portal = ntm_immersive_portals$createPortal(
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
            ci.cancel();
        }
    }

    @Unique
    private static Portal ntm_immersive_portals$createPortal(Level level,
                                                             Vec3 origin,
                                                             Vec3 destination,
                                                             ResourceKey<Level> destinationLvl,
                                                             DQuaternion quat,
                                                             double width,
                                                             double height
    ) {
        Portal portal = IPRegistry.PORTAL.get().create(level);

        portal.setOriginPos(origin);
        portal.setDestinationDimension(destinationLvl);
        portal.setDestination(destination);
        portal.setOrientationAndSize(
                new Vec3(1, 0, 0), // axisW
                new Vec3(0, 1, 0), // axisH
                width, // width
                height // height
        );
        PortalManipulation.rotatePortalBody(portal, DQuaternion.fromMcQuaternion(quat.toMcQuaternion()));

        return portal;
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
