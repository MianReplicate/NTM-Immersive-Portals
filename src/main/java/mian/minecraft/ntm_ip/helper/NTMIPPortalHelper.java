package mian.minecraft.ntm_ip.helper;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.misc.BotiPortal;
import mian.minecraft.ntm_ip.registry.IPEntityTypeRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.helpers.WorldHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

import java.util.UUID;

public class NTMIPPortalHelper {
    public static void removePortal(ServerLevel level, UUID portalID) {
        Entity portal = level.getEntity(portalID);
        if (portal instanceof Portal portal1 && portal1 != null && portal1.isAlive())
            portal1.kill();
    }

    public static void realignRotationToExterior(ExteriorTile exteriorTile, Portal interior) {
        DQuaternion otherSide = DQuaternion.fromFacingVecs(new Vec3(1, 0, 0), new Vec3(0, 1, 0));

        float angle = (-WorldHelper.getDegreeFromRotation(WorldHelper.getHorizontalFacing(exteriorTile.getBlockState()))
                * 2) - 360;
        otherSide.add(DQuaternion.rotationByDegrees(otherSide.getAxisH(),
                angle));

        NTMIP.LOGGER.info(String.valueOf(angle));

        interior.setOtherSideOrientation(otherSide);
    }

    public static BotiPortal createPortal(Level level, // org level
                                          Vec3 origin, // pos
                                          Vec3 destination, // dest
                                          ResourceKey<Level> destinationLvl, // level to go
                                          DQuaternion quat, // rotation
                                          double width,
                                          double height
    ) {
        BotiPortal portal = IPEntityTypeRegistry.BOTI_PORTAL.get().create(level);

        portal.setOriginPos(origin);
        portal.setDestinationDimension(destinationLvl);
        portal.setDestination(destination);
        portal.setOrientationAndSize(
                new Vec3(1, 0, 0), // axisW
                new Vec3(0, 1, 0), // axisH
                width, // width
                height // height
        );
        PortalManipulation.rotatePortalBody(portal, quat);

        return portal;
    }

    public static void adjustPortalsToConnectAndSync(Portal a, Portal b) {
        DQuaternion aRotation = a.getRotation();
        DQuaternion bRotation = b.getRotation();

        PortalManipulation.adjustRotationToConnect(a, b);

        if (aRotation == null || !aRotation.equals(a.getRotation()))
            a.reloadAndSyncToClient();

        if (bRotation == null || !bRotation.equals(b.getRotation()))
            b.reloadAndSyncToClient();
    }

    public static String createDebugForPortal(Portal portal){
        return  "Origin: " + portal.getOriginPos().toString() + "\n" +
                "Dest: " + portal.getDestPos().toString() + "\n" +
                "Origin Dim: " + portal.getOriginDim().location() + "\n" +
                "Dest Dim: " + portal.getDestDim().location() + "\n" +
                "Orientation: " + portal.getOrientationRotation().toString() + "\n" +
                "Rotation:" + (portal.getRotation() != null ? portal.getRotation().toString() : "null");
    }
}
