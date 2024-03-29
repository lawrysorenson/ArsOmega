package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.client.renderer.ColoredItemRenderer;
import com.dkmk100.arsomega.util.ResourceUtil;
import com.dkmk100.arsomega.util.StatsModifier;
import com.google.common.collect.ImmutableMultimap;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;
import software.bernie.ars_nouveau.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class BasicStaffCrystal extends ModularStaffComponent implements DyeableLeatherItem, IAnimatable {
    StatsModifier modifier;
    float bleed;
    int color;
    int extraUgprades;
    public BasicStaffCrystal(Properties properties, StatsModifier modifier, int color, float colorBleed, int extraUgprades) {
        super(properties);
        this.modifier = modifier;
        this.color = color;
        this.bleed = colorBleed;
        this.extraUgprades = extraUgprades;
    }

    @Override
    public ModularStaff.StaffComponentType getType(ItemStack component){
        return ModularStaff.StaffComponentType.PART;
    }

    @Override
    public ModularStaff.StaffPart getPartType() {
        return ModularStaff.StaffPart.GEM;
    }

    @Override
    public ResourceLocation getModel(ItemStack crystal, ItemStack staffStack, ModularStaff staff){
        return ResourceUtil.getModelResource("staff_gem");
    }

    @Override
    public ResourceLocation getTexture(ItemStack component, ItemStack staffStack, ModularStaff staffItem) {
        return ResourceUtil.getItemTextureResource("staff_gem");
    }


    @Override
    public Color getColor(ItemStack crystal, ItemStack staffStack, ModularStaff staff){
        if(hasCustomColor(crystal)){
            return Color.ofOpaque(getColor(crystal));
        }
        else if(bleed > 0) {
            if(bleed > 1){
                bleed = 1;
            }

            ParticleColor color = staff.getSpellCaster(staffStack).getSpell().color;

            Color baseColor = Color.ofOpaque(getColor(crystal));

            float baseMult = bleed;
            float colorMult = 1f - baseMult;

            int r = Math.round(colorMult * color.getRedInt() + baseMult * baseColor.getRed());
            int g = Math.round(colorMult * color.getGreenInt() + baseMult * baseColor.getGreen());
            int b = Math.round(colorMult * color.getBlueInt() + baseMult * baseColor.getBlue());
            return Color.ofRGB(r,g,b);
        }
        else{
            ParticleColor color = staff.getSpellCaster(staffStack).getSpell().color;
            return Color.ofOpaque(color.getColor());
        }
    }



    @Override
    public Spell modifySpell(Spell spell, ItemStack crystal, ItemStack staffStack, ModularStaff staff) {
        StatsModifier modifier = new StatsModifier(this.modifier);

        return modifier.ModifySpell(spell);
    }

    @Override
    public void addBonusesTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> components) {
        this.modifier.addTooltip(components);
        if(extraUgprades > 0){
            if(extraUgprades == 1) {
                components.add(Component.literal("Adds 1 upgrade slot."));
            }
            else{
                components.add(Component.literal("Adds " + extraUgprades + "upgrade slots."));
            }
        }
    }

    @Override
    public void addExtraAttributes(EquipmentSlot slot, ItemStack stack, ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes) {

    }

    @Override
    public int getExtraUpgrades() {
        return extraUgprades;
    }

    @Override
    public int getColor(ItemStack p_41122_) {
        int defaultColor = this.color;
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : defaultColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        components.add(Component.literal("Can be dyed"));
    }

    public AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new ColoredItemRenderer<>("staff_gem");

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}
