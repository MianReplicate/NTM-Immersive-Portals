package mian.minecraft.ntm_ip.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.helpers.WorldHelper;

public class DirectionHelper {
    public static Direction getExteriorDirection(ITardisLevel tardis) {
        BlockEntity entity = ((ServerLevel) tardis.getLevel()).getServer().getLevel(tardis.getLocation().getLevel()).getBlockEntity(tardis.getLocation().getPos());

        if (entity instanceof ExteriorTile exteriorTile) {
            return WorldHelper.getHorizontalFacing(exteriorTile.getBlockState());
        }

        return Direction.NORTH; // default fallback
    }

    public static Direction getInteriorDirection(ITardisLevel tardis) {
        return WorldHelper.getHorizontalFacing(
                tardis.getLevel().getBlockState(
                        BlockPos.containing(tardis.getInteriorManager().getMainInteriorDoor().getPosition(tardis.getLevel()))));
    }
}
