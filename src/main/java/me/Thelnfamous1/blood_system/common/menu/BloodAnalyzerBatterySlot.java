package me.Thelnfamous1.blood_system.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BloodAnalyzerBatterySlot extends Slot {
   private final AbstractBloodAnalyzerMenu menu;

   public BloodAnalyzerBatterySlot(AbstractBloodAnalyzerMenu pFurnaceMenu, Container pFurnaceContainer, int pSlot, int pXPosition, int pYPosition) {
      super(pFurnaceContainer, pSlot, pXPosition, pYPosition);
      this.menu = pFurnaceMenu;
   }

   /**
    * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
    */
   @Override
   public boolean mayPlace(ItemStack pStack) {
      return this.menu.isBattery(pStack);
   }
}