package com.dkmk100.arsomega.items;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;

import java.util.List;

/**
 * An interface for items that can be inserted in the modular staff
 * Specifically the crystal and the upgrades
 */
public interface IStaffComponent {

    Spell modifySpell(Spell spell, ItemStack component, ItemStack staffStack, ModularStaff staffItem);

    void addBonusesTooltip(ItemStack component, @javax.annotation.Nullable Level worldIn, List<Component> tooltip);

    //by default just use the full tooltip lol
    //but can be overridden
    default void addShortTooltip(ItemStack component, @javax.annotation.Nullable Level worldIn, List<Component> tooltip){
        addBonusesTooltip(component, worldIn, tooltip);
    }
    ModularStaff.StaffComponentType getType(ItemStack component);

    default double getDamageBonus(EquipmentSlot slot, ItemStack stack){
        return 0;
    }

    default Item getItem(){
        return (Item) this;
    }

    static @Nullable IStaffComponent getStaffComponent(ItemStack stack){
        Item item = stack.getItem();
        if(item instanceof IStaffComponent component){
            return component;
        }
        return null;
    }
}
