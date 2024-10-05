package me.Thelnfamous1.blood_system.common.menu;

import me.Thelnfamous1.blood_system.common.block.entity.AbstractBloodAnalyzerBlockEntity;
import me.Thelnfamous1.blood_system.common.recipe.BloodAnalysisRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

public abstract class AbstractBloodAnalyzerMenu extends AbstractContainerMenu {
   public static final int INGREDIENT_SLOT = 0;
   public static final int FUEL_SLOT = 1;
   public static final int RESULT_SLOT = 2;
   public static final int SLOT_COUNT = 3;
   public static final int DATA_COUNT = 4;
   private static final int INV_SLOT_START = 3;
   private static final int INV_SLOT_END = 30;
   private static final int USE_ROW_SLOT_START = 30;
   private static final int USE_ROW_SLOT_END = 39;
   public static final int ANALYSIS_BAR_HEIGHT = 59;
   public static final int BATTERY_POWER_WIDTH = 24;
   private final Container container;
   private final ContainerData data;
   protected final Level level;
   private final RecipeType<? extends BloodAnalysisRecipe> recipeType;

   protected AbstractBloodAnalyzerMenu(MenuType<?> pMenuType, RecipeType<? extends BloodAnalysisRecipe> pRecipeType, int pContainerId, Inventory pPlayerInventory) {
      this(pMenuType, pRecipeType, pContainerId, pPlayerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
   }

   protected AbstractBloodAnalyzerMenu(MenuType<?> pMenuType, RecipeType<? extends BloodAnalysisRecipe> pRecipeType, int pContainerId, Inventory pPlayerInventory, Container pContainer, ContainerData pData) {
      super(pMenuType, pContainerId);
      this.recipeType = pRecipeType;
      checkContainerSize(pContainer, SLOT_COUNT);
      checkContainerDataCount(pData, DATA_COUNT);
      this.container = pContainer;
      this.data = pData;
      this.level = pPlayerInventory.player.level;
      this.addSlot(new Slot(pContainer, INGREDIENT_SLOT, 79, 17));
      this.addSlot(new BloodAnalyzerBatterySlot(this, pContainer, FUEL_SLOT, 17, 17));
      this.addSlot(new BloodAnalyzerResultSlot(pPlayerInventory.player, pContainer, RESULT_SLOT, 79, 58));

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
      }

      this.addDataSlots(pData);
   }

   /**
    * Determines whether supplied player can use this container
    */
   @Override
   public boolean stillValid(Player pPlayer) {
      return this.container.stillValid(pPlayer);
   }

   /**
    * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
    * inventory and the other inventory(s).
    */
   @Override
   public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
      ItemStack quickMoveResult = ItemStack.EMPTY;
      Slot slot = this.slots.get(pIndex);
      if (slot != null && slot.hasItem()) {
         ItemStack itemInSlot = slot.getItem();
         quickMoveResult = itemInSlot.copy();
         if (pIndex == 2) {
            if (!this.moveItemStackTo(itemInSlot, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemInSlot, quickMoveResult);
         } else if (pIndex != 1 && pIndex != 0) {
            if (this.canSmelt(itemInSlot)) {
               if (!this.moveItemStackTo(itemInSlot, INGREDIENT_SLOT, FUEL_SLOT, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.isFuel(itemInSlot)) {
               if (!this.moveItemStackTo(itemInSlot, FUEL_SLOT, RESULT_SLOT, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (pIndex >= INV_SLOT_START && pIndex < INV_SLOT_END) {
               if (!this.moveItemStackTo(itemInSlot, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (pIndex >= USE_ROW_SLOT_START && pIndex < USE_ROW_SLOT_END && !this.moveItemStackTo(itemInSlot, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemInSlot, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
            return ItemStack.EMPTY;
         }

         if (itemInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemInSlot.getCount() == quickMoveResult.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(pPlayer, itemInSlot);
      }

      return quickMoveResult;
   }

   protected boolean canSmelt(ItemStack pStack) {
      return this.level.getRecipeManager().getRecipeFor((RecipeType<BloodAnalysisRecipe>)this.recipeType, new SimpleContainer(pStack), this.level).isPresent();
   }

   protected boolean isFuel(ItemStack pStack) {
      return ForgeHooks.getBurnTime(pStack, this.recipeType) > 0;
   }

   public int getBurnProgress() {
      int cookingProgress = this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_COOKING_PROGRESS);
      int cookingTotalTime = this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_COOKING_TOTAL_TIME);
      return cookingTotalTime != 0 && cookingProgress != 0 ? cookingProgress * ANALYSIS_BAR_HEIGHT / cookingTotalTime : 0;
   }

   public int getLitProgress() {
      int litDuration = this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_LIT_DURATION);
      if (litDuration == 0) {
         litDuration = AbstractBloodAnalyzerBlockEntity.BURN_TIME_STANDARD;
      }

      return this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_LIT_TIME) * BATTERY_POWER_WIDTH / litDuration;
   }

   public boolean isLit() {
      return this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_LIT_TIME) > 0;
   }
}