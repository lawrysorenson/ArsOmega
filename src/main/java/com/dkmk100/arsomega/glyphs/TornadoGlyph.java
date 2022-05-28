package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityTornado;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.potions.SummoningSicknessEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class TornadoGlyph extends TierFourEffect{

    public static DamageSource TORNADO_DAMAGE = new DamageSource("tornado");

    public static TornadoGlyph INSTANCE = new TornadoGlyph("tornado","Tornado");

    public TornadoGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(!this.canSummon(shooter)){
            return;
        }
        EntityTornado tornado = new EntityTornado(world,shooter);
        Vec3 pos = rayTraceResult.getLocation();
        tornado.setColor(spellContext.colors);
        tornado.setPos(pos.x,pos.y + 0.5,pos.z);
        int ticks = 250 + (int)Math.round(70 * spellStats.getDurationMultiplier());
        tornado.setDuration(ticks);
        tornado.setAccelerate(spellStats.getBuffCount(AugmentAccelerate.INSTANCE));
        tornado.setAoe(spellStats.getBuffCount(AugmentAOE.INSTANCE));
        world.addFreshEntity(tornado);

        if(shooter!=null) {
            shooter.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS,ticks));
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 1000;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAOE.INSTANCE, AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAccelerate.INSTANCE});
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_AIR});
    }
}
