package mian.minecraft.ntm_ip.helper;

import mian.minecraft.ntm_ip.NTMIP;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.helpers.WorldHelper;

public class Helper {
    public static ResourceLocation createRL(String path){
        return ResourceLocation.fromNamespaceAndPath(NTMIP.MODID, path);
    }

    public static void teleportFollowers(ITardisLevel tardis, Player player, Level from, BlockPos previousPos){
        if (!tardis.isClient()) {
            for(Entity entity : from
                    .getEntitiesOfClass(Entity.class, WorldHelper.getCenteredAABB(previousPos, 16.0F))) {
                if (entity instanceof OwnableEntity pet) {
                    if (player.getUUID().equals(pet.getOwnerUUID())) {
                        boolean var10000;
                        label26: {
                            if (entity instanceof TamableAnimal) {
                                TamableAnimal animal = (TamableAnimal)entity;
                                if (animal.isInSittingPose()) {
                                    var10000 = true;
                                    break label26;
                                }
                            }

                            var10000 = false;
                        }

                        boolean isSitting = var10000;
                        if (!isSitting) {
                            tardis.getExterior().getTeleportHandler()
                                    .teleportEntity((ServerLevel) player.level(), entity);
                        }
                    }
                }
            }
        }

    }
}
