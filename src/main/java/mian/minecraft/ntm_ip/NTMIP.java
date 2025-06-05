package mian.minecraft.ntm_ip;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

//TODO: Current Bugs
// Dim IDs arent always being synced over properly? Client isnt getting all the ids ?
// Fix rotation and position of portals
// Portals dont get removed always (maybe need to save ids)

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NTMIP.MODID)
public class NTMIP {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "ntm_ip";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public NTMIP() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
}
