package mian.minecraft.ntm_ip.registry;

import net.minecraft.world.phys.Vec3;
import net.tardis.mod.cap.level.ITardisLevel;
import oshi.util.tuples.Pair;

public abstract class PortalDimensionType {
    // Interior offset size
    public abstract Pair<Double, Double> getInteriorSize(ITardisLevel level);

    // Exterior offset size
    public abstract Pair<Double, Double> getExteriorSize(ITardisLevel level);

    // Interior offset position
    public abstract Vec3 getInteriorPosition(ITardisLevel level);

    // Exterior offset position
    public abstract Vec3 getExteriorPosition(ITardisLevel level);

    // Interior offset axis (1st is axisW, 2nd is axisH)
    public abstract Pair<Double, Double> getInteriorAxis(ITardisLevel level);

    // Exterior offset axis (1st is axisW, 2nd is axisH)
    public abstract Pair<Double, Double> getExteriorAxis(ITardisLevel level);

    // Interior to exterior offset axis (1st is axisW, 2nd is axisH)
    public abstract Pair<Double, Double> getInteriorToExteriorAxis(ITardisLevel level);

    // Exterior to interior offset axis (1st is axisW, 2nd is axisH)
    public abstract Pair<Double, Double> getExteriorToInteriorAxis(ITardisLevel level);

    // Exterior to interior offset destination (usually not needed unless working with mixed door & exterior types)
    public abstract Vec3 getDestinationToInterior(ITardisLevel level);

    // Interior to exterior offset destination (usually not needed unless working with mixed door & exterior types)
    public abstract Vec3 getDestinationToExterior(ITardisLevel level);
}
