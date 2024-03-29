package com.dkmk100.arsomega.crafting;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class StaffWithCrystalRecipe extends ShapedRecipe {
    Logger logger = LogManager.getLogger();
    public StaffWithCrystalRecipe(ResourceLocation location, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack output) {
        super(location, group, width, height, ingredients, output);

        logger.info("staff with crystal recipe: "+location.toString());
        logger.info("output: " + output.toString() + "" + output.getTag().toString());

    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RegistryHandler.STAFF_CRYSTAL_RECIPE_SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<StaffWithCrystalRecipe> {
        Logger logger = LogManager.getLogger();
        ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();
        @Override
        public StaffWithCrystalRecipe fromJson(ResourceLocation name, JsonObject json) {
            ShapedRecipe base = serializer.fromJson(name, json);

            ItemStack crystal = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "crystal"));

            ItemStack newOutput = base.getResultItem().copy();
            CompoundTag crystalTag = new CompoundTag();
            crystal.save(crystalTag);
            newOutput.getOrCreateTag().put("crystal", crystalTag);

            logger.info("staff crystal recipe from json");
            return new StaffWithCrystalRecipe(name, base.getGroup(), base.getWidth(), base.getHeight(), base.getIngredients(), newOutput);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, StaffWithCrystalRecipe recipe) {
            serializer.toNetwork(buffer, recipe);
            logger.info("staff crystal recipe to network");
        }

        @Override
        public StaffWithCrystalRecipe fromNetwork(ResourceLocation name, FriendlyByteBuf buffer) {
            ShapedRecipe base = serializer.fromNetwork(name, buffer);

            logger.info("staff crystal recipe from network");
            return new StaffWithCrystalRecipe(name, base.getGroup(), base.getWidth(), base.getHeight(), base.getIngredients(), base.getResultItem());
        }
    }
}
