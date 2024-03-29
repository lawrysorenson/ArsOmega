package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.util.ResourceUtil;
import com.google.common.collect.ImmutableMultimap;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ModularStaffComponent extends Item implements IStaffPart, IAnimatable, IStaffComponent {

    public ModularStaffComponent(Properties properties) {
        super(properties);
    }

    @Override
    public abstract ResourceLocation getModel(ItemStack crystal, ItemStack staffStack, ModularStaff staff);
    @Override
    public abstract Color getColor(ItemStack crystal, ItemStack staffStack, ModularStaff staff);

    @Override
    public abstract Spell modifySpell(Spell spell, ItemStack crystal, ItemStack staffStack, ModularStaff staff);

    @Override
    public abstract void addBonusesTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> components);


    @Override
    public abstract void addExtraAttributes(EquipmentSlot slot, ItemStack stack, ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes);

    @Override
    public boolean hasCustomColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99);
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        this.addBonusesTooltip(stack, level, components);
    }
}
