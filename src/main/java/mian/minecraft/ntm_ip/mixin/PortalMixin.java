package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.api.PortalImpl;
import mian.minecraft.ntm_ip.misc.Portals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.tardis.mod.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.portal.Portal;

import java.util.List;
import java.util.UUID;

@Mixin(Portal.class)
public class PortalMixin implements PortalImpl {
    @Unique
    private UUID ntm_immersive_portals$tardisID;
    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci){
        Portal portal = (Portal) (Object) this;
        if(!portal.level().isClientSide){
            Level destWorld = portal.getDestWorld();
            Level origWorld = portal.getOriginWorld();
            if(destWorld.dimensionTypeId() == DimensionTypes.TARDIS_TYPE || origWorld.dimensionTypeId() == DimensionTypes.TARDIS_TYPE){
                if(this.ntm_immersive_portals$getTardisID() == null){
                    portal.kill();
                } else if(Portals.tardisToPortals.get(this.ntm_immersive_portals$getTardisID()) == null){ // We can confirm this was a portal from last save, so create new portals!
                    portal.kill();
                } else {
                    List<Portal> list = Portals.tardisToPortals.get(this.ntm_immersive_portals$getTardisID());

                    if(!list.contains(portal)){
                        portal.kill();
                    }
                }
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lqouteall/q_misc_util/my_util/SignalBiArged;emit(Ljava/lang/Object;Ljava/lang/Object;)V", shift = At.Shift.AFTER, remap = false))
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci){
        if(this.ntm_immersive_portals$getTardisID() != null){
            compoundTag.putUUID("tardis_id", this.ntm_immersive_portals$getTardisID());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lqouteall/q_misc_util/my_util/SignalBiArged;emit(Ljava/lang/Object;Ljava/lang/Object;)V", shift = At.Shift.AFTER, remap = false))
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci){
        if(compoundTag.contains("tardis_id")){
            this.ntm_immersive_portals$setTardisID(compoundTag.getUUID("tardis_id"));
        }
    }

    @Override
    public void ntm_immersive_portals$setTardisID(UUID tardisID){
        this.ntm_immersive_portals$tardisID = tardisID;
    }

    @Override
    public UUID ntm_immersive_portals$getTardisID() {
        return this.ntm_immersive_portals$tardisID;
    }
}
