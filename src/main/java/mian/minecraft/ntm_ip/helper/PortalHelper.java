package mian.minecraft.ntm_ip.helper;

import mian.minecraft.ntm_ip.misc.BotiPortal;
import mian.minecraft.ntm_ip.registry.EntityTypeRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.platform_specific.IPRegistry;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

import javax.annotation.Nullable;
import java.util.UUID;

public class PortalHelper {
    public static void removePortal(ServerLevel level, UUID portalID){
        Entity portal = level.getEntity(portalID);
        if(portal instanceof Portal portal1 && portal1 != null && portal1.isAlive())
            portal1.kill();
    }

    public static BotiPortal createPortal(Level level, // org level
                                          Vec3 origin, // pos
                                          Vec3 destination, // dest
                                          ResourceKey<Level> destinationLvl, // level to go
                                          DQuaternion quat, // rotation
                                          double width,
                                          double height
    ) {
        BotiPortal portal = EntityTypeRegistry.BOTI_PORTAL.get().create(level);

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

    public static void adjustPortalsToConnectAndSync(Portal a, Portal b){
        DQuaternion aRotation = a.getRotation();
        DQuaternion bRotation = b.getRotation();

        PortalManipulation.adjustRotationToConnect(a, b);

        if(aRotation == null || !aRotation.equals(a.getRotation()))
            a.reloadAndSyncToClient();

        if(bRotation == null || !bRotation.equals(b.getRotation()))
            b.reloadAndSyncToClient();
    }
}
