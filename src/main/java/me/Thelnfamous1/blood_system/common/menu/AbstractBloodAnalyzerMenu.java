package me.Thelnfamous1.blood_system.common.menu;

import me.Thelnfamous1.blood_system.common.block.entity.AbstractBloodAnalyzerBlockEntity;
import me.Thelnfamous1.blood_system.common.recipe.BloodAnalysisRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBloodAnalyzerMenu extends AbstractContainerMenu {
   public static final int INGREDIENT_SLOT = 0;
   public static final int BATTERY_A_SLOT = 1;
   public static final int BATTERY_B_SLOT = 2;
   public static final int RESULT_SLOT = 3;
   public static final int SLOT_COUNT = 4;
   public static final int DATA_COUNT = 5;
   public static final int START_BUTTON_ID = 0;
   private static final int INV_SLOT_START = 4;
   private static final int INV_SLOT_END = 31;
   private static final int USE_ROW_SLOT_START = 31;
   private static final int USE_ROW_SLOT_END = 40;
   public static final int ANALYSIS_BAR_HEIGHT = 59;
   public static final int BATTERY_CHARGE_WIDTH = 25;
   private final Container container;
   private final ContainerData data;
   protected final Level level;
   private final RecipeType<? extends BloodAnalysisRecipe> recipeType;
   private final RecipeManager.CachedCheck<Container, ? extends BloodAnalysisRecipe> quickCheck;

   protected AbstractBloodAnalyzerMenu(MenuType<?> pMenuType, RecipeType<? extends BloodAnalysisRecipe> pRecipeType, int pContainerId, Inventory pPlayerInventory) {
      this(pMenuType, pRecipeType, pContainerId, pPlayerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
   }

   protected AbstractBloodAnalyzerMenu(MenuType<?> pMenuType, RecipeType<? extends BloodAnalysisRecipe> pRecipeType, int pContainerId, Inventory pPlayerInventory, Container pContainer, ContainerData pData) {
      super(pMenuType, pContainerId);
      this.quickCheck = RecipeManager.createCheck((RecipeType)pRecipeType);
      this.recipeType = pRecipeType;
      checkContainerSize(pContainer, SLOT_COUNT);
      checkContainerDataCount(pData, DATA_COUNT);
      this.container = pContainer;
      this.data = pData;
      this.level = pPlayerInventory.player.level;
      this.addSlot(new Slot(pContainer, INGREDIENT_SLOT, 79, 17));
      this.addSlot(new BloodAnalyzerBatterySlot(this, pContainer, BATTERY_A_SLOT, 17, 17));
      this.addSlot(new BloodAnalyzerBatterySlot(this, pContainer, BATTERY_B_SLOT, 17, 38));
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
         if (pIndex == RESULT_SLOT) {
            if (!this.moveItemStackTo(itemInSlot, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemInSlot, quickMoveResult);
         } else if (pIndex != BATTERY_B_SLOT && pIndex != BATTERY_A_SLOT && pIndex != INGREDIENT_SLOT) {
            if (this.canAnalyze(itemInSlot)) {
               if (!this.moveItemStackTo(itemInSlot, INGREDIENT_SLOT, BATTERY_A_SLOT, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.isBattery(itemInSlot)) {
               if (!this.moveItemStackTo(itemInSlot, BATTERY_A_SLOT, RESULT_SLOT, false)) {
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

   protected boolean canAnalyze(ItemStack pStack) {
      return this.level.getRecipeManager().getRecipeFor((RecipeType<BloodAnalysisRecipe>)this.recipeType, new SimpleContainer(pStack), this.level).isPresent();
   }

   protected boolean isBattery(ItemStack pStack) {
      return AbstractBloodAnalyzerBlockEntity.isBattery(pStack);
   }

   public int getAnalysisProgress() {
      int analysisProgress = this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_ANALYSIS_PROGRESS);
      int analysisTotalTime = this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_ANALYSIS_TOTAL_TIME);
      return analysisTotalTime != 0 && analysisProgress != 0 ? analysisProgress * ANALYSIS_BAR_HEIGHT / analysisTotalTime : 0;
   }

   public int getChargeProgressA(){
      return this.getChargeProgress(AbstractBloodAnalyzerBlockEntity.DATA_CHARGE_A);
   }

   public int getChargeProgressB(){
      return this.getChargeProgress(AbstractBloodAnalyzerBlockEntity.DATA_CHARGE_B);
   }

   protected int getChargeProgress(int chargeTimeId) {
      return this.data.get(chargeTimeId) * BATTERY_CHARGE_WIDTH / AbstractBloodAnalyzerBlockEntity.MAX_CHARGE;
   }

   public boolean isCharged() {
      return this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_CHARGE_A) > 0 || this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_CHARGE_B) > 0;
   }

   public boolean hasEnoughChargeToStartAnalysis(){
      return this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_CHARGE_A) + this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_CHARGE_B)
              >= AbstractBloodAnalyzerBlockEntity.MIN_CHARGE_TO_START;
   }

   public boolean isAnalyzing(){
      return this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_ANALYSIS_PROGRESS) > 0;
   }

   @Override
   public boolean clickMenuButton(Player pPlayer, int pId) {
      if(pId == START_BUTTON_ID){
         boolean started = this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_ACTIVATED) > 0;
         this.setData(AbstractBloodAnalyzerBlockEntity.DATA_ACTIVATED, started ? 0 : 1);
         return true;
      }
      return false;
   }

   @Nullable
   public BloodAnalysisRecipe getRecipe(Level level){
      return this.quickCheck.getRecipeFor(this.container, level).orElse(null);
   }

   public boolean isActivated() {
      return this.data.get(AbstractBloodAnalyzerBlockEntity.DATA_ACTIVATED) > 0;
   }
}