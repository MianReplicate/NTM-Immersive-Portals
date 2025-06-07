package mian.minecraft.ntm_ip.api;

import mian.minecraft.ntm_ip.misc.BotiPortal;

public interface Portalable {
    void ntm_immersive_portals$removePortals();

    BotiPortal ntm_immersive_portals$getExterior();

    BotiPortal ntm_immersive_portals$getInterior();
}
