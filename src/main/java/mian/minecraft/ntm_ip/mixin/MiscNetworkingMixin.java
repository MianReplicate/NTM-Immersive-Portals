package mian.minecraft.ntm_ip.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.MiscHelper;
import qouteall.q_misc_util.MiscNetworking;
import qouteall.q_misc_util.dimension.DimensionIdRecord;
import qouteall.q_misc_util.dimension.DimensionTypeSync;
import qouteall.q_misc_util.forge.events.ClientDimensionUpdateEvent;
import qouteall.q_misc_util.mixin.client.IEClientPacketListener_Misc;

import java.util.Set;

@Mixin(MiscNetworking.class)
public class MiscNetworkingMixin {
    /**
     * @author MianReplicate (NTM IP)
     * @reason this is kinda hacky but i can't really find another way for immersive portals to stop crashing
     */
    @OnlyIn(Dist.CLIENT)
    @Overwrite(remap = false)
    private static void processDimSync(FriendlyByteBuf buf, ClientGamePacketListener packetListener) {
        CompoundTag idMap = buf.readNbt();
        DimensionIdRecord.clientRecord = DimensionIdRecord.tagToRecord(idMap);
        CompoundTag typeMap = buf.readNbt();
        MiscHelper.executeOnRenderThread(() -> {
            DimensionIdRecord.clientRecord = DimensionIdRecord.tagToRecord(idMap);
            DimensionTypeSync.acceptTypeMapData(typeMap);
            Helper.log("Received Dimension Int Id Sync");
            Helper.log("\n" + DimensionIdRecord.clientRecord);
            Set<ResourceKey<Level>> dimIdSet = DimensionIdRecord.clientRecord.getDimIdSet();
            ((IEClientPacketListener_Misc) packetListener).ip_setLevels(dimIdSet);
            MinecraftForge.EVENT_BUS.post(new ClientDimensionUpdateEvent(dimIdSet));
        });
    }
}
