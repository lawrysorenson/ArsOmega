package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.items.ModularStaff;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemBreakMixin {
    @Shadow public abstract void setTag(@Nullable CompoundTag p_41752_);

    @Inject(method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
    at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"), remap = true)
    @Coerce <T extends LivingEntity> void hurtAndBreak(int amount, @Coerce T living, @Coerce Consumer<T> p_41625_, CallbackInfo ci){
        ItemStack stack = (ItemStack) (Object) this;

        if(stack.getItem() instanceof ModularStaff staff){
            Player p = living instanceof Player ? (Player) living : null;
            staff.dropAllContents(stack, living.getLevel(), living.getOnPos(), p);
        }
    }

}
