package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.blocks.PortalBlockEntity;
import com.dkmk100.arsomega.items.DimensionCrystal;
import com.dkmk100.arsomega.util.LevelUtil;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.List;

public class RitualOpenPortal extends AbstractRitual {

    public RitualOpenPortal(){
        super();
    }

    @Override
    protected void tick() {
        Logger logger = LogManager.getLogger();
        Level world = this.getWorld();
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 100; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(this.getCenterColor()), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }

        if (!world.isClientSide && world.getGameTime() % 10L == 0L) {
            if(this.needsSourceNow()){
                return;
            }
            else{
                this.setNeedsSource(true);
            }
            this.incrementProgress();
            if (this.getProgress() > 20) {
                BlockPos pos = this.getPos().above();

                ItemStack crystal = this.getConsumedItems().get(0);
                String targetDim = DimensionCrystal.getTargetDim(crystal);
                if(targetDim != null) {
                    if(world.dimension().location().toString().equals(targetDim)){
                        logger.error("Portal to same dimension tried to open at: "+pos.toShortString()+", dim: "+targetDim);
                        var players = world.getNearbyPlayers(TargetingConditions.forNonCombat(),null,new AABB(pos).inflate(12));
                        for (Player player : players){
                            PortUtil.sendMessage(player,"Error: cannot open portal from a dimension to itself!");
                            PortUtil.sendMessage(player,"target dim: "+targetDim);
                        }
                    }
                    else {
                        world.setBlockAndUpdate(pos, RegistryHandler.PORTAL_BLOCK.get().defaultBlockState());
                        BlockEntity tile = world.getBlockEntity(pos);
                        if (tile != null && tile instanceof PortalBlockEntity) {
                            ((PortalBlockEntity) tile).setTarget(targetDim);
                        }
                        else{
                            logger.error("Failed to set portal target at: "+pos.toShortString());
                            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                            LevelUtil.spawnAtLocation(new ItemStack(RegistryHandler.DIMENSION_CRYSTAL.get()),0.0f,pos,world);
                            var players = world.getNearbyPlayers(TargetingConditions.forNonCombat(),null,new AABB(pos).inflate(12));
                            for (Player player : players){
                                PortUtil.sendMessage(player,"ERROR: A bug occurred and the portal failed to open.");

                            }
                        }
                    }

                }
                else{
                    logger.error("Portal failed to open from invalid crystal at: "+pos.toShortString());
                    var players = world.getNearbyPlayers(TargetingConditions.forNonCombat(),null,new AABB(pos).inflate(12));
                    for (Player player : players){
                        PortUtil.sendMessage(player,"Error: cannot open portal, the provided crystal had an invalid dimension");
                    }
                }

                this.setFinished();
            }
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        int consumed = this.getConsumedItems().size();
        if(consumed==0){
            return stack.getItem()== RegistryHandler.DIMENSION_CRYSTAL.get();
        }
        else if(consumed==1){
            boolean scroll = stack.getItem() == com.hollingsworth.arsnouveau.setup.ItemsRegistry.WARP_SCROLL.get();
            return scroll && stack.hasTag() && WarpScroll.WarpScrollData.get(stack).getPos() != BlockPos.ZERO;
        }

        return false;
    }

    @Override
    public boolean canStart() {
        return this.getConsumedItems().size() > 0;
    }

    @Override
    public int getSourceCost() {
        return 5000;
    }

    @Override
    public boolean consumesSource() {
        return true;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName("open_portal");
    }
}
