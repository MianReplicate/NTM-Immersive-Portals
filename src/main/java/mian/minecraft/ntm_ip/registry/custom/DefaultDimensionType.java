package mian.minecraft.ntm_ip.registry.custom;

import mian.minecraft.ntm_ip.registry.ExteriorDimensionType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

public class DefaultDimensionType extends ExteriorDimensionType {
    public DefaultDimensionType() {
        super(
                level -> new Pair<>(new Vec3(0, 0, 0), new Vec3(0, 0, 0)),
                level -> new Pair<>(new Pair<>(1D, 2D), new Pair<>(1D, 2D))
        );
    }
}
