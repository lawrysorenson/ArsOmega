package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Iterator;
import java.util.List;

public class RitualFlowingTime extends AbstractRitual {

    int range = 4;
    int tickAmount = 40;
    long activationDelay = 4L;//pretty fast cause small range and weak effects
    int sourceCost = 25;//pretty low cause it activates a lot, this still makes it expensive

    protected void tick() {


        Level world = this.getWorld();
        BlockPos pos = this.getPos();
        if (this.getWorld().isClientSide) {
            ParticleUtil.spawnRitualAreaEffect(this.getPos(), this.getWorld(), this.rand, this.getOuterColor(), range, 24, 5);
        } else {
            if (this.getWorld().getGameTime() % activationDelay != 0L)
            {
                return;
            }

            boolean didWorkOnce;
            Iterator var5;

            didWorkOnce = false;
            //slightly higher for tall machines like potion stuff
            var5 = BlockPos.betweenClosed(pos.offset(range, -1, range), pos.offset(-range, 3, -range)).iterator();

            while (var5.hasNext()) {
                BlockPos b = (BlockPos) var5.next();
                if (b!=this.getPos() && TickBlock(b, (ServerLevel) world,tickAmount)) {
                    didWorkOnce = true;
                }
            }

            if (didWorkOnce) {
                this.setNeedsSource(true);
            }
        }

    }

    public boolean TickBlock(BlockPos pos, ServerLevel world, int amount){
        BlockEntity tile = world.getBlockEntity(pos);
        //we blacklist ritual braziers cause it causes too many issues
        if(tile!=null && !(tile instanceof RitualBrazierTile)) {
            for (int i = 0; i < amount; i++) {
                BlockState state = tile.getBlockState();
                BlockEntityTicker<BlockEntity> blockentityticker = state.getTicker(world, (BlockEntityType<BlockEntity>) tile.getType());
                if (blockentityticker != null) {
                    blockentityticker.tick(world, pos, state, tile);
                }
                return true;
            }
        }

        return false;
    }


    public int getSourceCost() {
        return sourceCost;
    }

    public boolean canConsumeItem(ItemStack stack) {
        return false;
    }
    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName("flowing_time");
    }

    public ParticleColor getCenterColor() {
        return new ParticleColor(255,255,255);
    }
}

