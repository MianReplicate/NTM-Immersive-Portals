package mian.minecraft.ntm_ip.misc;

import net.tardis.mod.cap.level.ITardisLevel;
import qouteall.imm_ptl.core.portal.Portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Portals {
    public static HashMap<UUID, List<BotiPortal>> tardisToPortals = new HashMap<>();

    public static List<BotiPortal> getPortalsForTardis(ITardisLevel tardis){
        return getPortalsForTardis(UUID.fromString(tardis.getLevel().dimension().location().getPath()));
    }

    public static List<BotiPortal> getPortalsForTardis(UUID tardis){
        List<BotiPortal> portals = tardisToPortals.getOrDefault(tardis, new ArrayList<>());
        tardisToPortals.putIfAbsent(tardis, portals);
        return portals;
    }
}
