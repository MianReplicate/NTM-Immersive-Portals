package mian.minecraft.ntm_ip;

import com.mojang.logging.LogUtils;
import mian.minecraft.ntm_ip.registry.EntityTypeRegistry;
import mian.minecraft.ntm_ip.registry.ExteriorDimensionRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

//TODO: Current Bugs
// Dim IDs arent always being synced over properly? Client isnt getting all the ids ?
// Fix rotation and position of portals
// portal keeps recreating itself for some reason

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

        EntityTypeRegistry.register(modEventBus);
        ExteriorDimensionRegistry.register(modEventBus);
    }
}
