package mian.minecraft.ntm_ip;

import com.mojang.logging.LogUtils;
import mian.minecraft.ntm_ip.event.ForgeEvents;
import mian.minecraft.ntm_ip.misc.BotiPortalRenderer;
import mian.minecraft.ntm_ip.registry.IPEntityTypeRegistry;
import mian.minecraft.ntm_ip.registry.IPMonitorFunctionRegistry;
import mian.minecraft.ntm_ip.registry.PortalDimensionRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

//TODO: Current Bugs
// for some reason the car exterior jsut breaks removing the portal idk :sob:

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NTMIP.MODID)
public class NTMIP {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "ntm_ip";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public NTMIP() {
        LOGGER.info("About to immerse your ass!");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
        modEventBus.addListener(this::clientRenderers);

        IPEntityTypeRegistry.register(modEventBus);
        IPMonitorFunctionRegistry.register(modEventBus);
        PortalDimensionRegistry.register(modEventBus);
    }

    @OnlyIn(value= Dist.CLIENT)
    @SubscribeEvent
    public void clientRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(IPEntityTypeRegistry.BOTI_PORTAL.get(), BotiPortalRenderer::new);
    }
}
