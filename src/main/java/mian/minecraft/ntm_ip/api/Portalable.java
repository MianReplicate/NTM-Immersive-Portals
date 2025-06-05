package mian.minecraft.ntm_ip.api;

import qouteall.imm_ptl.core.portal.Portal;

public interface Portalable {
    void ntm_immersive_portals$setStart(Portal portal);
    void ntm_immersive_portals$setDestination(Portal portal);
    Portal ntm_immersive_portals$getStart();
    Portal ntm_immersive_portals$getDestination();
}
