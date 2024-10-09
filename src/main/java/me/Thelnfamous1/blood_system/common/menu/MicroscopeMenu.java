package me.Thelnfamous1.blood_system.common.menu;

import me.Thelnfamous1.blood_system.common.block.entity.MicroscopeBlockEntity;
import me.Thelnfamous1.blood_system.common.registries.ModMenuTypes;
import me.Thelnfamous1.blood_system.common.registries.ModRecipeTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

public class MicroscopeMenu extends AbstractBloodAnalyzerMenu {
   public MicroscopeMenu(int pContainerId, Inventory pPlayerInventory) {
      super(ModMenuTypes.MICROSCOPE.get(), ModRecipeTypes.BLOOD_ANALYSIS.get(), pContainerId, pPlayerInventory);
   }

   public MicroscopeMenu(int pContainerId, Inventory pPlayerInventory, Container pFurnaceContainer, ContainerData pFurnaceData) {
      super(ModMenuTypes.MICROSCOPE.get(), ModRecipeTypes.BLOOD_ANALYSIS.get(), pContainerId, pPlayerInventory, pFurnaceContainer, pFurnaceData);
   }

   @Override
   protected int getMinimumChargeToStartAnalysis() {
      return MicroscopeBlockEntity.MIN_CHARGE_TO_START;
   }
}