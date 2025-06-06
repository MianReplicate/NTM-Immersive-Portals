package mian.minecraft.ntm_ip.registry.portal_types;

import mian.minecraft.ntm_ip.helper.DirectionHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.cap.level.ITardisLevel;
import oshi.util.tuples.Pair;

public class SteamType extends ChameleonType {
    @Override
    public Pair<Double, Double> getInteriorSize(ITardisLevel level) {
        return new Pair<>(1D, 2.2D);
    }

    @Override
    public Pair<Double, Double> getExteriorSize(ITardisLevel level) {
        return new Pair<>(1D, 2.2D);
    }

    @Override
    public Vec3 getInteriorPosition(ITardisLevel level) {
        Direction intDirection = DirectionHelper.getInteriorDirection(level).getOpposite();
        Vec3 intOffset = new Vec3(0, 1.3, 0);
        intOffset = intOffset.relative(intDirection, 0.4);

        return intOffset;
    }

    @Override
    public Vec3 getExteriorPosition(ITardisLevel level) {
        Direction extDirection = DirectionHelper.getExteriorDirection(level);
        Vec3 extOffset = new Vec3(0, 0.75, 0);

        extOffset = extOffset.relative(extDirection, 0.75);

        return extOffset;
    }
}
