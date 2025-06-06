package mian.minecraft.ntm_ip.registry.portal_types;

import mian.minecraft.ntm_ip.registry.PortalDimensionType;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.cap.level.ITardisLevel;
import oshi.util.tuples.Pair;

public class ChameleonType extends PortalDimensionType {
    @Override
    public Pair<Double, Double> getInteriorSize(ITardisLevel level) {
        return new Pair<>(1D, 2D);
    }

    @Override
    public Pair<Double, Double> getExteriorSize(ITardisLevel level) {
        return new Pair<>(1D, 2D);
    }

    @Override
    public Vec3 getInteriorPosition(ITardisLevel level) {
        return new Vec3(0, 0, 0);
    }

    @Override
    public Vec3 getExteriorPosition(ITardisLevel level) {
        return new Vec3(0, 0, 0);
    }
}
