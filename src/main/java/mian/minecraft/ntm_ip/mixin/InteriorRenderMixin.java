package mian.minecraft.ntm_ip.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.tardis.mod.blockentities.InteriorDoorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.client.renderers.tiles.InteriorDoorRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.render.context_management.WorldRenderInfo;

@Mixin(InteriorDoorRender.class)
public class InteriorRenderMixin {
    @Inject(remap = false, at = @At("HEAD"), method = "render", cancellable = true)
    public void shouldRender(ITardisLevel tardis, InteriorDoorTile tile, PoseStack pose, MultiBufferSource buffer, float ageInTicks, int packedLight, int packedOverlay, CallbackInfo ci){
        if(WorldRenderInfo.isRendering()
                && tile.getLevel().dimension().equals(WorldRenderInfo.getTopRenderInfo().world.dimension()) // is this being rendered in a portal?
        ) {
            ci.cancel();
        }
    }
}
