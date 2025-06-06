package mian.minecraft.ntm_ip.registry.custom;

import mian.minecraft.ntm_ip.helper.DirectionHelper;
import mian.minecraft.ntm_ip.registry.ExteriorDimensionType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

public class SteamDimensionType extends ExteriorDimensionType {
    public SteamDimensionType(){
        super(
                (level) -> {
                    Direction extDirection = DirectionHelper.getExteriorDirection(level).getOpposite();
                    Vec3 extOffset = new Vec3(0, 0, 0);

                    Direction intDirection = DirectionHelper.getInteriorDirection(level).getOpposite();
                    Vec3 intOffset = new Vec3(0, 0, 0);
                    return new Pair<>(extOffset.relative(extDirection, 0.75), intOffset.relative(intDirection, 2));
                },
                (level) -> new Pair<>(new Pair<>(1D, 2D), new Pair<>(1D, 2D))
        );
    }
}
