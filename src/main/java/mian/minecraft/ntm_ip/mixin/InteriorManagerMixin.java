package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.Portalable;
import mian.minecraft.ntm_ip.api.PublicDoorData;
import mian.minecraft.ntm_ip.helper.PortalHelper;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.helpers.WorldHelper;
import net.tardis.mod.misc.DoorHandler;
import net.tardis.mod.misc.tardis.InteriorDoorData;
import net.tardis.mod.misc.tardis.InteriorManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

import javax.annotation.Nullable;

@Mixin(InteriorManager.class)
public abstract class InteriorManagerMixin implements Portalable {
    @Shadow @Final private ITardisLevel tardis;

    @Shadow public abstract DoorHandler getDoorHandler();

    @Unique
    private int ntm_immersive_portals$exterior;
    @Unique
    private int ntm_immersive_portals$interior;

    @Redirect(method = "getMainInteriorDoor", at = @At(value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;log(Lorg/apache/logging/log4j/Level;Ljava/lang/String;)V"))
    public void getMainInteriorDoor(Logger instance, Level level, String s){
        // shut the fuck up
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void ntm_immersive_portals$tick(CallbackInfo ci){
        BlockEntity entity = tardis.getLevel().getServer().getLevel(tardis.getLocation().getLevel()).getBlockEntity(tardis.getLocation().getPos());

        if(!tardis.isClient() && entity instanceof ExteriorTile exteriorTile){
            boolean shouldTeleport = getDoorHandler().getDoorState().isOpen();

            if (shouldTeleport) {
                Portal exterior = ntm_immersive_portals$createOrEditExterior(exteriorTile);
                ntm_immersive_portals$setExterior(exterior);

                Portal interior = ntm_immersive_portals$createOrEditInterior(tardis, exteriorTile);
                ntm_immersive_portals$setInterior(interior);

                PortalManipulation.adjustRotationToConnect(exterior, interior);
            } else {
                ntm_immersive_portals$removePortals();
            }
        }
    }

    @Unique
    public Portal ntm_immersive_portals$createOrEditExterior(ExteriorTile exteriorTile){
        ServerLevel level = (ServerLevel) exteriorTile.getLevel();
        Portal portal = ntm_immersive_portals$getExterior();

        Vec3 origin = exteriorTile.getBlockPos().getCenter();
        InteriorDoorData doorData = tardis.getInteriorManager().getMainInteriorDoor();
        if(tardis.getInteriorManager().getMainDoorID().isEmpty())
            tardis.getInteriorManager().setMainDoor(((PublicDoorData) doorData).ntm_immersive_portals$getUUID());
        Vec3 dest = tardis.getInteriorManager().getMainInteriorDoor().getPosition(tardis.getLevel());

        float y = WorldHelper.getHorizontalFacing(exteriorTile.getBlockState()).toYRot();

        Direction intDir = Direction.fromYRot(tardis.getInteriorManager().getMainInteriorDoor().getRotation(tardis));
        dest = dest.relative(intDir, -2);

        Direction extDir = WorldHelper.getHorizontalFacing(exteriorTile.getBlockState());
        origin = origin.relative(extDir, 0.75);


        if(portal != null && !level.equals(portal.getOriginWorld())) {
            PortalHelper.removePortal(portal.getOriginWorld(), portal.getId());
        }

        ResourceKey<net.minecraft.world.level.Level> targetDim = tardis.getId();
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

        return portal;
    }

    @Unique
    public Portal ntm_immersive_portals$createOrEditInterior(ITardisLevel tardis, ExteriorTile exteriorTile){
        ServerLevel level = (ServerLevel) tardis.getLevel();
        Portal portal = ntm_immersive_portals$getExterior();

        InteriorDoorData door = tardis.getInteriorManager().getMainInteriorDoor();
        Vec3 origin = door.getPosition(level);
        Vec3 dest = tardis.getLocation().getPos().getCenter();

        float y = door.getRotation(tardis);

        Direction extDir = WorldHelper.getHorizontalFacing(exteriorTile.getBlockState());
        dest = dest.relative(extDir, 0.75);

        if(portal != null && !level.equals(portal.getOriginWorld())) {
            PortalHelper.removePortal(portal.getOriginWorld(), portal.getId());
        }

        ResourceKey<net.minecraft.world.level.Level> targetDim = tardis.getId();
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

        return portal;
    }

    @Override
    public void ntm_immersive_portals$removePortals() {
        PortalHelper.removePortal(this.tardis.getLevel().getServer().getLevel(this.tardis.getLocation().getLevel()), ntm_immersive_portals$exterior);
        ntm_immersive_portals$exterior = 0;

        PortalHelper.removePortal(this.tardis.getLevel(), ntm_immersive_portals$interior);
        ntm_immersive_portals$interior = 0;
    }

    @Override
    public void ntm_immersive_portals$setExterior(@Nullable Portal start) {
        if(start == null){
            this.ntm_immersive_portals$exterior = 0;
        } else {
            this.ntm_immersive_portals$exterior = start.getId();
        }
    }
    @Override
    public void ntm_immersive_portals$setInterior(@Nullable Portal start) {
        if(start == null){
            this.ntm_immersive_portals$exterior = 0;
        } else {
            this.ntm_immersive_portals$exterior = start.getId();
        }
    }
    @Override
    public Portal ntm_immersive_portals$getExterior() {
        Entity portal = ((BlockEntity)(Object) this).getLevel().getEntity(ntm_immersive_portals$exterior);
        if(portal != null)
            return (Portal) portal;
        return null;
    }
    @Override
    public Portal ntm_immersive_portals$getInterior() {
        Entity portal = ((BlockEntity)(Object) this).getLevel().getEntity(ntm_immersive_portals$interior);
        if(portal != null)
            return (Portal) portal;
        return null;
    }
}
