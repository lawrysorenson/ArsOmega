package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.client.models.ColoredItemModel;
import com.dkmk100.arsomega.client.renderer.ColoredItemRenderer;
import com.dkmk100.arsomega.util.ResourceUtil;
import com.dkmk100.arsomega.util.StatsModifier;
import com.google.common.collect.ImmutableMultimap;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import java.util.UUID;
import java.util.function.Consumer;

public class BasicStaffHead extends ModularStaffComponent {
    StatsModifier modifier;
    float bonusMeleeDamage;
    String modelName;
    String textureName;


    public BasicStaffHead(Properties properties, String modelName, String textureName, float bonusMeleeDamage, StatsModifier modifier) {
        super(properties);
        this.modelName = modelName;
        this.textureName = textureName;
        this.bonusMeleeDamage = bonusMeleeDamage;
        this.modifier = modifier;
    }

    public BasicStaffHead(Properties properties, String modelName, String textureName) {
        this(properties, modelName, textureName, 0, new StatsModifier());
    }


    @Override
    public ModularStaff.StaffComponentType getType(ItemStack component) {
        return ModularStaff.StaffComponentType.PART;
    }

    @Override
    public ResourceLocation getTexture(ItemStack component, ItemStack staffStack, ModularStaff staffItem) {
        return ResourceUtil.getItemTextureResource("staff_head_"+textureName);
    }

    @Override
    public ModularStaff.StaffPart getPartType() {
        return ModularStaff.StaffPart.HEAD;
    }

    @Override
    public ResourceLocation getModel(ItemStack crystal, ItemStack staffStack, ModularStaff staff) {
        return ResourceUtil.getModelResource("staff_head_"+modelName);
    }

    @Override
    public Color getColor(ItemStack crystal, ItemStack staffStack, ModularStaff staff) {
        return Color.WHITE;
    }

    @Override
    public Spell modifySpell(Spell spell, ItemStack crystal, ItemStack staffStack, ModularStaff staff) {
        return modifier.ModifySpell(spell);
    }

    @Override
    public void addBonusesTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> components) {
        if(bonusMeleeDamage != 0){
            components.add(Component.literal("Bonus melee damage: "+bonusMeleeDamage));
        }
        modifier.addTooltip(components);
    }

    @Override
    public void addShortTooltip(ItemStack component, @Nullable Level worldIn, List<Component> tooltip) {
        modifier.addTooltip(tooltip);
    }

    @Override
    public double getDamageBonus(EquipmentSlot slot, ItemStack stack) {
        return bonusMeleeDamage;
    }

    @Override
    public void addExtraAttributes(EquipmentSlot slot, ItemStack stack, ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes) {
        /*
        if(slot == EquipmentSlot.MAINHAND) {
            attributes.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("246627c8-d8d9-454a-9118-a211d688a436"),
                    "staff head", bonusMeleeDamage, AttributeModifier.Operation.ADDITION));
        }
        */
    }

    @Override
    public int getExtraUpgrades() {
        return 0;
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
            private BlockEntityWithoutLevelRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if(renderer == null){
                    renderer = new ColoredItemRenderer<>("staff_head_"+modelName, "staff_head_"+textureName);
                }
                return renderer;
            }
        });
    }
}
