package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.staff.StaffAnimationController;
import com.dkmk100.arsomega.client.staff.StaffModel;
import com.dkmk100.arsomega.client.staff.StaffRenderer;
import com.dkmk100.arsomega.util.ConfigHandler;
import com.dkmk100.arsomega.util.ResourceUtil;
import com.dkmk100.arsomega.util.StatsModifier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.perk.RepairingPerk;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.dkmk100.arsomega.items.Staff.*;

public class ModularStaff extends SwordItem implements IAnimatable, ICasterTool, DyeableLeatherItem {
    public AnimationFactory factory = new AnimationFactory(this);
    StatsModifier statsModifier;
    boolean fireImmune;
    int maxUpgrades;

    int tier;

    public ModularStaff(Tier iItemTier, int baseDamage, float baseAttackSpeed, StatsModifier mod, int tier, int maxUpgrades, boolean fireImmune) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsOmega.itemGroup));
        statsModifier = mod;
        this.maxUpgrades = maxUpgrades;
        this.fireImmune = fireImmune;
        this.tier = tier;
    }

    //compat with repairing thread
    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
        if(entity instanceof Player player)
            RepairingPerk.attemptRepair(stack, player);
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        AnimationController<?> controller = event.getController();
        StaffAnimationController<?> staffController = (StaffAnimationController<?>) controller;

        AnimationBuilder builder = (new AnimationBuilder()).addAnimation("idle", true);
        staffController.setAnimation(builder,event);

        return PlayState.CONTINUE;
    }

    @Override
    public boolean isFireResistant() {
        return fireImmune;
    }

    public enum StaffPart {
        BASE, HEAD, GEM;
    }
    public enum StaffComponentType{
        PART, UPGRADE, MISC
    }

    static String getPartName(StaffPart part){
        String str;
        switch (part){
            case BASE -> str = "base";
            case GEM -> str = "gem";
            case HEAD -> str = "head";
            default -> str = "part";
        }
        return str;
    }

    boolean canAcceptPart(StaffPart part){
        return part == StaffPart.GEM || part == StaffPart.HEAD;
    }
    String getPartNBTName(StaffPart part){
        String str;
        switch (part){
            case GEM -> str = "crystal";
            case HEAD -> str = "head";
            default -> str = null;
        }
        return str;
    }

    List<String> getPartNames(){
        return List.of("crystal", "head");
    }

    //TODO: make this better
    List<StaffPartWrapper> getParts(ItemStack staff){
        List<StaffPartWrapper> parts = new ArrayList<>();
        for(String target : getPartNames()){
            ItemStack partStack = getPart(staff,target);
            if(!partStack.isEmpty() && partStack.getItem() instanceof IStaffPart){
                parts.add(new StaffPartWrapper(partStack));
            }
        }
        return parts;
    }

    List<StaffUpgradeWrapper> getUpgrades(ItemStack staff) {

        if (staff.hasTag() && staff.getTag().contains("upgradeCount")) {
            List<StaffUpgradeWrapper> upgrades = new ArrayList<>();
            int upgradeCount = staff.getTag().getInt("upgradeCount");

            for(int i=0;i<upgradeCount;i++) {
                ItemStack upgrade = getUpgrade(staff, i);
                if (!upgrade.isEmpty() && upgrade.getItem() instanceof IStaffUpgrade) {
                    upgrades.add(new StaffUpgradeWrapper(upgrade));
                }
            }
            return upgrades;
        }

        return List.of();
    }

    record StaffUpgradeWrapper(ItemStack stack, Item item, IStaffComponent component, IStaffUpgrade upgrade) {
        public StaffUpgradeWrapper(ItemStack stack) {
            this(stack, stack.getItem(), (IStaffComponent) stack.getItem(), (IStaffUpgrade) stack.getItem());
        }
    }

    record StaffPartWrapper(ItemStack stack, Item item, IStaffComponent component, IStaffPart part) {
        public StaffPartWrapper(ItemStack stack) {
            this(stack, stack.getItem(), (IStaffComponent) stack.getItem(), (IStaffPart) stack.getItem());
        }
    }

    ResourceLocation getDefaultModel(StaffPart part){
        String str;
        switch (part){
            case BASE -> str = "staff_base";
            case HEAD -> str = "staff_head";
            default -> str = "empty";
        }
        return ResourceUtil.getModelResource(str);
    }

    ResourceLocation getDefaultTexture(StaffPart part){
        String str;
        switch (part){
            case GEM -> str = "staff_gem";
            case HEAD -> str = "staff_head";
            default -> str = "staff_base";
        }
        return ResourceUtil.getItemTextureResource(str);
    }

    public ResourceLocation getModel(ItemStack stack, StaffPart part){
        ResourceLocation target = null;
        if(canAcceptPart(part)){
            ItemStack partStack = getPart(stack, getPartNBTName(part));
            if(!partStack.isEmpty() && partStack.getItem() instanceof IStaffPart staffPart){
                target = staffPart.getModel(partStack, stack, this);

            }
        }

        if(target == null){
            target = getDefaultModel(part);
        }

        return target;
    }

    public ResourceLocation getTexture(ItemStack stack, StaffPart part){
        ResourceLocation target = null;

        if(canAcceptPart(part)){
            ItemStack partStack = getPart(stack, getPartNBTName(part));
            if(!partStack.isEmpty() && partStack.getItem() instanceof IStaffPart staffPart){
                target = staffPart.getTexture(partStack, stack, this);
            }
        }

        if(target == null){
            target = getDefaultTexture(part);
        }

        return target;
    }

    public ResourceLocation getAnimation(ItemStack stack, StaffPart part){
        return ResourceUtil.getAnimationResource("staff_"+ getPartName(part));
    }

    ItemStack getPart(ItemStack stack, String nameNBT){
        if(stack.hasTag() && stack.getTag().contains(nameNBT)){
            return ItemStack.of(stack.getTag().getCompound(nameNBT));
        } else{
            return ItemStack.EMPTY;
        }
    }

    void setPartNBT(ItemStack stack, ItemStack part, String nameNBT){
        if(part == null || part.isEmpty()){
            if(stack.hasTag() && stack.getTag().contains(nameNBT)){
                stack.getTag().remove(nameNBT);
            }
        } else{
            CompoundTag compound = new CompoundTag();
            part.save(compound);
            stack.getOrCreateTag().put(nameNBT,compound);
        }
    }

    public void removeAndDropPart(ItemStack stack, String nameNBT, Level world, BlockPos pos, @Nullable Player player){
        ItemStack crystal = getPart(stack, nameNBT);
        if(crystal != null){
            if(player!=null && player.addItem(crystal)){
                //added item in if statement lol
            }
            else {
                //spawn item in world
                ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), crystal);
                world.addFreshEntity(entity);
            }

            //remove crystal from staff
            setPartNBT(stack, ItemStack.EMPTY, nameNBT);
            this.updateUpgrades(stack, world, pos, player);
        }
    }

    protected void setPart(ItemStack staff, ItemStack crystal, String nameNBT, Level world, BlockPos pos, @Nullable Player player){
        //remove and give back old crystal
        removeAndDropPart(staff, nameNBT, world, pos, player);
        //new crystal
        ItemStack copy = crystal.copy();
        copy.setCount(1);
        setPartNBT(staff,copy,nameNBT);
        this.updateUpgrades(staff, world, pos, player);
        //remove original crystal
        crystal.shrink(1);
    }

    ItemStack getUpgrade(ItemStack stack, int id){
        String name = "upgrade"+id;
        if(stack.hasTag() && stack.getTag().contains(name)){
            return ItemStack.of(stack.getTag().getCompound(name));
        } else{
            return ItemStack.EMPTY;
        }
    }

    boolean hasUpgrades(ItemStack staff){
        return staff.hasTag() && staff.getTag().contains("upgradeCount") && staff.getTag().getInt("upgradeCount") > 0;
    }

    void updateUpgrades(ItemStack staff, Level world, BlockPos pos, @Nullable Player player){
        if(staff.hasTag() && staff.getTag().contains("upgradeCount")) {
            int upgradeCount = staff.getTag().getInt("upgradeCount");

            int maxUpgrades = getMaxUpgrades(staff);

            //drop extra upgrades
            for (int i = maxUpgrades; i < upgradeCount; i++){
                ItemStack upgrade = getUpgrade(staff, i);
                if(!upgrade.isEmpty()){
                    if(player!=null && player.addItem(upgrade)){
                        //added item in if statement lol
                    }
                    else {
                        ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), upgrade);
                        world.addFreshEntity(entity);
                    }
                }
                staff.getTag().remove("upgrade"+i);
            }

            staff.getTag().putInt("upgradeCount", Math.min(maxUpgrades, upgradeCount));
        }
    }

    void removeAndDropUpgrades(ItemStack staff, Level world, BlockPos pos, @Nullable Player player){
        if(staff.hasTag() && staff.getTag().contains("upgradeCount")) {
            int upgradeCount = staff.getTag().getInt("upgradeCount");
            for (int i = 0; i < upgradeCount; i++){
                ItemStack upgrade = getUpgrade(staff, i);
                if(!upgrade.isEmpty()){
                    if(player!=null && player.addItem(upgrade)){
                        //added item in if statement lol
                    }
                    else {
                        ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), upgrade);
                        world.addFreshEntity(entity);
                    }
                }
                staff.getTag().remove("upgrade"+i);
            }
            staff.getTag().putInt("upgradeCount",0);
        }
    }

    public int getMaxUpgrades(ItemStack staff){
        int max = maxUpgrades;
        for(var part : getParts(staff)){
            max += part.part.getExtraUpgrades();
        }
        return max;
    }

    public boolean tryAddUpgrade(ItemStack staff, ItemStack stack, IStaffUpgrade component, Level world, BlockPos pos, @Nullable Player player) {
        int upgradeCount = staff.getTag().getInt("upgradeCount");

        if (upgradeCount >= getMaxUpgrades(staff)) {
            PortUtil.sendMessageNoSpam(player, Component.literal("Staff has max number of upgrades already!"));
            return false;
        }

        String name = "upgrade"+upgradeCount;

        int existingCount = 0;
        for(var upgrade : getUpgrades(staff)){
            if(upgrade.stack.is(stack.getItem())){
                existingCount += upgrade.stack.getCount();
            }
        }

        if(existingCount >= component.getMaxCount()){
            PortUtil.sendMessageNoSpam(player, Component.literal("That upgrade can only be applied "+ component.getMaxCount() + " times!"));
            return false;
        }

        CompoundTag upgradeTag = new CompoundTag();

        ItemStack copy = stack.copy();
        copy.setCount(1);
        copy.save(upgradeTag);

        stack.shrink(1);

        staff.getTag().put(name, upgradeTag);
        staff.getTag().putInt("upgradeCount", upgradeCount + 1);
        return true;
    }

    public boolean tryAddComponent(ItemStack staff, ItemStack stack, IStaffComponent component, Level world, BlockPos pos, @Nullable Player player){
        if(component instanceof IStaffPart part){
            if(canAcceptPart(part.getPartType())){
                setPart(staff, stack, getPartNBTName(part.getPartType()), world, pos, player);
                return true;
            }
        }
        if(component instanceof IStaffUpgrade upgrade) {
            return tryAddUpgrade(staff, stack, upgrade, world, pos, player);
        }

        return false;
    }

    Color getDefaultColor(ItemStack stack, StaffPart part){
        if(part == StaffPart.GEM){
            ParticleColor color = this.getSpellCaster(stack).getSpell().color;
            return Color.ofOpaque(color.getColor());
        } else{
            int color = getColor(stack);
            return Color.ofOpaque(color);
        }
    }

    public Color getColor(ItemStack stack, StaffPart part){
        if(canAcceptPart(part)) {
            ItemStack partStack = getPart(stack, getPartNBTName(part));
            if (!partStack.isEmpty() && partStack.getItem() instanceof IStaffPart staffPart) {
                return staffPart.getColor(partStack, stack, this);
            } else {
                return getDefaultColor(stack, part);
            }
        } else {
            int color = getColor(stack);
            return Color.ofOpaque(color);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = new ImmutableMultimap.Builder<>();

        Multimap<Attribute, AttributeModifier> defaults = super.getAttributeModifiers(slot, stack);

        //TODO: add get components method
        int extraDamage = 0;
        for(var part : getParts(stack)){
           extraDamage += part.part().getDamageBonus(slot, part.stack);
        }

        for(var upgrade : getUpgrades(stack)){
            extraDamage += upgrade.upgrade.getDamageBonus(slot, upgrade.stack);
        }

        for(var x : defaults.entries()) {
            if(x.getKey() == Attributes.ATTACK_DAMAGE && x.getValue().getId() == BASE_ATTACK_DAMAGE_UUID){
                attributes.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                        x.getValue().getAmount() + extraDamage, AttributeModifier.Operation.ADDITION));
            } else{
                attributes.put(x);
            }
        }

        for(var part : getParts(stack)){
            part.part().addExtraAttributes(slot, part.stack, attributes);
        }

        return attributes.build();
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack tableStack) {
        ItemStack inHand = player.getItemInHand(handIn);
        if(inHand.getItem() instanceof IStaffComponent component){
            return tryAddComponent(tableStack, inHand, component, world, pos, player);
        }
        else if(inHand.getItem() instanceof ShearsItem){
            if(hasUpgrades(tableStack)){
                removeAndDropUpgrades(tableStack,world,pos, player);
            }
            else{
                for(var name : getPartNames()) {
                    removeAndDropPart(tableStack, name, world, pos, player);
                }
            }
            return true;
        }
        else {
            return ICasterTool.super.onScribe(world, pos, player, handIn, tableStack);
        }
    }

    public void dropAllContents(ItemStack staff, Level world, BlockPos pos, @Nullable Player player) {
        if (hasUpgrades(staff)) {
            removeAndDropUpgrades(staff, world, pos, player);
        }
        for (var name : getPartNames()) {
            removeAndDropPart(staff, name, world, pos, player);
        }
    }

    //we override to change the default color
    @Override
    public int getColor(ItemStack p_41122_) {
        int defaultColor = Color.WHITE.getColor();
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : defaultColor;
    }

    Spell modifySpell(Spell spell, ItemStack staff){
        //clone stats modifier
        StatsModifier modifier = new StatsModifier(this.statsModifier);

        for(var part : getParts(staff)) {
                part.part.modifySpell(spell, part.stack, staff, this);
        }

        for(var upgrade : getUpgrades(staff)){
                upgrade.component.modifySpell(spell, upgrade.stack, staff, this);
        }

        return modifier.ModifySpell(spell);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (worldIn.isClientSide)
            return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));

        ItemStack stack = playerIn.getItemInHand(handIn);

        ISpellCaster caster = this.getSpellCaster(stack);

        Spell spell = caster.getSpell();

        //TODO: replace this with correct use of spell caster
        spell = this.modifySpell(spell, stack);

        IWrappedCaster wrappedCaster = new PlayerCaster(playerIn);

        SpellContext context = new SpellContext(worldIn, spell,playerIn, wrappedCaster, stack);
        SpellResolver resolver = new SpellResolver(context);

        if(spell.isEmpty() || !(spell.recipe.get(0) instanceof AbstractCastMethod)){
            PortUtil.sendMessageNoSpam(playerIn, Component.literal("No spell"));
            return new InteractionResultHolder<>(InteractionResult.PASS,stack);
        }


        ItemStack crystal = getPart(stack, getPartNBTName(StaffPart.GEM));
        if(crystal.isEmpty()){
            PortUtil.sendMessageNoSpam(playerIn, Component.literal("Cannot cast spell without a staff crystal. "));
            return new InteractionResultHolder<>(InteractionResult.PASS,stack);
        }

        boolean isSensitive = resolver.spell.getBuffsAtIndex(0, playerIn, AugmentSensitive.INSTANCE) > 0;
        HitResult result = SpellUtil.rayTrace(playerIn, 0.5 + playerIn.getReachDistance(), 0, isSensitive);
        if (result instanceof BlockHitResult blockHit) {
            BlockEntity tile = worldIn.getBlockEntity(blockHit.getBlockPos());
            if (tile instanceof ScribesTile)
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

            if (!playerIn.isShiftKeyDown() && tile != null && !(worldIn.getBlockState(blockHit.getBlockPos()).is(BlockTagProvider.IGNORE_TILE))) {
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }
        }

        //TODO: see if this can be removed/changed at any point
        try {
            ISpellValidator validator = ArsNouveauAPI.getInstance().getSpellCastingSpellValidator();
            List<SpellValidationError> validationErrors = validator.validate(spell.recipe);
            for(SpellValidationError error : validationErrors){
                if(!(AugmentError.isInstance(error))){
                    PortUtil.sendMessageNoSpam(playerIn, error.makeTextComponentExisting());
                    return new InteractionResultHolder<>(InteractionResult.PASS,stack);
                }
            }

            if((boolean) enoughMana.invoke(resolver,playerIn)) {
                CastResolveType resolveType;
                AbstractCastMethod form = caster.getSpell().getCastMethod();
                if(result != null && result.getType() != HitResult.Type.MISS && result instanceof BlockHitResult blockHit){
                    resolveType = form.onCastOnBlock(blockHit, playerIn, (SpellStats) getStats.invoke(resolver), context, resolver);
                }
                else if(result != null && result.getType() != HitResult.Type.MISS && result instanceof EntityHitResult entityHit){
                    resolveType = form.onCastOnEntity(stack, playerIn, entityHit.getEntity(), handIn, (SpellStats) getStats.invoke(resolver), context, resolver);
                }
                else {
                    resolveType = form.onCast(stack, playerIn, worldIn, (SpellStats) getStats.invoke(resolver), context, resolver);
                }

                if(resolveType == CastResolveType.SUCCESS) {
                    resolver.expendMana();
                }
                if(resolveType == CastResolveType.FAILURE) {
                    return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
                } else {
                    stack.hurtAndBreak(1, playerIn, (t) -> {
                        t.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                    } );
                    return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
                }
            }
            else{
                return new InteractionResultHolder<>(InteractionResult.PASS,stack);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        for(StaffPart part : StaffPart.values()) {
            data.addAnimationController(new StaffAnimationController(this, part.name(), 20.0F, this::predicate, part));
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public boolean isScribedSpellValid(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        /*
        return spell.recipe.stream().noneMatch((s) -> {
            return s instanceof AbstractCastMethod;
        });
         */
        return true;
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.wand.invalid"));
    }

    static class DefaultStaffPart implements IStaffPart{
        StaffPart part;
        DefaultStaffPart(StaffPart part){
            this.part = part;
        }

        DefaultStaffPart BASE = new DefaultStaffPart(StaffPart.BASE);
        DefaultStaffPart HEAD = new DefaultStaffPart(StaffPart.HEAD);
        @Override
        public Spell modifySpell(Spell spell, ItemStack component, ItemStack staffStack, ModularStaff staffItem) {
            return spell;
        }

        @Override
        public void addBonusesTooltip(ItemStack component, @org.jetbrains.annotations.Nullable Level worldIn, List<Component> tooltip) {

        }

        @Override
        public StaffComponentType getType(ItemStack component) {
            return StaffComponentType.PART;
        }

        @Override
        public ResourceLocation getModel(ItemStack component, ItemStack staffStack, ModularStaff staffItem) {
            return ResourceUtil.getModelResource("staff_"+getPartName(part));
        }

        @Override
        public Color getColor(ItemStack component, ItemStack staffStack, ModularStaff staffItem) {
            return Color.ofOpaque(staffItem.getColor(staffStack));
        }

        @Override
        public ResourceLocation getTexture(ItemStack component, ItemStack staffStack, ModularStaff staffItem) {
            return ResourceUtil.getItemTextureResource("staff_"+getPartName(part));
        }

        @Override
        public boolean hasCustomColor(ItemStack stack) {
            CompoundTag compoundtag = stack.getTagElement("display");
            return compoundtag != null && compoundtag.contains("color", 99);
        }

        @Override
        public StaffPart getPartType() {
            return part;
        }

        @Override
        public void addExtraAttributes(EquipmentSlot slot, ItemStack stack, ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes) {

        }

        @Override
        public int getExtraUpgrades() {
            return 0;
        }
    }

    @Override
    public boolean setSpell(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {

        if (ConfigHandler.staffsLimitGlyphTier() && spell.recipe.stream().anyMatch((s) -> s.getConfigTier().value > tier)){
            PortUtil.sendMessageNoSpam(player, Component.literal("A glyph in this spell is a higher tier than the staff supports!"));
            return false;
        }

        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;

        return ICasterTool.super.setSpell(caster, player, hand, stack, spell);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        //tooltip2.add(Component.literal("WARNING: THIS ITEM IS WIP").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));

        this.getInformation(stack, worldIn, tooltip2, flagIn);
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);

        tooltip2.add(Component.literal("Can be dyed. "));
        tooltip2.add(Component.literal("Add upgrades or swap crystal on a scribe's table."));
        tooltip2.add(Component.literal("Remove upgrades and crystal with shears on a scribe's table. "));
        tooltip2.add(Component.literal(""));

        if(!Screen.hasShiftDown()) {
            tooltip2.add(Component.literal("Hold shift for stats. ").withStyle(ChatFormatting.BOLD));
            return;
        }

        tooltip2.add(Component.literal("Tier: "+tier));

        statsModifier.addTooltip(tooltip2);

        ItemStack gem = getPart(stack,getPartNBTName(StaffPart.GEM));
        if(!gem.isEmpty()){
            tooltip2.add(Component.literal("Crystal: ").append(gem.getHoverName()));
            if(gem.getItem() instanceof IStaffPart part) {
                part.addShortTooltip(gem, worldIn, tooltip2);
            }
        }

        ItemStack head = getPart(stack,getPartNBTName(StaffPart.HEAD));
        if(!head.isEmpty()){
            tooltip2.add(Component.literal("Tip: ").append(head.getHoverName()));
            if(head.getItem() instanceof IStaffPart part) {
                part.addShortTooltip(head, worldIn, tooltip2);
            }
        }

        if(hasUpgrades(stack)){
            int upgradeCount = stack.getTag().getInt("upgradeCount");
            tooltip2.add(Component.literal("Upgrades: " + upgradeCount + "/" + getMaxUpgrades(stack)));
            for(var upgrade : getUpgrades(stack)){
                upgrade.component.addShortTooltip(upgrade.stack, worldIn, tooltip2);
            }
        }
        else{
            tooltip2.add(Component.literal("Upgrades: 0/" + getMaxUpgrades(stack)));
        }

    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new StaffRenderer(new StaffModel());

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}

