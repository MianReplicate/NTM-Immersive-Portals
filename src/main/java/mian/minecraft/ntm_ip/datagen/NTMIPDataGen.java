package mian.minecraft.ntm_ip.datagen;

import mian.minecraft.ntm_ip.NTMIP;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = NTMIP.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class NTMIPDataGen {

    @SubscribeEvent
    public static void registerDataGen(GatherDataEvent event){
        event.getGenerator().addProvider(event.includeClient(), new NTMIPLangProvider(event.getGenerator()));
    }
}
