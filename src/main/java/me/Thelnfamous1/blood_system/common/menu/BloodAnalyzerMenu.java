package me.Thelnfamous1.blood_system.common.menu;

import me.Thelnfamous1.blood_system.common.block.entity.BloodAnalyzerBlockEntity;
import me.Thelnfamous1.blood_system.common.registries.ModMenuTypes;
import me.Thelnfamous1.blood_system.common.registries.ModRecipeTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

public class BloodAnalyzerMenu extends AbstractBloodAnalyzerMenu {
   public BloodAnalyzerMenu(int pContainerId, Inventory pPlayerInventory) {
      super(ModMenuTypes.BLOOD_ANALYZER.get(), ModRecipeTypes.BLOOD_ANALYSIS.get(), pContainerId, pPlayerInventory);
   }

   public BloodAnalyzerMenu(int pContainerId, Inventory pPlayerInventory, Container pFurnaceContainer, ContainerData pFurnaceData) {
      super(ModMenuTypes.BLOOD_ANALYZER.get(), ModRecipeTypes.BLOOD_ANALYSIS.get(), pContainerId, pPlayerInventory, pFurnaceContainer, pFurnaceData);
   }

   @Override
   protected int getMinimumChargeToStartAnalysis() {
      return BloodAnalyzerBlockEntity.MIN_CHARGE_TO_START;
   }
}