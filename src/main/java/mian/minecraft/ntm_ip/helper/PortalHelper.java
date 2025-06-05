package mian.minecraft.ntm_ip.helper;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.platform_specific.IPRegistry;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

public class PortalHelper {
    public static void removePortal(Portal portal){
        portal.kill();
    }

    public static Portal createPortal(Level level, // org level
                                      Vec3 origin, // pos
                                      Vec3 destination, // dest
                                      ResourceKey<Level> destinationLvl, // level to go
                                      DQuaternion quat, // rotation
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
}
