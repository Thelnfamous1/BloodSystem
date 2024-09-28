package me.Thelnfamous1.blood_system.common.recipe;

import com.google.common.collect.Lists;
import me.Thelnfamous1.blood_system.common.registries.ModItems;
import me.Thelnfamous1.blood_system.common.registries.ModRecipeSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class BloodBagAndNeedleRecipe extends CustomRecipe {
    public BloodBagAndNeedleRecipe(ResourceLocation pId) {
        super(pId);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        List<ItemStack> containedItems = Lists.newArrayList();

        for(int slot = 0; slot < pContainer.getContainerSize(); ++slot) {
            ItemStack itemInSlot = pContainer.getItem(slot);
            if (!itemInSlot.isEmpty()) {
                containedItems.add(itemInSlot);
                if (containedItems.size() > 1) {
                    ItemStack previousItem = containedItems.get(0);
                    if (!isBloodBagAndNeedle(previousItem, itemInSlot) && !isBloodBagAndNeedle(itemInSlot, previousItem)) {
                        return false;
                    }
                }
            }
        }

        return containedItems.size() == 2;
    }

    private static boolean isBloodBagAndNeedle(ItemStack first, ItemStack second){
        return first.is(ModItems.BLOOD_BAG.get()) && second.is(ModItems.NEEDLE.get());
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer) {
        ItemStack bloodBag = ItemStack.EMPTY;

        for(int i = 0; i < pContainer.getContainerSize(); ++i) {
            ItemStack itemInSlot = pContainer.getItem(i);
            if (!itemInSlot.isEmpty() && itemInSlot.is(ModItems.BLOOD_BAG.get())) {
                bloodBag = itemInSlot;
                break;
            }
        }

        ItemStack bloodBagAndNeedle = ModItems.BLOOD_BAG_AND_NEEDLE.get().getDefaultInstance();
        CompoundTag tag = bloodBag.getTag();
        if(tag != null){
            bloodBagAndNeedle.setTag(tag.copy());
        }
        return bloodBagAndNeedle;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BLOOD_BAG_AND_NEEDLE.get();
    }
}
