package com.dkmk100.arsomega.tools;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum BasicItemTier implements Tier {
    ModularStaff1(2, 512, 8.0f, 0F, 12, () -> {return Ingredient.of(Items.IRON_INGOT);}),
    ModularStaff2(3, 1024, 8.0f, 1F, 14, () -> {return Ingredient.of(Items.DIAMOND);}),
    ModularStaff3(4, 1536, 8.0f, 2F, 15, () -> {return Ingredient.of(Items.NETHERITE_INGOT);}),
    ModularStaff4(4, 2048, 8.0f, 3F, 16, () -> {return Ingredient.of(Items.NETHERITE_INGOT);}),

    Staff(3, -1, 8.0f, 4F, 10, () -> {return Ingredient.of(ItemsRegistry.SOURCE_GEM);}),
    Staff2(4, -1, 9.0f, 5F, 12, () -> {return Ingredient.of(Items.NETHERITE_INGOT);}),
    Staff3(4, -1, 10.0f, 6F, 16, () -> {return Ingredient.of(Items.NETHER_STAR);})
    ;
    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairMaterial;

    BasicItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial){
        this.harvestLevel=harvestLevel;
        this.maxUses=maxUses;
        this.efficiency=efficiency;
        this.attackDamage=attackDamage;
        this.enchantability=enchantability;
        this.repairMaterial=repairMaterial;
    }

    @Override
    public int getUses() {
        return maxUses;
    }

    @Override
    public float getSpeed() {
        return efficiency;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamage;
    }

    @Override
    public int getLevel() {
        return harvestLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }
}
