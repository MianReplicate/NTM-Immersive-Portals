package mian.minecraft.ntm_ip.event;

import mian.minecraft.ntm_ip.misc.Portals;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tardis.api.events.TardisEvent;
import net.tardis.mod.cap.Capabilities;

public class ForgeEvents {


    // Pre events are not called because you can't cancel portal teleports from what I know
    @SubscribeEvent
    public static void playerDimChange(PlayerEvent.PlayerChangedDimensionEvent event){
        ServerLevel to = event.getEntity().getServer().getLevel(event.getTo());
        if(to != null){
            to.getCapability(Capabilities.TARDIS).ifPresent(tardis -> {
                if(Portals.getPortalsForTardis(tardis).stream().anyMatch(portal ->
                        !portal.getOriginWorld().dimension().equals(tardis.getId()))){
                    MinecraftForge.EVENT_BUS.post(new TardisEvent.EnterEvent.Post(tardis, event.getEntity()));
                }
            });
        }
    }
}
