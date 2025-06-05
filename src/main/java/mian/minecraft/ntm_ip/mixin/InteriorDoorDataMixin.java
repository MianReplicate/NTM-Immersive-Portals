package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.api.PublicDoorData;
import net.tardis.mod.misc.tardis.InteriorDoorData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(InteriorDoorData.class)
public class InteriorDoorDataMixin implements PublicDoorData {
    @Shadow private UUID doorEntityID;

    @Override
    public UUID ntm_immersive_portals$getUUID() {
        return doorEntityID;
    }
}
