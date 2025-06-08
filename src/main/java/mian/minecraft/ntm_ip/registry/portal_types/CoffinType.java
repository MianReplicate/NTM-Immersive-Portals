package mian.minecraft.ntm_ip.registry.portal_types;

import mian.minecraft.ntm_ip.helper.NTMIPDirectionHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.cap.level.ITardisLevel;
import oshi.util.tuples.Pair;
import qouteall.imm_ptl.core.portal.GeometryPortalShape;

public class CoffinType extends DefaultType {
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
        Direction intDirection = NTMIPDirectionHelper.getInteriorDirection(level).getOpposite();
        Vec3 intOffset = new Vec3(0, 1.3, 0);
        intOffset = intOffset.relative(intDirection, 0.4);

        return intOffset;
    }

    @Override
    public Vec3 getExteriorPosition(ITardisLevel level) {
        Direction extDirection = NTMIPDirectionHelper.getExteriorDirection(level);
        Vec3 extOffset = new Vec3(0, 0.75, 0);

        extOffset = extOffset.relative(extDirection, 0.3);

        return extOffset;
    }

    @Override
    public GeometryPortalShape getPortalShape(ITardisLevel level) {
        GeometryPortalShape shape = new GeometryPortalShape();
        // Form the corner triangles
        shape.triangles.add(new GeometryPortalShape.TriangleInPlane(-0.28, 1, -0.28, 0.29, -0.6, 0.29));
        shape.triangles.add(new GeometryPortalShape.TriangleInPlane(0.28, 1, 0.28, 0.29, 0.6, 0.29));

        // the bottom corners
        shape.triangles.add(new GeometryPortalShape.TriangleInPlane(-0.28, -1.21, -0.28, 0.29, -0.6, 0.29));
        shape.triangles.add(new GeometryPortalShape.TriangleInPlane(-0.28, -1.21, -0.4, -1.21, -0.6, 0.29));

        shape.triangles.add(new GeometryPortalShape.TriangleInPlane(0.28, -1.21, 0.28, 0.29, 0.6, 0.29));
        shape.triangles.add(new GeometryPortalShape.TriangleInPlane(0.28, -1.21, 0.4, -1.21, 0.6, 0.29));

        // Form the rectangle between the two triangles
        shape.triangles.add(new GeometryPortalShape.TriangleInPlane(-0.28, 1, -0.28, -1.21, 0.28, 1));
        shape.triangles.add(new GeometryPortalShape.TriangleInPlane(0.28, 1, 0.28, -1.21, -0.28, -1.21));

        return shape;
    }
}
