package mian.minecraft.ntm_ip.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.client.renderers.exteriors.ExteriorRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.render.context_management.WorldRenderInfo;

@Mixin(ExteriorRenderer.class)
public abstract class ExteriorRenderMixin<T extends ExteriorTile> {
    @Inject(remap = false, at = @At("HEAD"), method = "render(Lnet/tardis/mod/blockentities/exteriors/ExteriorTile;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", cancellable = true)
    public void shouldRender(T pBlockEntity, float pPartialTick, PoseStack pose, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay, CallbackInfo ci){
        if(WorldRenderInfo.isRendering()
                && pBlockEntity.getLevel().dimension().equals(WorldRenderInfo.getTopRenderInfo().world.dimension()) // is this being rendered in a portal?
        ) {
            ci.cancel();
        }
    }
}
