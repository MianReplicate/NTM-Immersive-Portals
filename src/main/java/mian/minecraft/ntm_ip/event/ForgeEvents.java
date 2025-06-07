package mian.minecraft.ntm_ip.event;

import mian.minecraft.ntm_ip.helper.Helper;
import mian.minecraft.ntm_ip.misc.BotiPortal;
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
        ServerLevel from = event.getEntity().getServer().getLevel(event.getFrom());
        if(to != null){
            to.getCapability(Capabilities.TARDIS).ifPresent(tardis -> {
                if(Portals.getPortalsForTardis(tardis).stream().anyMatch(portal -> !portal.getIsInterior())){
                    MinecraftForge.EVENT_BUS.post(new TardisEvent.EnterEvent.Post(tardis, event.getEntity()));

                    // since pre isn't called for portals
                    Helper.teleportFollowers(tardis, event.getEntity(), from, tardis.getLocation().getPos());
                }
            });
        }

        if(from != null){
            from.getCapability(Capabilities.TARDIS).ifPresent(tardis ->
                    Portals.getPortalsForTardis(tardis).stream().filter(BotiPortal::getIsInterior).findFirst().ifPresent(portal ->
                    Helper.teleportFollowers(tardis, event.getEntity(), from, portal.getOnPos())));
        }
    }
}
