package mian.minecraft.ntm_ip.registry;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.helper.Helper;
import mian.minecraft.ntm_ip.misc.BotiPortal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityTypeRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NTMIP.MODID);

    public static final RegistryObject<EntityType<BotiPortal>> BOTI_PORTAL = ENTITY_TYPES.register("boti_portal", () ->
            EntityType.Builder.of(BotiPortal::new, MobCategory.MISC).build(Helper.createRL("boti_portal").toString()));

    public static void register(IEventBus eventBus){
        NTMIP.LOGGER.info("Registering entity types");

        ENTITY_TYPES.register(eventBus);
    }
}
