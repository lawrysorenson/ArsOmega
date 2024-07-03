package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class AdvancedAmplify extends AbstractAugment {

    public static AdvancedAmplify INSTANCE = new AdvancedAmplify();

    private AdvancedAmplify() {
        super(RegistryHandler.getGlyphName("advanced_amplify"), "Advanced Amplify");
    }

    @Override
    public int getDefaultManaCost() {
        return 1200;
    }

    @Override
    public SpellTier defaultTier() {
        return TierFourEffect.FOUR;
    }

    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addAmplification(4.0D); // make advanced applify even stronger, otherwise worse than amplify 2
        return super.applyModifiers(builder, spellPart);
    }

    @Override
    public String getBookDescription() {
        return "A more powerful version of amplify that is a lot less mana efficient";
    }
}
