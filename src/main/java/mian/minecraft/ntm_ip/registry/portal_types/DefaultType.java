package mian.minecraft.ntm_ip.registry.portal_types;

import mian.minecraft.ntm_ip.helper.DirectionHelper;
import mian.minecraft.ntm_ip.registry.PortalDimensionType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.cap.level.ITardisLevel;
import oshi.util.tuples.Pair;
import qouteall.imm_ptl.core.portal.GeometryPortalShape;
import qouteall.imm_ptl.core.portal.PortalManipulation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultType extends PortalDimensionType {
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

    @Override
    public Pair<Double, Double> getInteriorAxis(ITardisLevel level) {
        return new Pair<>(0D, 0D);
    }

    @Override
    public Pair<Double, Double> getExteriorAxis(ITardisLevel level) {
        return new Pair<>(0D, 0D);
    }

    @Override
    public Pair<Double, Double> getInteriorToExteriorAxis(ITardisLevel level) {
        return new Pair<>(0D, 0D);
    }

    @Override
    public Pair<Double, Double> getExteriorToInteriorAxis(ITardisLevel level) {
        return new Pair<>(0D, 0D);
    }

    @Override
    public Vec3 getDestinationToInterior(ITardisLevel level) {
        return new Vec3(0, 0, 0);
    }

    @Override
    public Vec3 getDestinationToExterior(ITardisLevel level) {
        return new Vec3(0, 0, 0);
    }

    @Override
    public GeometryPortalShape getPortalShape(ITardisLevel level){
        return null;
    }
}
