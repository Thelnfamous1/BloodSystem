package me.Thelnfamous1.blood_system.common.recipe;

import me.Thelnfamous1.blood_system.common.block.entity.AbstractBloodAnalyzerBlockEntity;
import me.Thelnfamous1.blood_system.common.item.BloodFillableItem;
import me.Thelnfamous1.blood_system.common.registries.ModRecipeSerializers;
import me.Thelnfamous1.blood_system.common.registries.ModRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class BloodAnalysisRecipe implements Recipe<Container> {
    private final ResourceLocation id;

    public BloodAnalysisRecipe(ResourceLocation pId) {
        this.id = pId;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(Container pInv, Level pLevel) {
        ItemStack input = pInv.getItem(AbstractBloodAnalyzerBlockEntity.SLOT_INPUT);
        return input.getItem() instanceof BloodFillableItem
                && BloodFillableItem.getStoredBloodType(input).isPresent()
                && !BloodFillableItem.isAnalyzed(input);
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack assemble(Container pInv) {
        ItemStack inputCopy = pInv.getItem(AbstractBloodAnalyzerBlockEntity.SLOT_INPUT).copy();
        inputCopy.setCount(1);
        BloodFillableItem.setAnalyzed(inputCopy, true);
        return inputCopy;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BLOOD_ANALYSIS.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BLOOD_ANALYSIS.get();
    }
}
