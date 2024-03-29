package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.util.ResourceUtil;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;

public interface IStaffPart extends IStaffComponent{
    @Override
    default ModularStaff.StaffComponentType getType(ItemStack component){
        return ModularStaff.StaffComponentType.PART;
    }

    ResourceLocation getModel(ItemStack component, ItemStack staffStack, ModularStaff staffItem);
    Color getColor(ItemStack component, ItemStack staffStack, ModularStaff staffItem);

    ResourceLocation getTexture(ItemStack component, ItemStack staffStack, ModularStaff staffItem);

    boolean hasCustomColor(ItemStack stack);

    ModularStaff.StaffPart getPartType();


    default boolean useStaffColor(ItemStack stack){return false;}

    void addExtraAttributes(EquipmentSlot slot, ItemStack stack, ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes);

    int getExtraUpgrades();

    public class StaffPartVisuals {
        int defaultColor;
        float dyeBleed;
        String path = null;

        String uncoloredPath = null;

        public StaffPartVisuals(String path){
            defaultColor = 0xffffffff;
            dyeBleed = 0f;
            this.path = path;
            this.uncoloredPath = null;
        }

        public StaffPartVisuals(String path, int defaultColor, float colorBleed){
            this.defaultColor = defaultColor;
            this.path = path;
            this.dyeBleed =colorBleed;
            this.uncoloredPath = null;
        }
        public StaffPartVisuals(String path, String uncoloredPath){
            this.defaultColor = 0xffffffff;
            this.path = path;
            this.uncoloredPath = uncoloredPath;
        }

        ResourceLocation getModel(ModularStaff.StaffPartWrapper part, ItemStack staffStack, ModularStaff stafft){
            if(uncoloredPath != null && part.part().hasCustomColor(part.stack())){
                return ResourceUtil.getModelResource(uncoloredPath);
            }
            return ResourceUtil.getModelResource(path);
        }

        static StaffPartVisuals base = new StaffPartVisuals("staff_base");
        static StaffPartVisuals gem = new StaffPartVisuals("staff_gem");
        static StaffPartVisuals head = new StaffPartVisuals("staff_head");
    }
}
