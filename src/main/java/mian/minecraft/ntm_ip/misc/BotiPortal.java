package mian.minecraft.ntm_ip.misc;

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
    private static final EntityDataAccessor<Optional<UUID>> TARDIS_ID = SynchedEntityData.defineId(BotiPortal.class, EntityDataSerializers.OPTIONAL_UUID);
    private boolean valid = false;
    private boolean isInterior = false;

    public BotiPortal(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(TARDIS_ID, Optional.of(UUID.randomUUID()));
    }

    public UUID getTardisId(){
        return this.entityData.get(TARDIS_ID).get();
    }

    public void setTardisId(UUID uuid){
        this.entityData.set(TARDIS_ID, Optional.of(uuid));
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
            if(portalList.stream().filter(portal -> portal.getUUID() == this.getUUID()).findAny().isEmpty()
            && !this.level().isClientSide)
                return false;

            if(!getValid())
                return false;
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
