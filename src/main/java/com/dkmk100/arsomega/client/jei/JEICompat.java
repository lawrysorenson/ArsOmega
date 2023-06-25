package com.dkmk100.arsomega.client.jei;

/*
import com.dkmk100.arsomega.ArsOmega;

import com.dkmk100.arsomega.crafting.ConjuringRecipe;
import com.dkmk100.arsomega.crafting.EnchantRecipe;
import com.dkmk100.arsomega.crafting.TransmuteRecipe;
import com.dkmk100.arsomega.glyphs.EnchantGlyph;
import com.dkmk100.arsomega.glyphs.TransmuteGlyph;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
 
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEICompat implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        final IJeiHelpers helpers = registration.getJeiHelpers();
        final IGuiHelper guiHelper = helpers.getGuiHelper();

        registration.addRecipeCategories(new TransmuteRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ConjuringRecipeCategory(guiHelper));
        registration.addRecipeCategories(new TributeRecipeCategory(guiHelper));
        registration.addRecipeCategories(new EnchantRecipeCategory(guiHelper));

        IModPlugin.super.registerCategories(registration);
    }

    public void registerRecipes(IRecipeRegistration registry) {
        //transmute recipes, make a reversed duplicated version for all reversible ones. Easiest solution.
        List<TransmuteRecipe> transmuteRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RegistryHandler.TRANSMUTE_TYPE);
        ArrayList<TransmuteRecipe> finalRecipes = new ArrayList<>();
        for(TransmuteRecipe recipe : transmuteRecipes){
            finalRecipes.add(new TransmuteRecipe(recipe.getId(),recipe.input,recipe.output,false, recipe.minAmp));
            if(recipe.reversible) {
                finalRecipes.add(new TransmuteRecipe(recipe.getId(), recipe.output, recipe.input, false, recipe.minAmp));
            }
        }
        registry.addRecipes(finalRecipes, TransmuteRecipeCategory.UID);

        //enchant recipes
        List<EnchantRecipe> enchantRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RegistryHandler.ENCHANT_TYPE);
        registry.addRecipes(enchantRecipes, EnchantRecipeCategory.UID);


        //conjuring recipes
        ArrayList<ConjuringRecipe> conjuring = new ArrayList<>();
        conjuring.add(new ConjuringRecipe(new ItemStack(RegistryHandler.DEMON_GEM.get())));
        conjuring.add(new ConjuringRecipe(new ItemStack(RegistryHandler.GORGON_GEM.get())));

        registry.addRecipes(conjuring, ConjuringRecipeCategory.UID);

        //tribute recipes
        ArrayList<ConjuringRecipe> tribute = new ArrayList<>();
        tribute.add(new ConjuringRecipe(new ItemStack(com.hollingsworth.arsnouveau.setup.ItemsRegistry.WILDEN_TRIBUTE)));

        registry.addRecipes(tribute, TributeRecipeCategory.UID);

        //I'm gonna add info here later
        //registry.addIngredientInfo(new ItemStack(ArsNouveauAPI.getInstance().getRitualItemMap().get("tribute")), VanillaTypes.ITEM,   Component.literal(""));
    }

    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(TransmuteGlyph.INSTANCE)), new ResourceLocation[]{TransmuteRecipeCategory.UID});
        registry.addRecipeCatalyst(new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(EnchantGlyph.INSTANCE)), new ResourceLocation[]{EnchantRecipeCategory.UID});
        registry.addRecipeCatalyst(new ItemStack(ArsNouveauAPI.getInstance().getRitualItemMap().get("conjuring")), new ResourceLocation[]{ConjuringRecipeCategory.UID});
        registry.addRecipeCatalyst(new ItemStack(ArsNouveauAPI.getInstance().getRitualItemMap().get("tribute")), new ResourceLocation[]{TributeRecipeCategory.UID});

    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("arsomega", "main");
    }
}

 */
