package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.ExteriorDataHandlerImpl;
import mian.minecraft.ntm_ip.api.Portalable;
import mian.minecraft.ntm_ip.helper.NTMIPHelper;
import mian.minecraft.ntm_ip.helper.NTMIPPortalHelper;
import mian.minecraft.ntm_ip.misc.BotiPortal;
import mian.minecraft.ntm_ip.misc.NTMIPPortals;
import mian.minecraft.ntm_ip.registry.PortalDimensionRegistry;
import mian.minecraft.ntm_ip.registry.PortalDimensionType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

        ServerLevel dimensionTo = ((ServerLevel) tardis.getLevel()).getServer().getLevel(tardis.getLocation().getLevel());
        float rotation = NTMIPHelper.getActualTardisRotation(tardis);

        boolean shouldTeleport = getDoorHandler().getDoorState().isOpen();
        Optional<PortalDimensionType> dimensionType = PortalDimensionRegistry.getDimensionTypeFromTardis(tardis);

        boolean foundValidDoor = false;
        for (InteriorDoorData data : interiorDoorPositions.values()) {
            if (data != null && data.isValidDoor(this.tardis)) {
                foundValidDoor = true;
                break;
            }
        }

        if (
                shouldTeleport &&
                        dimensionType.isPresent() &&
                        !tardis.isInVortex() &&
                        !tardis.isTakingOffOrLanding() &&
                        foundValidDoor &&
                        ((ExteriorDataHandlerImpl) tardis.getExteriorExtraData()).ntm_immersive_portals$isBOTIEnabled()) {
            List<BotiPortal> portals = NTMIPPortals.getPortalsForTardis(ntm_immersive_portals$getTardisID());
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

            NTMIPPortalHelper.adjustPortalsToConnectAndSync(exteriorPortal, interiorPortal);
        } else {
            ntm_immersive_portals$removePortals();
        }
    }

    @Unique
    public BotiPortal ntm_immersive_portals$createOrEditExterior(ITardisLevel tardis, ServerLevel dimensionIn, float yRot, PortalDimensionType dimensionType, UUID tardisID) {
        BotiPortal exterior = ntm_immersive_portals$getExterior();

        if (exterior != null && !dimensionIn.dimension().equals(exterior.getOriginDim())) {
            NTMIP.LOGGER.debug("Killed exterior because levels aren't equal!\nExpected: {}\nGot:{}", dimensionIn, exterior.getOriginWorld());
            NTMIPPortalHelper.removePortal((ServerLevel) exterior.getOriginWorld(), exterior.getUUID());
        }

        ResourceKey<Level> targetDim = tardis.getId();

        InteriorDoorData door = tardis.getInteriorManager().getMainInteriorDoor();
        Vec3 origin = NTMIPHelper.getActualTardisLocation(tardis);
        Vec3 dest = door.getPosition(dimensionIn);

        Vec3 extOffset = dimensionType.getExteriorPosition(tardis);
        Vec3 intOffset = dimensionType.getInteriorPosition(tardis);
        origin = origin.add(extOffset);
        dest = dest.add(intOffset);
        dest = dest.add(dimensionType.getDestinationToInterior(tardis));

        Pair<Double, Double> sizes = dimensionType.getExteriorSize(tardis);
        double width = sizes.getA();
        double height = sizes.getB();

        DQuaternion rotation = DQuaternion.rotationByDegrees(new Vec3(0, -1, 0), yRot);

        if (exterior == null) {
//            Pair<Double, Double> rotations = dimensionType.getExteriorAxis(tardis);

            exterior = NTMIPPortalHelper.createPortal(
                    dimensionIn,
                    origin,
                    dest, // maybe work??,
                    targetDim,
                    rotation,
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

            NTMIPPortals.getPortalsForTardis(ntm_immersive_portals$getTardisID()).add(exterior);
            exterior.setTardisId(tardisID);
            McHelper.spawnServerEntity(exterior);

            String string = "\nCreated exterior portal!\n" + NTMIPPortalHelper.createDebugForPortal(exterior);
            NTMIP.LOGGER.debug(string);
        }

        if (!origin.equals(exterior.getOriginPos())
                || !dest.equals(exterior.getDestPos())
                || !targetDim.equals(exterior.getDestDim())
                || width != exterior.width
                || height != exterior.height
                || !rotation.rotate(new Vec3(1, 0, 0)).equals(exterior.axisW)
                || !rotation.rotate(new Vec3(0, 1, 0)).equals(exterior.axisH)) {
            exterior.setOriginPos(origin);
            exterior.setDestination(dest);
            exterior.setDestinationDimension(targetDim);
            exterior.setOrientationAndSize(
                    rotation.rotate(new Vec3(1, 0, 0)), // axisW
                    rotation.rotate(new Vec3(0, 1, 0)), // axisH
                    width, // width
                    height // height
            );
            exterior.specialShape = dimensionType.getPortalShape(tardis);
            exterior.reloadAndSyncToClient();

            String string = "\nEdited exterior portal!\n" + NTMIPPortalHelper.createDebugForPortal(exterior);
            NTMIP.LOGGER.debug(string);
        }
        return exterior;
    }

    @Unique
    public BotiPortal ntm_immersive_portals$createOrEditInterior(ITardisLevel tardis, PortalDimensionType dimensionType, UUID tardisID) {
        ServerLevel level = (ServerLevel) tardis.getLevel();
        BotiPortal interior = ntm_immersive_portals$getInterior();

        InteriorDoorData door = tardis.getInteriorManager().getMainInteriorDoor();
        Vec3 origin = door.getPosition(level);
        Vec3 dest = NTMIPHelper.getActualTardisLocation(tardis);

        Vec3 extOffset = dimensionType.getExteriorPosition(tardis);
        Vec3 intOffset = dimensionType.getInteriorPosition(tardis);
        origin = origin.add(intOffset);
        dest = dest.add(extOffset);
        dest = dest.add(dimensionType.getDestinationToExterior(tardis));

        if (interior != null && !level.dimension().equals(interior.getOriginDim())) {
            NTMIP.LOGGER.debug("\nKilled interior because levels aren't equal!\nExpected: {}\nGot:{}", level, interior.getOriginWorld());
            NTMIPPortalHelper.removePortal((ServerLevel) interior.getOriginWorld(), interior.getUUID());
        }

        ResourceKey<Level> targetDim = tardis.getLocation().getLevel();
        Pair<Double, Double> sizes = dimensionType.getInteriorSize(tardis);
        double width = sizes.getA();
        double height = sizes.getB();

        if (interior == null) {

            float y = WorldHelper.getHorizontalFacing(level.getBlockState(BlockPos.containing(door.getPosition(level))))
                    .toYRot();

            interior = NTMIPPortalHelper.createPortal(
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

            NTMIPPortals.getPortalsForTardis(ntm_immersive_portals$getTardisID()).add(interior);
            interior.setTardisId(tardisID);
            McHelper.spawnServerEntity(interior);

            String string = "\nCreated interior portal!\n" + NTMIPPortalHelper.createDebugForPortal(interior);
            NTMIP.LOGGER.debug(string);
        }

        if (!origin.equals(interior.getOriginPos())
                || !dest.equals(interior.getDestPos())
                || !targetDim.equals(interior.getDestDim())
                || width != interior.width
                || height != interior.height) {
            interior.setOriginPos(origin);
            interior.setDestination(dest);
            interior.setDestinationDimension(tardis.getLocation().getLevel());
            interior.setWidth(width);
            interior.setHeight(height);
            interior.specialShape = dimensionType.getPortalShape(tardis).getFlippedWithScaling(1);
            interior.reloadAndSyncToClient();

            String string = "\nEdited interior portal!\n" + NTMIPPortalHelper.createDebugForPortal(interior);
            NTMIP.LOGGER.debug(string);
        }
        return interior;
    }

    @Override
    public void ntm_immersive_portals$removePortals() {
        List<BotiPortal> portals = NTMIPPortals.getPortalsForTardis(ntm_immersive_portals$getTardisID());
        portals.forEach(portal -> portal.setValid(false));
        portals.clear();
    }

    @Override
    public BotiPortal ntm_immersive_portals$getExterior() {
        return NTMIPPortals.getPortalsForTardis(ntm_immersive_portals$getTardisID())
                .stream().filter(filtering -> !filtering.getIsInterior()).findFirst().orElse(null);
    }

    @Override
    public BotiPortal ntm_immersive_portals$getInterior() {
        return NTMIPPortals.getPortalsForTardis(ntm_immersive_portals$getTardisID())
                .stream().filter(BotiPortal::getIsInterior).findFirst().orElse(null);
    }
}
