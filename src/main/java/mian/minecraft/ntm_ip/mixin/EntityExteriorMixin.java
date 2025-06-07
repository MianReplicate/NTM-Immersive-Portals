package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.api.PublicEntityExterior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.exterior.EntityExterior;
import net.tardis.mod.exterior.Exterior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(EntityExterior.class)
public class EntityExteriorMixin implements PublicEntityExterior {
    @Shadow private UUID exteriorID;

    @Override
    public Entity ntm_immersive_portals$getEntity() {
        ITardisLevel tardis = ((Exterior)(Object) this).tardis;
        return tardis.getLevel().getServer().getLevel(tardis.getLocation().getLevel()).getEntity(exteriorID);
    }
}
