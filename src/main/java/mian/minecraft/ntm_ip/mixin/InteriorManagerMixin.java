package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.PortalImpl;
import mian.minecraft.ntm_ip.api.Portalable;
import mian.minecraft.ntm_ip.api.PublicDoorData;
import mian.minecraft.ntm_ip.helper.PortalHelper;
import mian.minecraft.ntm_ip.misc.Portals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.helpers.WorldHelper;
import net.tardis.mod.misc.DoorHandler;
import net.tardis.mod.misc.tardis.InteriorDoorData;
import net.tardis.mod.misc.tardis.InteriorManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(InteriorManager.class)
public abstract class InteriorManagerMixin implements Portalable {
    @Shadow @Final private ITardisLevel tardis;

    @Shadow public abstract DoorHandler getDoorHandler();

    @Unique
    private UUID ntm_immersive_portals$exterior;
    @Unique
    private UUID ntm_immersive_portals$interior;

    @Redirect(method = "getMainInteriorDoor", at = @At(value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;log(Lorg/apache/logging/log4j/Level;Ljava/lang/String;)V"))
    public void getMainInteriorDoor(Logger instance, org.apache.logging.log4j.Level level, String s){
        // shut the fuck up
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void ntm_immersive_portals$tick(CallbackInfo ci){
        if(!tardis.isClient()){
            BlockEntity entity = ((ServerLevel)tardis.getLevel()).getServer().getLevel(tardis.getLocation().getLevel()).getBlockEntity(tardis.getLocation().getPos());

            if(entity instanceof ExteriorTile exteriorTile){
                boolean shouldTeleport = getDoorHandler().getDoorState().isOpen();

                if (shouldTeleport) {
                    UUID tardisID = UUID.fromString(tardis.getLevel().dimension().location().getPath());
                    List<Portal> portals = Portals.tardisToPortals.get(tardisID);
                    if(portals == null){
                        portals = new ArrayList<>();
                    }
                    Portals.tardisToPortals.putIfAbsent(tardisID, portals);

                    Portal exterior = ntm_immersive_portals$createOrEditExterior(exteriorTile, tardisID, portals);
                    ntm_immersive_portals$setExterior(exterior);

                    Portal interior = ntm_immersive_portals$createOrEditInterior(tardis, exteriorTile, tardisID, portals);
                    ntm_immersive_portals$setInterior(interior);

                    PortalHelper.adjustPortalsToConnectAndSync(interior, exterior);
                } else {
                    ntm_immersive_portals$removePortals();
                }
            }
        }
    }

    @Unique
    public Portal ntm_immersive_portals$createOrEditExterior(ExteriorTile exteriorTile, UUID tardisID, List<Portal> portals){
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


        if(portal != null && !level.dimension().equals(portal.getOriginDim())) {
            NTMIP.LOGGER.info("Killed exterior because levels aren't equal!\nExpected: "+level+"\nGot:"+portal.getOriginWorld());
            PortalHelper.removePortal((ServerLevel) portal.getOriginWorld(), portal.getUUID());
        }

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
            portal.setInteractable(false);
            portal.animation.defaultAnimation.durationTicks = 0;
            portal.setIsVisible(true);
            portal.teleportable = true;

            portals.add(portal);
            ((PortalImpl) portal).ntm_immersive_portals$setTardisID(tardisID);
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
    public Portal ntm_immersive_portals$createOrEditInterior(ITardisLevel tardis, ExteriorTile exteriorTile, UUID tardisID, List<Portal> portals){
        ServerLevel level = (ServerLevel) tardis.getLevel();
        Portal portal = ntm_immersive_portals$getInterior();

        InteriorDoorData door = tardis.getInteriorManager().getMainInteriorDoor();
        Vec3 origin = door.getPosition(level);
        Vec3 dest = tardis.getLocation().getPos().getCenter();

        float y = WorldHelper.getHorizontalFacing(level.getBlockState(BlockPos.containing(door.getPosition(level))))
                .toYRot();

        Direction extDir = WorldHelper.getHorizontalFacing(exteriorTile.getBlockState());
        dest = dest.relative(extDir, 0.75);

        if(portal != null && !level.dimension().equals(portal.getOriginDim())) {
            NTMIP.LOGGER.info("Killed interior because levels aren't equal!\nExpected: "+level+"\nGot:"+portal.getOriginWorld());
            PortalHelper.removePortal((ServerLevel) portal.getOriginWorld(), portal.getUUID());
        }

        ResourceKey<Level> targetDim = tardis.getLocation().getLevel();
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

            portals.add(portal);
            ((PortalImpl) portal).ntm_immersive_portals$setTardisID(tardisID);
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
        PortalHelper.removePortal((ServerLevel) ntm_immersive_portals$getExteriorLevel(tardis), ntm_immersive_portals$exterior);
        ntm_immersive_portals$exterior = null;

        PortalHelper.removePortal((ServerLevel) ntm_immersive_portals$getInteriorLevel(tardis), ntm_immersive_portals$interior);
        ntm_immersive_portals$interior = null;
    }

    @Override
    public void ntm_immersive_portals$setExterior(@Nullable Portal start) {
        if(start == null){
            this.ntm_immersive_portals$exterior = null;
        } else {
            this.ntm_immersive_portals$exterior = start.getUUID();
        }
    }
    @Override
    public void ntm_immersive_portals$setInterior(@Nullable Portal start) {
        if(start == null){
            this.ntm_immersive_portals$interior = null;
        } else {
            this.ntm_immersive_portals$interior = start.getUUID();
        }
    }
    @Override
    public Portal ntm_immersive_portals$getExterior() {
        ServerLevel level = ((ServerLevel)ntm_immersive_portals$getExteriorLevel(tardis));
        Entity portal = ntm_immersive_portals$exterior != null ? level.getEntity(ntm_immersive_portals$exterior) : null;
        if(portal == null){
            UUID tardisID = UUID.fromString(tardis.getLevel().dimension().location().getPath());
            List<Portal> portals = Portals.tardisToPortals.get(tardisID);

            if(portals != null)
                portal = portals.stream().filter(filtering -> !filtering.level().dimension().equals(tardis.getId())).findFirst().orElse(null);
        }
        if(portal != null)
            return (Portal) portal;
        return null;
    }
    @Override
    public Portal ntm_immersive_portals$getInterior() {
        ServerLevel level = ((ServerLevel)ntm_immersive_portals$getInteriorLevel(tardis));
        Entity portal = ntm_immersive_portals$interior != null ? level.getEntity(ntm_immersive_portals$interior) : null;
        if(portal == null){
            UUID tardisID = UUID.fromString(tardis.getLevel().dimension().location().getPath());
            List<Portal> portals = Portals.tardisToPortals.get(tardisID);

            if(portals != null)
                portal = portals.stream().filter(filtering -> filtering.level().dimension().equals(tardis.getId())).findFirst().orElse(null);
        }
        if(portal != null)
            return (Portal) portal;
        return null;
    }

    @Unique
    private static Level ntm_immersive_portals$getInteriorLevel(ITardisLevel tardis){
        return tardis.getLevel();
    }

    @Unique
    private static Level ntm_immersive_portals$getExteriorLevel(ITardisLevel tardis){
        return ((ServerLevel) tardis.getLevel()).getServer().getLevel(tardis.getLocation().getLevel());
    }
}
