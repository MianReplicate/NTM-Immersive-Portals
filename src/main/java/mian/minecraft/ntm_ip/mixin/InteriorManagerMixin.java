package mian.minecraft.ntm_ip.mixin;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.api.Portalable;
import mian.minecraft.ntm_ip.helper.PortalHelper;
import mian.minecraft.ntm_ip.misc.BotiPortal;
import mian.minecraft.ntm_ip.misc.Portals;
import mian.minecraft.ntm_ip.registry.EntityTypeRegistry;
import mian.minecraft.ntm_ip.registry.ExteriorDimensionRegistry;
import mian.minecraft.ntm_ip.registry.ExteriorDimensionType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.tardis.mod.blockentities.exteriors.ExteriorTile;
import net.tardis.mod.cap.level.ITardisLevel;
import net.tardis.mod.helpers.WorldHelper;
import net.tardis.mod.misc.DoorHandler;
import net.tardis.mod.misc.tardis.InteriorDoorData;
import net.tardis.mod.misc.tardis.InteriorManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.util.tuples.Pair;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mixin(InteriorManager.class)
public abstract class InteriorManagerMixin implements Portalable {
    @Shadow @Final private ITardisLevel tardis;

    @Shadow public abstract DoorHandler getDoorHandler();
//
//    @Unique
//    private UUID ntm_immersive_portals$exterior;
//    @Unique
//    private UUID ntm_immersive_portals$interior;

    @Unique
    public UUID ntm_immersive_portals$getTardisID(){
        return UUID.fromString(tardis.getLevel().dimension().location().getPath());
    }

    @Redirect(method = "getMainInteriorDoor", at = @At(value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;log(Lorg/apache/logging/log4j/Level;Ljava/lang/String;)V"))
    public void getMainInteriorDoor(Logger instance, org.apache.logging.log4j.Level level, String s){
        // shut the fuck up
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void ntm_immersive_portals$tick(CallbackInfo ci){
        if(!tardis.isClient()){
            BlockEntity entity = ((ServerLevel)tardis.getLevel()).getServer().getLevel(tardis.getLocation().getLevel()).getBlockEntity(tardis.getLocation().getPos());

            if(entity instanceof ExteriorTile exteriorTile){
                boolean shouldTeleport = getDoorHandler().getDoorState().isOpen();

                if (shouldTeleport) {
                    UUID tardisID = ntm_immersive_portals$getTardisID();
//                    BotiPortal interior = ntm_immersive_portals$createOrEditInterior(tardis, tardisID);
                    BotiPortal exterior = ntm_immersive_portals$createOrEditExterior(tardis, exteriorTile, tardisID);

//                    PortalHelper.adjustPortalsToConnectAndSync(interior, exterior);
                } else {
                    ntm_immersive_portals$removePortals();
                }
            }
        }
    }

    @Unique
    public BotiPortal ntm_immersive_portals$createOrEditExterior(ITardisLevel tardis, ExteriorTile exteriorTile, UUID tardisID){
        ServerLevel level = (ServerLevel) exteriorTile.getLevel();
        BotiPortal portal = ntm_immersive_portals$getExterior();

//        Vec3 origin = exteriorTile.getBlockPos().getCenter();
//        InteriorDoorData doorData = tardis.getInteriorManager().getMainInteriorDoor();
//        if(tardis.getInteriorManager().getMainDoorID().isEmpty())
//            tardis.getInteriorManager().setMainDoor(((PublicDoorData) doorData).ntm_immersive_portals$getUUID());
//        Vec3 dest = tardis.getInteriorManager().getMainInteriorDoor().getPosition(tardis.getLevel());
//
//        float y = WorldHelper.getHorizontalFacing(exteriorTile.getBlockState()).toYRot();
//
//        ExteriorDimensionType dimensionType = ExteriorDimensionRegistry.getDimensionType(tardis);
//        Pair<Vec3, Vec3> positions = dimensionType.getPositions(tardis);
//        origin = origin.add(positions.getA());
//        dest = dest.add(positions.getB());
//        Direction intDir = Direction.fromYRot(tardis.getInteriorManager().getMainInteriorDoor().getRotation(tardis));
//        dest = dest.relative(intDir, -2);
//
//        Direction extDir = WorldHelper.getHorizontalFacing(exteriorTile.getBlockState());
//        origin = origin.relative(extDir, 0.75);


        if(portal != null && !level.dimension().equals(portal.getOriginDim())) {
            NTMIP.LOGGER.info("Killed exterior because levels aren't equal!\nExpected: "+level+"\nGot:"+portal.getOriginWorld());
            PortalHelper.removePortal((ServerLevel) portal.getOriginWorld(), portal.getUUID());
        }

        // interior needed
        BotiPortal interior = ntm_immersive_portals$createOrEditInterior(tardis, tardisID);
        ResourceKey<Level> targetDim = tardis.getId();
        if(portal == null){
            portal = PortalManipulation.createReversePortal(
                    interior,
                    EntityTypeRegistry.BOTI_PORTAL.get()
            );
//            portal = PortalHelper.createPortal(
//                    level,
//                    origin,
//                    dest, // maybe work??,
//                    targetDim,
//                    DQuaternion.rotationByDegrees(new Vec3(0, -1, 0), y),
//                    sizes.getA().getA(),
//                    sizes.getA().getB()
//            );
            portal.setValid(true);
            portal.setInteractable(false);
            portal.animation.defaultAnimation.durationTicks = 0;
            portal.setIsVisible(true);
            portal.teleportable = true;

            Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID()).add(portal);
            portal.setTardisId(tardisID);
            McHelper.spawnServerEntity(portal);

            NTMIP.LOGGER.info("Created exterior portal!");
            NTMIP.LOGGER.info("Origin:"+ portal.getOriginPos().toString());
            NTMIP.LOGGER.info("Dest:"+ portal.getDestPos().toString());
            NTMIP.LOGGER.info("Origin Dim:"+ level.toString());
            NTMIP.LOGGER.info("Dest Dim:"+ targetDim.toString());
        }

        if(!interior.getDestPos().equals(portal.getOriginPos())
                || !interior.getOriginPos().equals(portal.getDestPos())
                || !interior.getOriginDim().equals(portal.getDestDim())){
//            NTMIP.LOGGER.info("Origin:"+ origin.toString());
//            NTMIP.LOGGER.info("Dest:"+ dest.toString());
//            NTMIP.LOGGER.info("Origin Dim:"+ level.toString());
//            NTMIP.LOGGER.info("Dest Dim:"+ targetDim.toString());

            portal.setOriginPos(interior.getDestPos());
            portal.setPos(interior.getOriginPos());
            portal.setDestinationDimension(tardis.getLocation().getLevel());
            portal.reloadAndSyncToClient();
        }
        return portal;
    }

    @Unique
    public BotiPortal ntm_immersive_portals$createOrEditInterior(ITardisLevel tardis, UUID tardisID){
        ServerLevel level = (ServerLevel) tardis.getLevel();
        BotiPortal portal = ntm_immersive_portals$getInterior();

        InteriorDoorData door = tardis.getInteriorManager().getMainInteriorDoor();
        Vec3 origin = door.getPosition(level);
        Vec3 dest = tardis.getLocation().getPos().getCenter();

        float y = WorldHelper.getHorizontalFacing(level.getBlockState(BlockPos.containing(door.getPosition(level))))
                .toYRot();

        ExteriorDimensionType dimensionType = ExteriorDimensionRegistry.getDimensionType(tardis);
        Pair<Vec3, Vec3> positions = dimensionType.getPositions(tardis);
        origin = origin.add(positions.getB());
        dest = dest.add(positions.getA());

//        Direction extDir = WorldHelper.getHorizontalFacing(exteriorTile.getBlockState());
//        dest = dest.relative(extDir, 0.75);

        if(portal != null && !level.dimension().equals(portal.getOriginDim())) {
            NTMIP.LOGGER.info("Killed interior because levels aren't equal!\nExpected: "+level+"\nGot:"+portal.getOriginWorld());
            PortalHelper.removePortal((ServerLevel) portal.getOriginWorld(), portal.getUUID());
        }

        ResourceKey<Level> targetDim = tardis.getLocation().getLevel();
        if(portal == null){
            Pair<Pair<Double, Double>, Pair<Double, Double>> sizes = dimensionType.getSizes(tardis);

            portal = PortalHelper.createPortal(
                    level,
                    origin,
                    dest, // maybe work??,
                    targetDim,
                    DQuaternion.rotationByDegrees(new Vec3(0, -1, 0), y),
                    sizes.getB().getA(),
                    sizes.getB().getB()
            );
            portal.setValid(true);

            DQuaternion flip = DQuaternion.rotationByDegrees(portal.axisH, 180.0F);
            portal.setRotation(flip);

            portal.setInteractable(false);
            portal.animation.defaultAnimation.durationTicks = 0;
            portal.setIsVisible(true);
            portal.teleportable = true;

            Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID()).add(portal);
            portal.setTardisId(tardisID);
            McHelper.spawnServerEntity(portal);

            NTMIP.LOGGER.info("Created interior portal!");
            NTMIP.LOGGER.info("Origin:"+ origin.toString());
            NTMIP.LOGGER.info("Dest:"+ dest.toString());
            NTMIP.LOGGER.info("Origin Dim:"+ level.toString());
            NTMIP.LOGGER.info("Dest Dim:"+ targetDim.toString());
        }

        if(!origin.equals(portal.getOriginPos())
                || !dest.equals(portal.getDestPos())
                || !targetDim.equals(portal.getDestDim())){
            NTMIP.LOGGER.info("Origin:"+ origin.toString());
            NTMIP.LOGGER.info("Dest:"+ dest.toString());
            NTMIP.LOGGER.info("Origin Dim:"+ level.toString());
            NTMIP.LOGGER.info("Dest Dim:"+ targetDim.toString());

            portal.setOriginPos(origin);
            portal.setPos(dest);
            portal.setDestinationDimension(tardis.getLocation().getLevel());
            portal.reloadAndSyncToClient();
        }
        return portal;
    }

    @Override
    public void ntm_immersive_portals$removePortals() {
        List<BotiPortal> portals = Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID());
        for(int i = 0; i < portals.size(); i++){
            BotiPortal portal = portals.get(i);
            portal.setValid(false);
            portals.remove(portal);
            i--;
        }
    }

    @Override
    public BotiPortal ntm_immersive_portals$getExterior() {
        List<BotiPortal> portals = Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID());

        return portals.stream().filter(filtering -> !filtering.getOriginWorld().equals(tardis.getLevel())).findFirst().orElse(null);
    }
    @Override
    public BotiPortal ntm_immersive_portals$getInterior() {
        List<BotiPortal> portals = Portals.getPortalsForTardis(ntm_immersive_portals$getTardisID());

        return portals.stream().filter(filtering -> filtering.getOriginWorld().equals(tardis.getLevel())).findFirst().orElse(null);
    }

    @Unique
    private static Level ntm_immersive_portals$getInteriorLevel(ITardisLevel tardis){
        return tardis.getLevel();
    }

    @Unique
    private static Level ntm_immersive_portals$getExteriorLevel(ITardisLevel tardis){
        return ((ServerLevel) tardis.getLevel()).getServer().getLevel(tardis.getLocation().getLevel());
    }
}
