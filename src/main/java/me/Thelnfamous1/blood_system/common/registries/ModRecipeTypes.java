package me.Thelnfamous1.blood_system.common.registries;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.recipe.BloodAnalysisRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, BloodSystemMod.MODID);

    public static final RegistryObject<RecipeType<BloodAnalysisRecipe>> BLOOD_ANALYSIS = RECIPE_TYPES.register("blood_analysis", () -> createRecipeType(BloodSystemMod.location("blood_analysis").toString()));

    private static RecipeType<BloodAnalysisRecipe> createRecipeType(final String id) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return id;
            }
        };
    }
}
