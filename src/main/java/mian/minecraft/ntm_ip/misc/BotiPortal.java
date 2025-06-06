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

    public BotiPortal(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    public UUID getTardisId(){
        return this.entityData.get(TARDIS_ID).get();
    }

    public void setTardisId(UUID uuid){
        this.entityData.set(TARDIS_ID, Optional.of(uuid));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean isPortalValid() {
        UUID tardisId = getTardisId();

        if (!this.level().isClientSide) {
            List<BotiPortal> portalList = Portals.tardisToPortals.get(tardisId);
            if(portalList == null ||
                    portalList.stream().filter(portal -> portal.getUUID() == this.getUUID()).findAny().isEmpty())
                return false;

            if(!this.getOriginWorld().isClientSide()) {
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
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("tardis_id")) {
            setTardisId(compoundTag.getUUID("tardis_id"));
        }
    }
}
