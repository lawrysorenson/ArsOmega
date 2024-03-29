package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.util.StatsModifier;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BasicStaffUpgrade extends Item implements IStaffUpgrade {

    StatsModifier modifier;
    int maxCount;
    int bonusDamage;

    public BasicStaffUpgrade(Properties properties, StatsModifier modifier, int maxCount, int bonusDamage) {
        super(properties);
        this.modifier = modifier;
        this.maxCount = maxCount;
        this.bonusDamage = bonusDamage;
    }

    @Override
    public Spell modifySpell(Spell spell, ItemStack component, ItemStack staffStack, ModularStaff staffItem) {
        return modifier.ModifySpell(spell);
    }

    @Override
    public void addBonusesTooltip(ItemStack component, @Nullable Level worldIn, List<Component> tooltip) {
        modifier.addTooltip(tooltip);
        if(bonusDamage != 0){
            tooltip.add(Component.literal("Bonus melee damage: "+bonusDamage));
        }
    }

    @Override
    public void addShortTooltip(ItemStack component, @Nullable Level worldIn, List<Component> tooltip) {
        tooltip.add(component.getDisplayName());
    }

    @Override
    public int getMaxCount() {
        return maxCount;
    }
}
