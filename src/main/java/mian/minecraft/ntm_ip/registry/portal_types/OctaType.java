package mian.minecraft.ntm_ip.registry.portal_types;

import mian.minecraft.ntm_ip.helper.NTMIPDirectionHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.cap.level.ITardisLevel;
import oshi.util.tuples.Pair;

public class OctaType extends DefaultType {
    @Override
    public Pair<Double, Double> getInteriorSize(ITardisLevel level) {
        return new Pair<>(1.5D, 2.4D);
    }

    @Override
    public Pair<Double, Double> getExteriorSize(ITardisLevel level) {
        return new Pair<>(1.4D, 2.8D);
    }

    @Override
    public Vec3 getInteriorPosition(ITardisLevel level) {
        Direction intDirection = NTMIPDirectionHelper.getInteriorDirection(level).getOpposite();
        Vec3 intOffset = new Vec3(0, 1.35, 0);
        intOffset = intOffset.relative(intDirection, 0.4);

        return intOffset;
    }

    @Override
    public Vec3 getExteriorPosition(ITardisLevel level) {
        Direction extDirection = NTMIPDirectionHelper.getExteriorDirection(level);
        Vec3 extOffset = new Vec3(0, .9, 0);

        extOffset = extOffset.relative(extDirection, 0.5);

        return extOffset;
    }

    @Override
    public Vec3 getDestinationToInterior(ITardisLevel level) {
        return new Vec3(0, 0.05, 0);
    }

    @Override
    public Vec3 getDestinationToExterior(ITardisLevel level) {
        return new Vec3(0, -0.05, 0);
    }
}
