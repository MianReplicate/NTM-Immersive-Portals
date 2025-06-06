package mian.minecraft.ntm_ip.registry;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.helper.Helper;
import mian.minecraft.ntm_ip.registry.custom.DefaultDimensionType;
import mian.minecraft.ntm_ip.registry.custom.SteamDimensionType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.registry.BlockRegistry;
import net.tardis.mod.registry.ExteriorRegistry;

import java.util.function.Supplier;

public class ExteriorDimensionRegistry {
    public static final DeferredRegister<ExteriorDimensionType>
            DIMENSION_TYPES = DeferredRegister.create(Helper.createRL("portal_dimensions"), NTMIP.MODID);

    public static final Supplier<IForgeRegistry<ExteriorDimensionType>> REGISTRY =
            DIMENSION_TYPES.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<ExteriorDimensionType> DEFAULT =
            DIMENSION_TYPES.register("default", DefaultDimensionType::new);

    public static final RegistryObject<ExteriorDimensionType> STEAM =
            DIMENSION_TYPES.register("steam", SteamDimensionType::new);

    public static ExteriorDimensionType getDimensionType(ITardisLevel tardis){
        return DIMENSION_TYPES.getEntries().stream()
                .filter(type -> {
                    return type.getId().getPath().equals(
                            ExteriorRegistry.REGISTRY.get().getKey(tardis.getExterior().getType()).getPath());
//                    BlockEntity entity = ((ServerLevel)tardis.getLevel()).getServer()
//                            .getLevel(tardis.getLocation().getLevel()).getBlockEntity(tardis.getLocation().getPos());
//                    if(entity != null && entity instanceof ExteriorTile tile){
//                        ResourceLocation location = ForgeRegistries.BLOCKS.getKey(tile.getBlockState().getBlock());
//
//                        return type.getId().getPath().equals(location.getPath());
//                    }
//                    return false;
                }).findFirst().orElse(DEFAULT).get();
    }

    public static void register(IEventBus bus){
        NTMIP.LOGGER.info("Registering exterior dimension types");
        DIMENSION_TYPES.register(bus);
    }
}
