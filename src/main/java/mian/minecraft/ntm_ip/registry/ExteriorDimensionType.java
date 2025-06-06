package mian.minecraft.ntm_ip.registry;

import net.minecraft.world.phys.Vec3;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.exterior.ExteriorType;
import oshi.util.tuples.Pair;

import java.util.function.Function;

public class ExteriorDimensionType {
    private final Function<ITardisLevel, Pair<Vec3, Vec3>> positions;
    private final Function<ITardisLevel, Pair<Pair<Double, Double>, Pair<Double, Double>>> sizes;

    public ExteriorDimensionType(Function<ITardisLevel, Pair<Vec3, Vec3>> positions,
                                 Function<ITardisLevel, Pair<Pair<Double, Double>, Pair<Double, Double>>> sizes){
        this.positions = positions;
        this.sizes = sizes;
    }

    // returns exterior, interior offsets
    public Pair<Pair<Double, Double>, Pair<Double, Double>> getSizes(ITardisLevel tardis) {
        return sizes.apply(tardis);
    }

    // returns exterior, interior offsets
    public Pair<Vec3, Vec3> getPositions(ITardisLevel tardis) {
        return positions.apply(tardis);
    }
}
