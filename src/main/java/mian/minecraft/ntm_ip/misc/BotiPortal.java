package mian.minecraft.ntm_ip.misc;

import mian.minecraft.ntm_ip.NTMIP;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import qouteall.imm_ptl.core.portal.Portal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BotiPortal extends Portal {
    private UUID tardis = null;
    private boolean valid = false;
    private boolean isInterior = false;

    public BotiPortal(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    public UUID getTardisId(){
        return tardis;
    }

    public void setTardisId(UUID uuid){
        tardis = uuid;
    }

    public void setIsInterior(boolean isInterior){
        this.isInterior = isInterior;
    }

    public boolean getIsInterior(){
        return isInterior;
    }

    public void setValid(boolean valid){
        this.valid = valid;
    }

    public boolean getValid(){
        return valid;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean isPortalValid() {
        UUID tardisId = getTardisId();

        if (level() instanceof ServerLevel) {
            List<BotiPortal> portalList = Portals.getPortalsForTardis(tardisId);
//            NTMIP.LOGGER.info(String.valueOf(portalList.size()));
            if(portalList.stream().filter(portal -> portal.getUUID() == this.getUUID()).findAny().isEmpty()
            && !this.level().isClientSide)
                return false;

            if(!getValid()) {
                portalList.remove(this);
                return false;
            }
        }

        return super.isPortalValid();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        UUID tardisId = getTardisId();
        if (tardisId != null) {
            compoundTag.putUUID("tardis_id", tardisId);
        }
        compoundTag.putBoolean("valid", getValid());
        compoundTag.putBoolean("is_interior", getIsInterior());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("tardis_id")) {
            setTardisId(compoundTag.getUUID("tardis_id"));
        }
        setValid(compoundTag.getBoolean("valid"));
        setIsInterior(compoundTag.getBoolean("is_interior"));
    }
}
