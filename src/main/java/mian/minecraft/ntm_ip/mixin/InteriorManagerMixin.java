package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.ExteriorDataHandlerImpl;
import mian.minecraft.ntm_ip.api.Portalable;
import mian.minecraft.ntm_ip.api.PublicEntityExterior;
import mian.minecraft.ntm_ip.helper.PortalHelper;
import mian.minecraft.ntm_ip.misc.BotiPortal;
import mian.minecraft.ntm_ip.misc.Portals;
import mian.minecraft.ntm_ip.registry.PortalDimensionRegistry;
import mian.minecraft.ntm_ip.registry.PortalDimensionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.exterior.EntityExterior;
import net.tardis.mod.exterior.Exterior;
import net.tardis.mod.exterior.TileExterior;
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
import oshi.util.tuples.Pair;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalExtension;
import qouteall.q_misc_util.my_util.DQuaternion;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(InteriorManager.class)
public abstract class InteriorManagerMixin implements Portalable {
    @Shadow
    @Final
    private ITardisLevel tardis;

    @Shadow
    public abstract DoorHandler getDoorHandler();

    @Shadow
    private HashMap<UUID, InteriorDoorData> interiorDoorPositions;

    @Unique
    public UUID ntm_immersive_portals$getTardisID() {
        return UUID.fromString(tardis.getLevel().dimension().location().getPath());
    }

    @Redirect(method = "getMainInteriorDoor", at = @At(value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;log(Lorg/apache/logging/log4j/Level;Ljava/lang/String;)V"))
    public void getMainInteriorDoor(Logger instance, org.apache.logging.log4j.Level level, String s) {
        // shut the fuck up i dont care how you got the main interior door
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void ntm_immersive_portals$tick(CallbackInfo ci) {
        if (tardis.isClient())
            return;

//        tardis.getExterior().getTeleportHandler().getParent()
        ServerLevel dimensionTo = ((ServerLevel) tardis.getLevel()).getServer().getLevel(tardis.getLocation().getLevel());
        Float rotation = null;
        Exterior exterior = tardis.getExterior();
        if(exterior instanceof TileExterior){
            BlockEntity be = dimensionTo.getBlockEntity(this.tardis.getLocation().getPos());
            if(be instanceof ExteriorTile){
                rotation = WorldHelper.getHorizontalFacing(be.getBlockState()).toYRot();
            }
        } else if(exterior instanceof EntityExterior entityExterior){
            PublicEntityExterior publicEntityExterior = (PublicEntityExterior) entityExterior;
            Entity entity = publicEntityExterior.ntm_immersive_portals$getEntity();
            if(entity != null){
                rotation = publicEntityExterior.ntm_immersive_portals$getEntity().getYRot();
            }
        }

        boolean shouldTeleport = getDoorHandler().getDoorState().isOpen();
        Optional<PortalDimensionType> dimensionType = PortalDimensionRegistry.getDimensionTypeFromTardis(tardis);

        boolean foundValidDoor = false;
        for (InteriorDoorData data : interiorDoorPositions.values()) {
            if (data != null && data.isValidDoor(this.tardis)) {
                foundValidDoor = true;
                break;
            }
        }

        if (rotation != null &&
                shouldTeleport &&
                dimensionType.isPresent() &&
                !tardis.isInVortex() &&
                !tardis.isTakingOffOrLanding() &&
                foundValidDoor &&
                ((ExteriorDataHandlerImpl) tardis.getExteriorExtraData()).ntm_immersive_portals$isBOTIEnabled()) {
            List<BotiPortal> portals = Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID());
            for (int i = 0; i < portals.size(); i++) {
                Portal portal = portals.get(i);
                if (!portal.isAlive() || !portal.isPortalValid()) {
                    portals.remove(portal);
                    i--;
                }
            }

            UUID tardisID = ntm_immersive_portals$getTardisID();

            BotiPortal exteriorPortal = ntm_immersive_portals$createOrEditExterior(tardis, dimensionTo, rotation, dimensionType.get(), tardisID);
            BotiPortal interiorPortal = ntm_immersive_portals$createOrEditInterior(tardis, dimensionType.get(), tardisID);

            PortalHelper.adjustPortalsToConnectAndSync(exteriorPortal, interiorPortal);
        } else {
            ntm_immersive_portals$removePortals();
        }
    }

    @Unique
    public BotiPortal ntm_immersive_portals$createOrEditExterior(ITardisLevel tardis, ServerLevel dimensionIn, float yRot, PortalDimensionType dimensionType, UUID tardisID) {
        BotiPortal exterior = ntm_immersive_portals$getExterior();

        if (exterior != null && !dimensionIn.dimension().equals(exterior.getOriginDim())) {
            NTMIP.LOGGER.info("Killed exterior because levels aren't equal!\nExpected: " + dimensionIn + "\nGot:" + exterior.getOriginWorld());
            PortalHelper.removePortal((ServerLevel) exterior.getOriginWorld(), exterior.getUUID());
        }

        ResourceKey<Level> targetDim = tardis.getId();

        InteriorDoorData door = tardis.getInteriorManager().getMainInteriorDoor();
        Vec3 origin = tardis.getLocation().getPos().getCenter();
        Vec3 dest = door.getPosition(dimensionIn);

        Vec3 extOffset = dimensionType.getExteriorPosition(tardis);
        Vec3 intOffset = dimensionType.getInteriorPosition(tardis);
        origin = origin.add(extOffset);
        dest = dest.add(intOffset);
        dest = dest.add(dimensionType.getDestinationToInterior(tardis));

        Pair<Double, Double> sizes = dimensionType.getExteriorSize(tardis);
        double width = sizes.getA();
        double height = sizes.getB();

        if (exterior == null) {
//            Pair<Double, Double> rotations = dimensionType.getExteriorAxis(tardis);

            exterior = PortalHelper.createPortal(
                    dimensionIn,
                    origin,
                    dest, // maybe work??,
                    targetDim,
                    DQuaternion.rotationByDegrees(new Vec3(0, -1, 0), yRot),
                    width,
                    height
            );

//            Pair<Double, Double> interiorRotations = dimensionType.getInteriorToExteriorAxis(tardis);
//            exterior.setOrientation(
//                    DQuaternion.rotationByDegrees(exterior.axisW, -interiorRotations.getA()).getAxisW(),
//                    DQuaternion.rotationByDegrees(exterior.axisH, -interiorRotations.getB()).getAxisH());
            // undo rotations that interior did

//            exterior.setOrientation(
//                            DQuaternion.rotationByDegrees(exterior.axisW, rotations.getA()).getAxisW(),
//                            DQuaternion.rotationByDegrees(exterior.axisH, rotations.getB()).getAxisH());

//            rotations = dimensionType.getExteriorToInteriorAxis(tardis);
//
//            Vec3 axisW = exterior.getRotation().getAxisW();
//            Vec3 axisH = exterior.getRotation().getAxisH();

//            exterior.setRotation(DQuaternion.fromFacingVecs(
//                    DQuaternion.rotationByDegrees(axisW, rotations.getA()).getAxisW(),
//                    DQuaternion.rotationByDegrees(axisH, rotations.getB()).getAxisH()));

            exterior.setValid(true);
            exterior.setInteractable(false);
            exterior.animation.defaultAnimation.durationTicks = 0;
            PortalExtension.get(exterior).adjustPositionAfterTeleport = false;
            exterior.setIsVisible(true);
            exterior.specialShape = dimensionType.getPortalShape(tardis);
            exterior.teleportable = true;

            Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID()).add(exterior);
            exterior.setTardisId(tardisID);
            McHelper.spawnServerEntity(exterior);

            NTMIP.LOGGER.info("Created exterior portal!");
            NTMIP.LOGGER.info("Origin:" + exterior.getOriginPos().toString());
            NTMIP.LOGGER.info("Dest:" + exterior.getDestPos().toString());
            NTMIP.LOGGER.info("Origin Dim:" + dimensionIn.toString());
            NTMIP.LOGGER.info("Dest Dim:" + targetDim.toString());
        }

        if (!origin.equals(exterior.getOriginPos())
                || !dest.equals(exterior.getDestPos())
                || !targetDim.equals(exterior.getDestDim())
                || width != exterior.width
                || height != exterior.height) {
            NTMIP.LOGGER.info("Origin:" + origin);
            NTMIP.LOGGER.info("Dest:" + dest);
            NTMIP.LOGGER.info("Origin Dim:" + dimensionIn);
            NTMIP.LOGGER.info("Dest Dim:" + targetDim.toString());

            exterior.setOriginPos(origin);
            exterior.setDestination(dest);
            exterior.setDestinationDimension(targetDim);
            exterior.setWidth(width);
            exterior.setHeight(height);
            exterior.specialShape = dimensionType.getPortalShape(tardis);
            exterior.reloadAndSyncToClient();
        }
        return exterior;
    }

    @Unique
    public BotiPortal ntm_immersive_portals$createOrEditInterior(ITardisLevel tardis, PortalDimensionType dimensionType, UUID tardisID) {
        ServerLevel level = (ServerLevel) tardis.getLevel();
        BotiPortal interior = ntm_immersive_portals$getInterior();

        InteriorDoorData door = tardis.getInteriorManager().getMainInteriorDoor();
        Vec3 origin = door.getPosition(level);
        Vec3 dest = tardis.getLocation().getPos().getCenter();

        Vec3 extOffset = dimensionType.getExteriorPosition(tardis);
        Vec3 intOffset = dimensionType.getInteriorPosition(tardis);
        origin = origin.add(intOffset);
        dest = dest.add(extOffset);
        dest = dest.add(dimensionType.getDestinationToExterior(tardis));

        if (interior != null && !level.dimension().equals(interior.getOriginDim())) {
            NTMIP.LOGGER.info("Killed interior because levels aren't equal!\nExpected: " + level + "\nGot:" + interior.getOriginWorld());
            PortalHelper.removePortal((ServerLevel) interior.getOriginWorld(), interior.getUUID());
        }

        ResourceKey<Level> targetDim = tardis.getLocation().getLevel();
        Pair<Double, Double> sizes = dimensionType.getInteriorSize(tardis);
        double width = sizes.getA();
        double height = sizes.getB();

        if (interior == null) {

            float y = WorldHelper.getHorizontalFacing(level.getBlockState(BlockPos.containing(door.getPosition(level))))
                    .toYRot();

            interior = PortalHelper.createPortal(
                    level,
                    origin,
                    dest, // maybe work??,
                    targetDim,
                    DQuaternion.rotationByDegrees(new Vec3(0, -1, 0), y),
                    width,
                    height
            );
            interior.setValid(true);


//            interior.setOrientation(
//                    DQuaternion.rotationByDegrees(interior.axisW, rotations.getA()).getAxisW(),
//                    DQuaternion.rotationByDegrees(interior.axisH, rotations.getB()).getAxisH());

//            rotations = dimensionType.getInteriorToExteriorAxis(tardis);
//
//            Vec3 axisW = interior.getRotation().getAxisW();
//            Vec3 axisH = interior.getRotation().getAxisH();

//            interior.setRotation(DQuaternion.fromFacingVecs(
//                    DQuaternion.rotationByDegrees(axisW, rotations.getA()).getAxisW(),
//                    DQuaternion.rotationByDegrees(axisH, rotations.getB()).getAxisH()));

            interior.setInteractable(false);
            interior.animation.defaultAnimation.durationTicks = 0;
            PortalExtension.get(interior).adjustPositionAfterTeleport = false;
            interior.setIsVisible(true);
            interior.teleportable = true;
            interior.specialShape = dimensionType.getPortalShape(tardis).getFlippedWithScaling(1);
            interior.setIsInterior(true);

            Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID()).add(interior);
            interior.setTardisId(tardisID);
            McHelper.spawnServerEntity(interior);

            NTMIP.LOGGER.info("Created interior portal!");
            NTMIP.LOGGER.info("Origin:" + origin);
            NTMIP.LOGGER.info("Dest:" + dest);
            NTMIP.LOGGER.info("Origin Dim:" + level);
            NTMIP.LOGGER.info("Dest Dim:" + targetDim.toString());
        }

        if (!origin.equals(interior.getOriginPos())
                || !dest.equals(interior.getDestPos())
                || !targetDim.equals(interior.getDestDim())
                || width != interior.width
                || height != interior.height) {
            NTMIP.LOGGER.info("Origin:" + origin);
            NTMIP.LOGGER.info("Dest:" + dest);
            NTMIP.LOGGER.info("Origin Dim:" + level);
            NTMIP.LOGGER.info("Dest Dim:" + targetDim.toString());

            interior.setOriginPos(origin);
            interior.setDestination(dest);
            interior.setDestinationDimension(tardis.getLocation().getLevel());
            interior.setWidth(width);
            interior.setHeight(height);
            interior.specialShape = dimensionType.getPortalShape(tardis).getFlippedWithScaling(1);
            interior.reloadAndSyncToClient();
        }
        return interior;
    }

    @Override
    public void ntm_immersive_portals$removePortals() {
        List<BotiPortal> portals = Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID());
        for (int i = 0; i < portals.size(); i++) {
            BotiPortal portal = portals.get(i);
            portal.setValid(false);
            portals.remove(portal);
            i--;
        }
        portals.clear();
    }

    @Override
    public BotiPortal ntm_immersive_portals$getExterior() {
        return Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID())
                .stream().filter(filtering -> !filtering.getIsInterior()).findFirst().orElse(null);
    }

    @Override
    public BotiPortal ntm_immersive_portals$getInterior() {
        return Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID())
                .stream().filter(BotiPortal::getIsInterior).findFirst().orElse(null);
    }
}
