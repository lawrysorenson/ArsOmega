package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public class DemonicCleanse extends TierFourEffect{

    public static DemonicCleanse INSTANCE = new DemonicCleanse("demonic_cleanse","Demonic Cleanse");

    public DemonicCleanse(String tag, String description) {
        super(tag, description);
    }

    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (rayTraceResult.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity)rayTraceResult.getEntity();
            if(entity.hasEffect(ModPotions.DEMONIC_CLEANSE)){
                return;
            }

            Collection<MobEffectInstance> effects = entity.getActiveEffects();
            MobEffectInstance[] array = (MobEffectInstance[])effects.toArray(new MobEffectInstance[0]);
            MobEffectInstance[] var9 = array;
            int var10 = array.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                MobEffectInstance e = var9[var11];
                if (e.isCurativeItem(new ItemStack(Items.MILK_BUCKET))||e.isCurativeItem(new ItemStack(ItemsRegistry.CLEANSING_GEM))||e.getEffect() == com.hollingsworth.arsnouveau.common.potions.ModPotions.SUMMONING_SICKNESS) {
                    entity.removeEffect(e.getEffect());
                }
            }

            entity.addEffect(new MobEffectInstance(ModPotions.DEMONIC_CLEANSE,1200));
        }

    }

    @Override
    public int getDefaultManaCost() {
        return 800;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}