package mian.minecraft.ntm_ip.registry;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.ExteriorDataHandlerImpl;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.tardis.mod.helpers.Helper;
import net.tardis.mod.misc.tardis.montior.BasicToggleMonitorFunction;
import net.tardis.mod.misc.tardis.montior.MonitorFunction;

public class IPMonitorFunctionRegistry {
    public static final DeferredRegister<MonitorFunction> MONITOR_FUNCTIONS = DeferredRegister.create(Helper.createRL("monitor_functions"), NTMIP.MODID);

    public static final RegistryObject<BasicToggleMonitorFunction> ENABLE_BOTI = MONITOR_FUNCTIONS.register("exterior/enable_boti", () -> new BasicToggleMonitorFunction(
            (tardis, value) -> ((ExteriorDataHandlerImpl) tardis.getExteriorExtraData()).ntm_immersive_portals$setBOTIEnabled(value),
            tardis -> ((ExteriorDataHandlerImpl) tardis.getExteriorExtraData()).ntm_immersive_portals$isBOTIEnabled()));

    public static void register(IEventBus eventBus) {
        NTMIP.LOGGER.info("Registering monitor functions!");

        MONITOR_FUNCTIONS.register(eventBus);
    }
}
