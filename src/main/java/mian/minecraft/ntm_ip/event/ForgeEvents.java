package mian.minecraft.ntm_ip.event;

import mian.minecraft.ntm_ip.helper.NTMIPHelper;
import mian.minecraft.ntm_ip.misc.BotiPortal;
import mian.minecraft.ntm_ip.misc.NTMIPPortals;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tardis.mod.cap.Capabilities;

public class ForgeEvents {


    // Pre events are not called unfortunately
    @SubscribeEvent
    public static void playerDimChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerLevel to = event.getEntity().getServer().getLevel(event.getTo());
        ServerLevel from = event.getEntity().getServer().getLevel(event.getFrom());
        if (to != null) {
            to.getCapability(Capabilities.TARDIS).ifPresent(tardis -> {
                if (NTMIPPortals.getPortalsForTardis(tardis).stream().anyMatch(portal -> !portal.getIsInterior())) {
                    // since pre isn't called for portals
                    NTMIPHelper.teleportFollowers(tardis, event.getEntity(), from, tardis.getLocation().getPos());
                }
            });
        }

        if (from != null) {
            from.getCapability(Capabilities.TARDIS).ifPresent(tardis ->
                    NTMIPPortals.getPortalsForTardis(tardis).stream().filter(BotiPortal::getIsInterior).findFirst().ifPresent(portal ->
                            NTMIPHelper.teleportFollowers(tardis, event.getEntity(), from, portal.getOnPos())));
        }
    }
}
