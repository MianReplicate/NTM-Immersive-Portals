package mian.minecraft.ntm_ip.api;

import qouteall.imm_ptl.core.portal.Portal;

public interface Portalable {
    void ntm_immersive_portals$setPortal(Portal portal);
    void ntm_immersive_portals$removePortal();
    Portal ntm_immersive_portals$getPortal();
}
