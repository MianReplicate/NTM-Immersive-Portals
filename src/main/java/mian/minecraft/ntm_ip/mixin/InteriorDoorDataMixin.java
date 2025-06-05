package mian.minecraft.ntm_ip.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.blockentities.InteriorDoorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.helpers.WorldHelper;
import net.tardis.mod.misc.tardis.InteriorDoorData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InteriorDoorData.class)
public abstract class InteriorDoorDataMixin {
    @Shadow private BlockPos doorPos;

    @Shadow public abstract float getRotation(ITardisLevel tardis);

    /**
     * @author MianReplicate (NTM Immersive Portals)
     * @reason Have to overwrite this method to make it so all calls from entity don't get called when entity is null for Immersive Portals
     */
    @Overwrite
    public Vec3 placeAtMe(Entity e, ITardisLevel tardis){
        if (this.doorPos != null) {
            tardis.getLevel().getChunkAt(this.doorPos);
            BlockEntity var4 = tardis.getLevel().getBlockEntity(this.doorPos);
            if (var4 instanceof InteriorDoorTile) {
                InteriorDoorTile door = (InteriorDoorTile)var4;
                if(e != null)
                    e.setYRot(this.getRotation(tardis));
                return WorldHelper.centerOfBlockPos(door.getBlockPos().relative(WorldHelper.getHorizontalFacing(door.getBlockState())));
            }

            this.doorPos = null;
        }

        if(e == null)
            return null;
        return e.position();
    }
}
