package mian.minecraft.ntm_ip.registry;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.helper.Constants;
import mian.minecraft.ntm_ip.registry.portal_types.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.exterior.ExteriorType;
import net.tardis.mod.registry.ExteriorRegistry;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class PortalDimensionRegistry {
    public static final DeferredRegister<PortalDimensionType>
            DIMENSION_TYPES = DeferredRegister.create(Constants.PORTAL_REGISTRY, NTMIP.MODID);

    public static final Supplier<IForgeRegistry<PortalDimensionType>> REGISTRY =
            DIMENSION_TYPES.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<PortalDimensionType> CHAMELEON =
            registerType(ExteriorRegistry.CHAMELEON, ChameleonType::new);

    public static final RegistryObject<PortalDimensionType> STEAM =
            registerType(ExteriorRegistry.STEAM, SteamType::new);

    public static final RegistryObject<PortalDimensionType> CAPSULE =
            registerType(ExteriorRegistry.TT_CAPSULE, CapsuleType::new);

    public static final RegistryObject<PortalDimensionType> COFFIN =
            registerType(ExteriorRegistry.COFFIN, CoffinType::new);

//    public static final RegistryObject<PortalDimensionType> IMPALA =
//            registerType(ExteriorRegistry.IMPALA, ImpalaType::new);

    public static final RegistryObject<PortalDimensionType> OCTA =
            registerType(ExteriorRegistry.OCTA, OctaType::new);

    public static final RegistryObject<PortalDimensionType> POLICE_BOX =
            registerType(ExteriorRegistry.POLICE_BOX, PoliceBoxType::new);

    public static final RegistryObject<PortalDimensionType> SPRUCE =
            registerType(ExteriorRegistry.SPRUCE, SpruceType::new);

    public static final RegistryObject<PortalDimensionType> TRUNK =
            registerType(ExteriorRegistry.TRUNK, TrunkType::new);

    public static Optional<PortalDimensionType> getDimensionTypeFromTardis(ITardisLevel tardis){
        return REGISTRY.get().getEntries().stream()
                .filter(type -> type.getKey().location().getPath().equals(
                        ExteriorRegistry.REGISTRY.get().getKey(tardis.getExterior().getType()).getPath()))
                .map(Map.Entry::getValue).findFirst();
    }

    public static <T extends PortalDimensionType> RegistryObject<T> registerType(RegistryObject<ExteriorType> type, Supplier<T> portalSupplier){
        return DIMENSION_TYPES.register(type.getId().getPath(), portalSupplier);
    }

    public static void register(IEventBus bus){
        NTMIP.LOGGER.info("Registering portal dimension types");
        DIMENSION_TYPES.register(bus);
    }
}
