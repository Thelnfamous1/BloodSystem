package me.Thelnfamous1.blood_system.common.registries;

import me.Thelnfamous1.blood_system.BloodSystemMod;
import me.Thelnfamous1.blood_system.common.recipe.BloodBagAndNeedleRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BloodSystemMod.MODID);
    public static final RegistryObject<SimpleRecipeSerializer<BloodBagAndNeedleRecipe>> BLOOD_BAG_AND_NEEDLE = RECIPE_SERIALIZERS.register("crafting_special_bloodbagandneedle", () -> new SimpleRecipeSerializer<>(BloodBagAndNeedleRecipe::new));
}
