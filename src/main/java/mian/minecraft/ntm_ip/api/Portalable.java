package mian.minecraft.ntm_ip.api;

import qouteall.imm_ptl.core.portal.Portal;

import javax.annotation.Nullable;

public interface Portalable {
    void ntm_immersive_portals$setExterior(@Nullable Portal portal);
    void ntm_immersive_portals$setInterior(@Nullable Portal portal);
    void ntm_immersive_portals$removePortals();
    Portal ntm_immersive_portals$getExterior();
    Portal ntm_immersive_portals$getInterior();
}
