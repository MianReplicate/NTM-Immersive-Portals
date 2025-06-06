package mian.minecraft.ntm_ip.registry;

import net.minecraft.world.phys.Vec3;
import net.tardis.mod.cap.level.ITardisLevel;
import oshi.util.tuples.Pair;

public abstract class PortalDimensionType {
    public abstract Pair<Double, Double> getInteriorSize(ITardisLevel level);

    public abstract Pair<Double, Double> getExteriorSize(ITardisLevel level);

    public abstract Vec3 getInteriorPosition(ITardisLevel level);

    public abstract Vec3 getExteriorPosition(ITardisLevel level);
}
