package com.dkmk100.arsomega.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class CustomClay extends Block {
    Supplier<CarvedClay> carvedVariant;
    public CustomClay(Properties p_49795_, Supplier<CarvedClay> carved) {
        super(p_49795_);
        carvedVariant = carved;
    }



    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.canPerformAction(net.minecraftforge.common.ToolActions.SHEARS_CARVE)) {
            if (!level.isClientSide) {
                Direction dir = hit.getDirection();
                dir = dir.getAxis() == Direction.Axis.Y ? player.getDirection().getOpposite() : dir;

                //TODO: decide if this is the best sound
                level.playSound((Player)null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlock(pos, carvedVariant.get().defaultBlockState().setValue(CarvedClay.FACING, dir), 11);

                itemstack.hurtAndBreak(1, player, (p) -> {
                    p.broadcastBreakEvent(hand);
                });

                level.gameEvent(player, GameEvent.SHEAR, pos);
                player.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.use(state, level, pos, player, hand, hit);
        }
    }
}
