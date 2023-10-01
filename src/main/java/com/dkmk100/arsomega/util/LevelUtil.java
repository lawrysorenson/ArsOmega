package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Function;

public class LevelUtil {

    public static void sendToNearby(Level world, BlockPos pos, Component message, int rangeSide, int rangeUp, boolean noSpam){
        AABB box = new AABB(pos).inflate(rangeSide, rangeUp, rangeSide);
        List<Player> players = world.getNearbyPlayers(TargetingConditions.forNonCombat(), null, box);
        for(Player player : players){
            if(noSpam){
                PortUtil.sendMessageNoSpam(player,message);
            }
            else{
                PortUtil.sendMessage(player,message);
            }
        }
    }

    public static BlockPos getPosInWorld(ServerLevel newWorld, BlockPos oldPos, ServerLevel oldWorld){
        DimensionType oldType = oldWorld.dimensionType();
        DimensionType newType = newWorld.dimensionType();
        float coordRatio = (float) DimensionType.getTeleportationScale(oldWorld.dimensionType(),newWorld.dimensionType());
        int x = Math.round(oldPos.getX() * coordRatio);
        int z = Math.round(oldPos.getZ() * coordRatio);
        float heightInOld = (oldPos.getY() - oldType.minY()) / ((float) oldType.height());
        if(heightInOld < 0.1f){
            heightInOld = 0.1f;
        }
        if(heightInOld > 0.9f){
            heightInOld = 0.9f;
        }

        int y = Math.round((newType.height() * heightInOld) + newType.minY());
        if(oldWorld.dimension().location().equals(RegistryHandler.DIMTYPE)){
            if(y<45){
                y = 45;
            }
        }

        return new BlockPos(x,y,z);
    }

    public static void teleportEntity(Entity entity, BlockPos destPos, ServerLevel destinationWorld, ServerLevel originalWorld) {

        // makes sure chunk is made
        destinationWorld.getChunk(destPos);

        if (entity instanceof Player) {
            ((ServerPlayer) entity).teleportTo(
                    destinationWorld,
                    destPos.getX() + 0.5D,
                    destPos.getY() + 1D,
                    destPos.getZ() + 0.5D,
                    entity.getRotationVector().y,
                    entity.getRotationVector().x);
        }
        else {
            Entity entity2 = EntityType.loadEntityRecursive(entity.serializeNBT(),destinationWorld, Function.identity());
            if (entity2 != null) {
                entity2.setPos(destPos.getX(),destPos.getY(),destPos.getZ());
                entity2.setDeltaMovement(entity.getDeltaMovement());
                destinationWorld.addDuringTeleport(entity2);
            }
            entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
        }
    }
}
