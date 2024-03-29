package com.dkmk100.arsomega.crafting;

import com.dkmk100.arsomega.items.IStaffPart;
import com.dkmk100.arsomega.items.ModularStaff;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: add a way to upgrade staffs
public class StaffUpgradeRecipe extends ShapedRecipe {
    public StaffUpgradeRecipe(ResourceLocation location, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack output) {
        super(location, group, width, height, ingredients, output);
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack toUpgrade = ItemStack.EMPTY;

        for(int i = 0; i < container.getContainerSize() && toUpgrade.isEmpty(); ++i) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof ModularStaff || stack.getItem() instanceof IStaffPart) {
                toUpgrade = stack;
            }
        }

        ItemStack result = this.getResultItem().copy();
        result.setTag(toUpgrade.getOrCreateTag());
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RegistryHandler.STAFF_UPGRADE_RECIPE_SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<StaffUpgradeRecipe> {
        public Serializer(){

        }

        ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();
        @Override
        public StaffUpgradeRecipe fromJson(ResourceLocation name, JsonObject json) {

            ShapedRecipe base = serializer.fromJson(name, json);

            return new StaffUpgradeRecipe(name, base.getGroup(), base.getWidth(), base.getHeight(), base.getIngredients(), base.getResultItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, StaffUpgradeRecipe recipe) {
            serializer.toNetwork(buffer, recipe);
        }

        @Override
        public StaffUpgradeRecipe fromNetwork(ResourceLocation name, FriendlyByteBuf buffer) {
            ShapedRecipe base = serializer.fromNetwork(name, buffer);

            return new StaffUpgradeRecipe(name, base.getGroup(), base.getWidth(), base.getHeight(), base.getIngredients(), base.getResultItem());
        }
    }
}
